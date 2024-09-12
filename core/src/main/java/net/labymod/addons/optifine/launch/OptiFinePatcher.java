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
import java.nio.file.Files;
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
import net.labymod.api.util.io.IOUtil;
import net.labymod.api.util.io.zip.ZipTransformer;
import net.labymod.api.util.logging.Logging;

public class OptiFinePatcher {

  private static final Logging LOGGER = Logging.getLogger();
  private static final int UNKNOWN_VERSION = 0;
  private static final int VERSION = 2;
  private static final int MAX_TRIES = 3;
  private final Map<String, List<Patcher>> patchers;


  private int tries;

  public OptiFinePatcher() {
    this.patchers = new HashMap<>();

    this.registerPatcher("optifine/OptiFineClassTransformer", new OptiFineTransformerPatcher());
    this.registerPatcher("net/optifine/shaders/gui/GuiButtonDownloadShaders",
        new OptiFineShaderDownloadButtonPatcher());
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
    Path versionPath = directory.resolve(optiFineVersion.getQualifiedJarName() + ".version");

    int currentVersion = this.getVersion(versionPath);
    boolean needsTransformation = currentVersion < VERSION;

    if (IOUtil.isCorrupted(patchedJar) || needsTransformation) {
      LOGGER.info("Patching " + patchedJar);
      ZipTransformer transformer = ZipTransformer.createDefault(path, patchedJar);
      transformer.addTransformer(new PatchApplierClassEntryTransformer(this));
      transformer.transform();
      try {
        Files.writeString(versionPath, String.valueOf(VERSION));
      } catch (IOException ignored) {

      }
      LOGGER.info("Patched " + patchedJar);
    }

    if (IOUtil.isCorrupted(patchedJar)) {
      this.tries++;
      if (this.tries > MAX_TRIES) {
        throw new IOException("Too many tries");
      }

      LOGGER.warn("Failed to patch {} (Tries: {}/{})", patchedJar, this.tries, MAX_TRIES);
      return this.patch(optiFineVersion, patchedJar);
    }

    return patchedJar;
  }

  private int getVersion(Path file) {
    if (!Files.exists(file)) {
      return UNKNOWN_VERSION;
    }

    try {
      return Integer.parseInt(Files.readString(file));
    } catch (IOException | NumberFormatException exception) {
      return UNKNOWN_VERSION;
    }
  }
}
