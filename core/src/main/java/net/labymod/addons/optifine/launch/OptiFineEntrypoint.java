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
import net.labymod.api.addon.entrypoint.Entrypoint;
import net.labymod.api.loader.platform.PlatformClassloader;
import net.labymod.api.loader.platform.PlatformClassloader.TransformerPhase;
import net.labymod.api.loader.platform.PlatformEnvironment;
import net.labymod.api.models.addon.annotation.AddonEntryPoint;
import net.labymod.api.models.version.Version;
import net.labymod.api.util.io.IOUtil;
import net.labymod.api.util.version.SemanticVersion;
import net.labymod.core.util.classpath.ClasspathUtil;

@SuppressWarnings("UnstableApiUsage")
@AddonEntryPoint
public class OptiFineEntrypoint implements Entrypoint {

  private static final Version LEGACY_VERSION = new SemanticVersion("1.16.5");

  private static URI optifineUri;
  private static Version version;

  @Override
  public void initialize(Version version) {
    try {
      OptiFineEntrypoint.version = version;
      OptiFinePatcher patcher = new OptiFinePatcher();

      PlatformClassloader platformClassloader = PlatformEnvironment.getPlatformClassloader();

      // Download the specified optifine version
      OptifineDownloader optifineDownloader = new OptifineDownloader();
      optifineDownloader.download(version);
      OptiFineVersion optiFineVersion = optifineDownloader
          .getDownloadService()
          .currentOptiFineVersion();

      Path optifineJarPath = optifineDownloader.getDownloadService().getOptifineJarPath();

      optifineJarPath = patcher.patch(optiFineVersion, optifineJarPath);
      optifineUri = optifineJarPath.toUri();

      // Add the optifine jar to the classpath
      platformClassloader.addPath(optifineJarPath);

      List<String> list = ClasspathUtil.getJarEntryNames(
          optifineJarPath.toAbsolutePath().toString(),
          "optifine"
      );

      if (version.equals(LEGACY_VERSION)) {
        platformClassloader.registerTransformer(
            TransformerPhase.PRE,
            "net.labymod.addons.optifine.launch.transformer.GLXTransformer"
        );
      }

      ClassLoader classloader = platformClassloader.getPlatformClassloader();
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
          "net.labymod.addons.optifine.launch.transformer.WrappedOptiFineTransformer"
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

      if (version != null && version.isLowerThan(LEGACY_VERSION)) {
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
