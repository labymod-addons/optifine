package net.labymod.addons.optifine.handler;

import net.labymod.addons.optifine.handler.download.DownloadService;
import net.labymod.addons.optifine.handler.download.LabyModDownloadService;
import net.labymod.addons.optifine.handler.download.OptifineDownloadService;
import net.labymod.api.loader.EnvironmentData;
import java.io.IOException;

public class OptifineDownloader {

  private boolean shouldUseFallback = false;
  private DownloadService downloadService;

  public void download(EnvironmentData data) throws IOException {
    this.downloadService =
        this.shouldUseFallback ?
            new LabyModDownloadService() :
            new OptifineDownloadService();
    this.downloadService.download(data);
  }

  public DownloadService getDownloadService() {
    return this.downloadService;
  }
}
