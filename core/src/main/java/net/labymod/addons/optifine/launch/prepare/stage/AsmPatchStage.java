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
import net.labymod.addons.optifine.launch.OptiFinePatcher;
import net.labymod.addons.optifine.launch.prepare.PreparationContext;
import net.labymod.addons.optifine.launch.prepare.PreparationStage;
import net.labymod.addons.optifine.util.PatchApplierClassEntryTransformer;
import net.labymod.api.util.io.zip.ZipTransformer;

/**
 * Applies the OptiFine GUI patchers to the raw, pristine OptiFine jar. This runs before the xdelta
 * stage on purpose: the patched GUI classes are not part of OptiFine's MD5-validated patch set, and
 * keeping the jar otherwise pristine lets {@code optifine.Patcher.process} reconstruct the modded
 * Minecraft classes exactly as LabyGradle does at build time.
 */
public class AsmPatchStage implements PreparationStage {

  private final OptiFinePatcher patcher;

  public AsmPatchStage(OptiFinePatcher patcher) {
    this.patcher = patcher;
  }

  @Override
  public String name() {
    return "asm-patch";
  }

  @Override
  public Path run(PreparationContext context, Path input) throws OptiFineException {
    Path output = context.scratch("asm.jar");
    try {
      ZipTransformer transformer = ZipTransformer.createDefault(input, output);
      transformer.addTransformer(new PatchApplierClassEntryTransformer(this.patcher));
      transformer.transform();
    } catch (IOException exception) {
      throw new OptiFineException("Failed to apply OptiFine GUI patches", exception);
    }
    return output;
  }
}
