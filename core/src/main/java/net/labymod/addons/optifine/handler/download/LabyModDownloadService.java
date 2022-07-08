package net.labymod.addons.optifine.handler.download;

import java.io.IOException;
import net.labymod.addons.optifine.handler.OptiFineVersion;
import net.labymod.api.models.version.Version;

// TODO: 23.09.2021 Implement fallback service
public class LabyModDownloadService extends DownloadService {

  @Override
  public void download(Version version) throws IOException {

  }

  @Override
  public OptiFineVersion currentOptiFineVersion() {
    return null;
  }
}
