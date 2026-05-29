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
import java.nio.file.Path;
import net.labymod.addons.optifine.exception.OptiFineException;
import net.labymod.addons.optifine.launch.prepare.PreparationContext;
import net.labymod.addons.optifine.launch.prepare.PreparationStage;
import net.labymod.addons.optifine.launch.remap.OptiFineMappingProvider;
import net.labymod.api.mapping.MappingService;
import net.labymod.api.mapping.provider.MappingProvider;
import net.labymod.api.mapping.remap.JarRemapper;
import net.labymod.api.util.logging.Logging;

/**
 * Remaps the stripped, obfuscated jar from the obfuscated namespace to named, mirroring LabyGradle's
 * build-time remap. The obfuscated client jar is supplied as a library because OptiFine's classes
 * reference Minecraft class hierarchies that the remapper (FART) otherwise cannot resolve.
 */
public class RemapStage implements PreparationStage {

  private static final Logging LOGGER = Logging.getLogger();

  @Override
  public String name() {
    return "remap";
  }

  @Override
  public Path run(PreparationContext context, Path input) throws OptiFineException {
    Path output = context.scratch("remapped.jar");
    MappingProvider provider = OptiFineMappingProvider.resolve();
    try {
      JarRemapper remapper = MappingService.instance()
          .jarRemapper(provider)
          .entry(input, output)
          .library(context.obfuscatedClientJar())
          .build();
      remapper.execute(line -> LOGGER.debug("{}", line));
    } catch (IOException exception) {
      throw new OptiFineException("Failed to remap OptiFine jar to named mappings", exception);
    }
    return output;
  }
}
