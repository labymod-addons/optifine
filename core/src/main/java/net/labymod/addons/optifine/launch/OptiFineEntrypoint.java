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

import java.net.URI;
import java.nio.file.Path;
import net.labymod.addons.optifine.exception.OptiFineException;
import net.labymod.addons.optifine.handler.OptiFineVersion;
import net.labymod.addons.optifine.handler.OptifineDownloader;
import net.labymod.addons.optifine.handler.download.DownloadService;
import net.labymod.api.Laby;
import net.labymod.api.addon.entrypoint.Entrypoint;
import net.labymod.api.loader.platform.PlatformClassloader;
import net.labymod.api.loader.platform.PlatformClassloader.TransformerPhase;
import net.labymod.api.loader.platform.PlatformEnvironment;
import net.labymod.api.models.addon.annotation.AddonEntryPoint;
import net.labymod.api.models.version.Version;
import net.labymod.api.util.io.IOUtil;
import net.labymod.api.util.version.serial.VersionDeserializer;
import net.labymod.core.addon.DefaultAddonService;
import net.labymod.core.loader.DefaultLabyModLoader;

@AddonEntryPoint
public class OptiFineEntrypoint implements Entrypoint {

  // GLX and the OptiFine buffer-source mixin are 1.16.5-era concerns and fire only on exactly 1.16.5.
  private static final Version VERSION_1_16_5 = VersionDeserializer.from("1.16.5");

  // When LabyForge is present we run on Forge, which already provides the net.minecraftforge and
  // javax classes OptiFine bundles for its standalone Forge build; those get stripped to avoid clashes.
  private static final String FORGE_ADDON_NAMESPACE = "labyforge";

  private static URI optifineUri;

  public static URI optifineUri() {
    return optifineUri;
  }

  @Override
  public void initialize(Version version) {
    try {
      this.prepareAndRegister(version);
    } catch (OptiFineException exception) {
      // Thrown before the client/UI exists: re-throwing makes the platform log the cause and cleanly
      // unload the addon rather than crashing the game.
      throw new RuntimeException("Failed to initialize OptiFine: " + exception.getMessage(), exception);
    }
  }

  private void prepareAndRegister(Version version) throws OptiFineException {
    PlatformClassloader platformClassloader = PlatformEnvironment.getPlatformClassloader();

    OptifineDownloader optifineDownloader = new OptifineDownloader();
    optifineDownloader.download(version);

    DownloadService downloadService = optifineDownloader.getDownloadService();
    Path rawOptiFineJar = downloadService.getOptifineJarPath();
    if (IOUtil.isCorrupted(rawOptiFineJar)) {
      throw new OptiFineException("Downloaded OptiFine jar is corrupted");
    }

    Path obfuscatedClientJar = PlatformEnvironment.getObfuscatedJarPath();
    if (obfuscatedClientJar == null) {
      throw new OptiFineException("Obfuscated Minecraft client jar is not available");
    }

    OptiFineVersion optiFineVersion = downloadService.currentOptiFineVersion();
    boolean stripForge = DefaultAddonService.getInstance().getAddon(FORGE_ADDON_NAMESPACE).isPresent();
    OptiFinePatcher patcher = new OptiFinePatcher();
    Path preparedJar = patcher.prepare(
        optiFineVersion,
        rawOptiFineJar,
        obfuscatedClientJar,
        version.toString(),
        stripForge
    );
    optifineUri = preparedJar.toUri();

    platformClassloader.addPath(preparedJar);

    platformClassloader.registerTransformer(
        TransformerPhase.PRE,
        "net.labymod.addons.optifine.launch.transformer.WrappedOptiFineTransformer"
    );
  }
}
