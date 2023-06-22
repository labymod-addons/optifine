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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import net.labymod.addons.optifine.handler.OptiFineVersion;
import net.labymod.addons.optifine.handler.OptiFineVersions;
import net.labymod.api.models.version.Version;
import net.labymod.api.util.io.IOUtil;

public class OptiFineDownloadService extends DownloadService {

  private static final String USER_AGENT = "OptifineHandler";

  private static final String BASE_URL = "https://optifine.net/";
  private static final String URL_PATH = "adloadx?f=%s.jar";
  private static final String FOLLOW = "((.|\\R)+)(downloadx\\?f=%s\\.jar)([^']+)((.|\\R)+)";

  private OptiFineVersion optiFineVersion;

  @Override
  public void download(Version version) throws IOException {
    String gameVersion = version.toString();


    this.optiFineVersion = OptiFineVersions.getVersion(gameVersion);

    if (this.optiFineVersion == null) {
      throw new IOException(
          "No Optifine version was found for this specified version " + gameVersion);
    }

    this.optifineJarPath = Paths.get(
        String.format(
            "labymod-neo/optifine/%s/%s.jar",
            gameVersion,
            this.optiFineVersion.getQualifiedJarName()
        )
    );

    gameVersion = this.optiFineVersion.getQualifiedJarName();

    this.download(
        String.format(
            URL_PATH,
            gameVersion
        ),
        String.format(
            FOLLOW,
            gameVersion.replace(".", "\\.")
        )
    );
  }

  @Override
  public OptiFineVersion currentOptiFineVersion() {
    return this.optiFineVersion;
  }

  private void download(String urlPath, String follow) throws IOException {
    if (Files.exists(this.optifineJarPath)) {
      return;
    }

    HttpURLConnection connection = this.prepareConnection(BASE_URL + urlPath);

    if (connection.getResponseCode() / 100 != 2) {
      return;
    }

    InputStream inputStream = connection.getInputStream();

    String webContent = IOUtil.toString(inputStream);

    for (String content : webContent.split(System.lineSeparator())) {
      if (!content.matches(follow)) {
        continue;
      }

      int firstIndex = content.indexOf("'");
      int secondIndex = content.indexOf("'", firstIndex + 1);
      String relativeLink = content.substring(firstIndex + 1, secondIndex);
      String absoluteLink = BASE_URL + relativeLink;
      Files.createDirectories(this.optifineJarPath.getParent());
      Files.write(
          this.optifineJarPath,
          IOUtil.readBytes(this.prepareConnection(absoluteLink).getInputStream())
      );
      break;
    }
  }

  private HttpURLConnection prepareConnection(String url) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(Proxy.NO_PROXY);
    connection.setRequestProperty("User-Agent", USER_AGENT);
    return connection;
  }


}

