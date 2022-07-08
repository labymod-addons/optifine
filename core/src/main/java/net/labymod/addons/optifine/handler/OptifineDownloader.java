package net.labymod.addons.optifine.handler;

import java.io.IOException;
import net.labymod.addons.optifine.handler.download.DownloadService;
import net.labymod.addons.optifine.handler.download.LabyModDownloadService;
import net.labymod.addons.optifine.handler.download.OptiFineDownloadService;
import net.labymod.api.models.version.Version;

public class OptifineDownloader {

  private boolean shouldUseFallback = false;
  private DownloadService downloadService;

  public void download(Version version) throws IOException {
    this.downloadService =
        this.shouldUseFallback ?
            new LabyModDownloadService() :
            new OptiFineDownloadService();
    this.downloadService.download(version);
  }

  public DownloadService getDownloadService() {
    return this.downloadService;
  }
}
