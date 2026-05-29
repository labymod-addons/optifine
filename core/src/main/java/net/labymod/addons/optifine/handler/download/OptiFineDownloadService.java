/*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package net.labymod.addons.optifine.handler.download;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import net.labymod.addons.optifine.exception.OptiFineException;
import net.labymod.addons.optifine.handler.OptiFineManifest;
import net.labymod.addons.optifine.handler.OptiFineVersion;
import net.labymod.addons.optifine.launch.OptiFineStorage;
import net.labymod.api.models.version.Version;
import net.labymod.api.util.io.IOUtil;

public class OptiFineDownloadService extends DownloadService {

  private static final Gson GSON = new GsonBuilder().create();
  private static final String USER_AGENT = "OptifineHandler";
  private static final int TIMEOUT_MILLIS = 15_000;

  private static final String BASE_URL = "https://optifine.net/";
  private static final String URL_PATH = "adloadx?f=%s.jar";
  private static final String FOLLOW = "((.|\\R)+)(downloadx\\?f=%s\\.jar)([^']+)((.|\\R)+)";

  private OptiFineVersion optiFineVersion;

  @Override
  public void download(Version version) throws OptiFineException {
    String gameVersion = version.toString();

    OptiFineManifest manifest = this.loadManifest();
    this.optiFineVersion = manifest.findVersion(gameVersion);
    if (this.optiFineVersion == null) {
      throw new OptiFineException("No OptiFine version is available for Minecraft " + gameVersion);
    }

    String qualifiedName = this.optiFineVersion.getQualifiedJarName();
    this.optifineJarPath = OptiFineStorage.directory(gameVersion).resolve(qualifiedName + ".jar");

    if (Files.exists(this.optifineJarPath) && !IOUtil.isCorrupted(this.optifineJarPath)) {
      return;
    }

    this.downloadJar(
        String.format(URL_PATH, qualifiedName),
        String.format(FOLLOW, qualifiedName.replace(".", "\\."))
    );
  }

  @Override
  public OptiFineVersion currentOptiFineVersion() {
    return this.optiFineVersion;
  }

  private OptiFineManifest loadManifest() throws OptiFineException {
    URL url = this.getClass().getClassLoader().getResource("assets/optifine/versions.json");
    if (url == null) {
      throw new OptiFineException("OptiFine manifest file not found");
    }

    try (Reader reader = new InputStreamReader(url.openStream())) {
      return GSON.fromJson(reader, OptiFineManifest.class);
    } catch (IOException exception) {
      throw new OptiFineException("Failed to read OptiFine manifest file", exception);
    }
  }

  private void downloadJar(String urlPath, String follow) throws OptiFineException {
    try {
      HttpURLConnection connection = this.prepareConnection(BASE_URL + urlPath);
      int responseCode = connection.getResponseCode();
      if (responseCode / 100 != 2) {
        throw new OptiFineException("OptiFine page request failed with HTTP status " + responseCode);
      }

      String webContent;
      try (InputStream inputStream = connection.getInputStream()) {
        webContent = IOUtil.toString(inputStream);
      }

      String relativeLink = this.extractDownloadLink(webContent, follow);
      if (relativeLink == null) {
        throw new OptiFineException(
            "Failed to locate the OptiFine download link (optifine.net layout changed?)"
        );
      }

      Files.createDirectories(this.optifineJarPath.getParent());
      byte[] jarBytes;
      try (InputStream downloadStream = this.prepareConnection(BASE_URL + relativeLink).getInputStream()) {
        jarBytes = IOUtil.readBytes(downloadStream);
      }
      Files.write(this.optifineJarPath, jarBytes);
    } catch (IOException exception) {
      throw new OptiFineException("Failed to download OptiFine jar", exception);
    }

    if (IOUtil.isCorrupted(this.optifineJarPath)) {
      throw new OptiFineException("Downloaded OptiFine jar is corrupted or not a valid archive");
    }
  }

  private String extractDownloadLink(String webContent, String follow) {
    for (String content : webContent.split(System.lineSeparator())) {
      if (!content.matches(follow)) {
        continue;
      }

      int firstIndex = content.indexOf('\'');
      int secondIndex = content.indexOf('\'', firstIndex + 1);
      if (firstIndex < 0 || secondIndex < 0) {
        continue;
      }

      return content.substring(firstIndex + 1, secondIndex);
    }

    return null;
  }

  private HttpURLConnection prepareConnection(String url) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(Proxy.NO_PROXY);
    connection.setRequestProperty("User-Agent", USER_AGENT);
    connection.setConnectTimeout(TIMEOUT_MILLIS);
    connection.setReadTimeout(TIMEOUT_MILLIS);
    return connection;
  }
}
