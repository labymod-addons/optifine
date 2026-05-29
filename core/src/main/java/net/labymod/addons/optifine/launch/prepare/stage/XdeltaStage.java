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

import java.nio.file.Path;
import net.labymod.addons.optifine.exception.OptiFineException;
import net.labymod.addons.optifine.launch.prepare.PreparationContext;
import net.labymod.addons.optifine.launch.prepare.PreparationStage;
import net.labymod.addons.optifine.launch.remap.OptiFineXdeltaApplier;

/**
 * Applies OptiFine's own xdelta patches against the obfuscated Minecraft client jar, producing a
 * jar that contains the obfuscated, OptiFine-modified Minecraft classes plus OptiFine's own
 * classes.
 */
public class XdeltaStage implements PreparationStage {

  @Override
  public String name() {
    return "xdelta";
  }

  @Override
  public Path run(PreparationContext context, Path input) throws OptiFineException {
    Path output = context.scratch("mod.jar");
    OptiFineXdeltaApplier.apply(context.obfuscatedClientJar(), input, output);
    return output;
  }
}
