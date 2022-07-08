package net.labymod.addons.optifine.handler.download;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import net.labymod.addons.optifine.handler.OptiFineVersion;
import net.labymod.addons.optifine.handler.OptiFineVersions;
import net.labymod.api.models.version.Version;
import net.labymod.api.util.io.IOUtil;

public class OptiFineDownloadService extends DownloadService {

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

    HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL + urlPath).openConnection();
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
          IOUtil.readBytes(new URL(absoluteLink).openConnection().getInputStream())
      );
      break;
    }
  }


}

