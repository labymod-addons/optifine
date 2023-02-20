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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.labymod.addons.optifine.handler.OptiFineVersion;
import net.labymod.addons.optifine.launch.patches.OptiFineShaderDownloadButtonPatcher;
import net.labymod.addons.optifine.launch.patches.OptiFineShadersPatcher;
import net.labymod.addons.optifine.launch.patches.OptiFineTransformerPatcher;
import net.labymod.addons.optifine.launch.patches.OptiFineWidgetIdentifierPatcher;
import net.labymod.addons.optifine.util.PatchApplierClassEntryTransformer;
import net.labymod.api.util.io.zip.ZipTransformer;

public class OptiFinePatcher {

  private final Map<String, List<Patcher>> patchers;

  public OptiFinePatcher() {
    this.patchers = new HashMap<>();

    this.registerPatcher("optifine/OptiFineClassTransformer", new OptiFineTransformerPatcher());
    this.registerPatcher("net/optifine/shaders/gui/GuiButtonDownloadShaders", new OptiFineShaderDownloadButtonPatcher());
    this.registerPatcher("net/optifine/gui/GuiButtonOF", new OptiFineWidgetIdentifierPatcher());
    this.registerPatcher("net/optifine/shaders/Shaders", new OptiFineShadersPatcher());
  }

  public void registerPatcher(String className, Patcher patcher) {
    this.patchers.computeIfAbsent(className, l -> new ArrayList<>()).add(patcher);
  }

  public Map<String, List<Patcher>> getPatchers() {
    return this.patchers;
  }

  public Path patch(OptiFineVersion optiFineVersion, Path path) throws IOException {
    Path directory = path.getParent();
    Path patchedJar = directory.resolve(optiFineVersion.getQualifiedJarName() + "-PATCHED.jar");

    ZipTransformer transformer = ZipTransformer.createDefault(path, patchedJar);
    transformer.addTransformer(new PatchApplierClassEntryTransformer(this));
    transformer.transform();

    return patchedJar;
  }
}
