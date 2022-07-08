package net.labymod.addons.optifine.launch;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.labymod.addons.optifine.handler.OptiFineVersion;
import net.labymod.addons.optifine.handler.OptifineDownloader;
import net.labymod.addons.optifine.handler.dev.OptiFineDevHandler;
import net.labymod.api.addon.entrypoint.Entrypoint;
import net.labymod.api.loader.platform.PlatformClassloader;
import net.labymod.api.loader.platform.PlatformClassloader.TransformerPhase;
import net.labymod.api.loader.platform.PlatformEnvironment;
import net.labymod.api.models.addon.annotation.AddonEntryPoint;
import net.labymod.api.models.version.Version;
import net.labymod.api.util.io.IOUtil;
import net.labymod.core.loader.DefaultLabyModLoader;
import net.labymod.core.util.classpath.ClasspathUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("UnstableApiUsage")
@AddonEntryPoint
public class OptiFineEntrypoint implements Entrypoint {

  private static final Logger LOGGER = LogManager.getLogger("Optifine");

  private static URI optifineUri;

  @Override
  public void initialize(Version version) {
    try {
      OptiFinePatcher patcher = new OptiFinePatcher();

      PlatformClassloader platformClassloader = PlatformEnvironment.getPlatformClassloader();

      // Download the specified optifine version
      OptifineDownloader optifineDownloader = new OptifineDownloader();
      optifineDownloader.download(version);
      OptiFineVersion optiFineVersion = optifineDownloader
          .getDownloadService()
          .currentOptiFineVersion();

      Path optifineJarPath = optifineDownloader.getDownloadService().getOptifineJarPath();

      ClassLoader classloader = platformClassloader.getPlatformClassloader();
      if (DefaultLabyModLoader.getInstance().isLabyModDevelopmentEnvironment()) {
        OptiFineDevHandler handler = new OptiFineDevHandler();
        optifineJarPath = handler.handle(optiFineVersion, optifineJarPath);
      }

      optifineJarPath = patcher.patch(optiFineVersion, optifineJarPath);
      optifineUri = optifineJarPath.toUri();

      // Add the optifine jar to the classpath
      platformClassloader.addPath(optifineJarPath);

      List<String> list = ClasspathUtil.getJarEntryNames(
          optifineJarPath.toAbsolutePath().toString(),
          "optifine"
      );

      // Preload all classes
      for (String s : list) {
        if (!s.endsWith(".class")) {
          continue;
        }
        s = s.replace("/", ".");
        s = s.substring(0, s.length() - ".class".length());
        try {
          classloader.loadClass(s);
        } catch (Throwable throwable) {
          // throwable.printStackTrace();
        }
      }

      // Register the optifine class transformer
      platformClassloader.registerTransformer(
          TransformerPhase.PRE,
          "optifine.OptiFineClassTransformer"
      );
    } catch (Exception exception) {
      LOGGER.error("OptiFine could not be loaded because an error occurred!", exception);
    }
  }

  public static URI optifineUri() {
    return optifineUri;
  }

  public static byte[] readDev(String name, ZipFile file) {
    if (file != null) {
      ZipEntry entry = file.getEntry(name);
      if (entry == null) {
        return null;
      }
      try (InputStream stream = file.getInputStream(entry)) {
        return IOUtil.readBytes(stream);
      } catch (IOException exception) {
        return null;
      }
    }
    return null;
  }

}
