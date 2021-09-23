package net.labymod.addons.optifine.handler.download;

import net.labymod.api.loader.EnvironmentData;
import java.io.IOException;
import java.nio.file.Path;

public abstract class DownloadService {

  protected Path optifineJarPath;

  public abstract void download(EnvironmentData data) throws IOException;

  public Path getOptifineJarPath() {
    return this.optifineJarPath;
  }
}
