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
import net.labymod.api.util.logging.Logging;
import net.labymod.api.util.version.SemanticVersion;
import net.labymod.core.loader.DefaultLabyModLoader;
import net.labymod.core.util.classpath.ClasspathUtil;

@SuppressWarnings("UnstableApiUsage")
@AddonEntryPoint
public class OptiFineEntrypoint implements Entrypoint {

  private static final Version VERSION_1_12_2 = new SemanticVersion("1.12.2");

  private static URI optifineUri;
  private static Version version;

  @Override
  public void initialize(Version version) {
    try {
      OptiFineEntrypoint.version = version;
      boolean developmentEnvironment = DefaultLabyModLoader.getInstance().isLabyModDevelopmentEnvironment();
      OptiFinePatcher patcher = new OptiFinePatcher(
          version.isLowerThan(VERSION_1_12_2),
          developmentEnvironment
      );

      PlatformClassloader platformClassloader = PlatformEnvironment.getPlatformClassloader();

      // Download the specified optifine version
      OptifineDownloader optifineDownloader = new OptifineDownloader();
      optifineDownloader.download(version);
      OptiFineVersion optiFineVersion = optifineDownloader
          .getDownloadService()
          .currentOptiFineVersion();

      Path optifineJarPath = optifineDownloader.getDownloadService().getOptifineJarPath();

      ClassLoader classloader = platformClassloader.getPlatformClassloader();
      if (developmentEnvironment) {
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
      throw new RuntimeException(exception);
    }
  }

  public static URI optifineUri() {
    return optifineUri;
  }

  public static byte[] readDev(String name, ZipFile file) {
    if (file != null) {

      if (version != null && version.isLowerThan(VERSION_1_12_2)) {
        boolean addNotchPrefix = name.startsWith("net.minecraft");
        name = name.replace(".class", "");
        if (addNotchPrefix) {
          name = "notch/" + name.replace(".", "/");
        }
        name += ".class";
      }

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
