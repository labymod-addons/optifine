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
package net.labymod.addons.optifine.launch.remap;

import java.io.IOException;
import java.io.InputStream;
import net.labymod.addons.optifine.exception.OptiFineException;
import net.labymod.api.mapping.MappingNamespace;
import net.labymod.api.mapping.MappingService;
import net.labymod.api.mapping.provider.MappingProvider;
import net.labymod.api.util.logging.Logging;
import net.labymod.core.mapping.provider.FartMappingProvider;
import net.minecraftforge.srgutils.IMappingFile;

/**
 * Builds the obfuscated -&gt; named mapping provider used to remap OptiFine, optionally merging the
 * version-specific OptiFine overlay.
 *
 * <p>OptiFine renames some Minecraft methods it overrides (e.g. {@code getChildOptiFine}). The
 * bundled {@code <version>.srg} overlay maps those back onto the vanilla method so the obfuscated
 * -&gt; named remap binds them correctly. This mirrors LabyGradle's
 * {@code LayeredMappingFile.combineFiles}, which merges the same overlay before remapping. The
 * overlays only exist for the versions that need them; absence is not an error.
 */
public final class OptiFineMappingProvider {

  private static final Logging LOGGER = Logging.getLogger();
  private static final String OVERLAY_RESOURCE = "assets/optifine/mappings/%s.srg";

  private OptiFineMappingProvider() {
  }

  public static MappingProvider resolve(String gameVersion) throws OptiFineException {
    MappingService mappingService = MappingService.instance();
    MappingProvider base = mappingService.mappings(
        MappingNamespace.MINECRAFT_OBFUSCATED,
        MappingNamespace.NAMED
    );
    if (base == null) {
      throw new OptiFineException(
          "No '" + MappingNamespace.MINECRAFT_OBFUSCATED + " -> " + MappingNamespace.NAMED
              + "' mapping provider available"
      );
    }

    IMappingFile overlay = loadOverlay(gameVersion);
    if (overlay == null) {
      return base;
    }

    if (!(base instanceof FartMappingProvider fartBase)) {
      LOGGER.warn(
          "Mapping provider is not a FartMappingProvider; skipping OptiFine overlay for {}",
          gameVersion
      );
      return base;
    }

    IMappingFile merged = fartBase.getDelegate().merge(overlay);
    return new FartMappingProvider(
        MappingNamespace.MINECRAFT_OBFUSCATED,
        MappingNamespace.NAMED,
        merged
    );
  }

  private static IMappingFile loadOverlay(String gameVersion) throws OptiFineException {
    String resource = String.format(OVERLAY_RESOURCE, gameVersion);
    ClassLoader classLoader = OptiFineMappingProvider.class.getClassLoader();
    try (InputStream stream = classLoader.getResourceAsStream(resource)) {
      if (stream == null) {
        return null;
      }
      return IMappingFile.load(stream);
    } catch (IOException exception) {
      throw new OptiFineException("Failed to load OptiFine mapping overlay for " + gameVersion, exception);
    }
  }
}
