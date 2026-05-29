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
import net.labymod.addons.optifine.launch.remap.JarEntryFilter;

/**
 * Drops the {@code net/minecraftforge/} and {@code javax/} packages OptiFine bundles for its
 * standalone Forge build. Only appended to the pipeline when LabyMod itself runs on Forge, where
 * those classes already exist on the classpath and would otherwise clash with the prepared jar. Runs
 * after the remap stage so the remapper still sees the Forge supertypes OptiFine's classes
 * reference.
 */
public class ForgeStripStage implements PreparationStage {

  @Override
  public String name() {
    return "forge-strip";
  }

  @Override
  public Path run(PreparationContext context, Path input) throws OptiFineException {
    Path output = context.scratch("forge-stripped.jar");
    try {
      JarEntryFilter.stripForgePackages(input, output);
    } catch (IOException exception) {
      throw new OptiFineException("Failed to strip Forge packages from OptiFine jar", exception);
    }
    return output;
  }
}
