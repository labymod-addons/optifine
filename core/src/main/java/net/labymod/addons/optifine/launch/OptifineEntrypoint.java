package net.labymod.addons.optifine.launch;

import java.nio.file.Path;
import net.labymod.addons.optifine.handler.OptifineDownloader;
import net.labymod.api.addon.entrypoint.Entrypoint;
import net.labymod.api.loader.EnvironmentData;
import net.labymod.api.models.addon.annotation.AddonEntryPoint;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("UnstableApiUsage")
@AddonEntryPoint
public class OptifineEntrypoint implements Entrypoint {

  private static final Logger LOGGER = LogManager.getLogger("Optifine");

  @Override
  public void initialize(EnvironmentData data) {
    try {
      // Download the specified optifine version
      OptifineDownloader optifineDownloader = new OptifineDownloader();
      optifineDownloader.download(data);

      Path optifineJarPath = optifineDownloader.getDownloadService().getOptifineJarPath();
      // Add the optifine jar to the classpath
      Launch.classLoader.addURL(optifineJarPath.toUri().toURL());
      // Register the optifine class transformer
      Launch.classLoader.registerPreTransformer("optifine.OptiFineClassTransformer");
    } catch (Exception exception) {
      LOGGER.error("An error occurred while downloading Optifine...", exception);
    }
  }
}
