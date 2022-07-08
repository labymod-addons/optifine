package net.labymod.addons.optifine.handler.download;

import java.io.IOException;
import java.nio.file.Path;
import net.labymod.addons.optifine.handler.OptiFineVersion;
import net.labymod.api.models.version.Version;

public abstract class DownloadService {

  protected Path optifineJarPath;

  public abstract void download(Version version) throws IOException;

  public Path getOptifineJarPath() {
    return this.optifineJarPath;
  }

  public abstract OptiFineVersion currentOptiFineVersion();

}
