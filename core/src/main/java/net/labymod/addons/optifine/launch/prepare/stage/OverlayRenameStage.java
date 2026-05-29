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
package net.labymod.addons.optifine.launch.prepare.stage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import net.labymod.addons.optifine.exception.OptiFineException;
import net.labymod.addons.optifine.launch.prepare.PreparationContext;
import net.labymod.addons.optifine.launch.prepare.PreparationStage;
import net.labymod.api.mapping.MappingNamespace;
import net.labymod.api.mapping.MappingService;
import net.labymod.api.mapping.remap.JarRemapper;
import net.labymod.api.util.logging.Logging;
import net.labymod.core.mapping.provider.FartMappingProvider;
import net.minecraftforge.srgutils.IMappingFile;

/**
 * Renames the methods OptiFine declares under their plain vanilla name (e.g. {@code onEntityRemoved})
 * to the suffixed {@code <name>OptiFine} form, in the obfuscated namespace, before the obfuscated
 * -&gt; named remap runs.
 *
 * <p>OptiFine's modded classes keep these methods under the real vanilla name, so the following
 * obfuscated -&gt; named remap would map both them and the genuine vanilla method (still obfuscated)
 * onto the same named signature, producing a {@code ClassFormatError: Duplicate method}. The bundled
 * {@code <version>.srg} overlay pairs each {@code <name>OptiFine} name with its vanilla name;
 * applied <em>reversed</em> it performs exactly this rename, so the OptiFine method survives the
 * remap under a distinct name. Versions without an overlay pass through unchanged.
 *
 * <p>This must run after {@link StripStage} (the obfuscated classes are at their canonical paths only
 * then) and before {@link RemapStage}. The obfuscated client jar is supplied as a library for the
 * same hierarchy-resolution reason as the named remap.
 */
public class OverlayRenameStage implements PreparationStage {

  private static final Logging LOGGER = Logging.getLogger();
  private static final String OVERLAY_RESOURCE = "assets/optifine/mappings/%s.srg";

  @Override
  public String name() {
    return "overlay-rename";
  }

  @Override
  public Path run(PreparationContext context, Path input) throws OptiFineException {
    IMappingFile overlay = this.loadOverlay(context.gameVersion());
    if (overlay == null) {
      return input;
    }

    FartMappingProvider provider = new FartMappingProvider(
        MappingNamespace.MINECRAFT_OBFUSCATED,
        MappingNamespace.MINECRAFT_OBFUSCATED,
        overlay.reverse()
    );

    Path output = context.scratch("overlay.jar");
    try {
      JarRemapper remapper = MappingService.instance()
          .jarRemapper(provider)
          .entry(input, output)
          .library(context.obfuscatedClientJar())
          .build();
      remapper.execute(line -> LOGGER.debug("{}", line));
    } catch (IOException exception) {
      throw new OptiFineException("Failed to apply OptiFine mapping overlay rename", exception);
    }
    return output;
  }

  private IMappingFile loadOverlay(String gameVersion) throws OptiFineException {
    String resource = String.format(OVERLAY_RESOURCE, gameVersion);
    ClassLoader classLoader = OverlayRenameStage.class.getClassLoader();
    try (InputStream stream = classLoader.getResourceAsStream(resource)) {
      if (stream == null) {
        return null;
      }
      return IMappingFile.load(stream);
    } catch (IOException exception) {
      throw new OptiFineException(
          "Failed to load OptiFine mapping overlay for " + gameVersion,
          exception
      );
    }
  }
}
