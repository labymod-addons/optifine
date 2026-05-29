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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.labymod.addons.optifine.exception.OptiFineException;
import net.labymod.addons.optifine.handler.OptiFineVersion;
import net.labymod.addons.optifine.launch.patches.OptiFineShaderDownloadButtonPatcher;
import net.labymod.addons.optifine.launch.patches.OptiFineShadersPatcher;
import net.labymod.addons.optifine.launch.patches.OptiFineWidgetIdentifierPatcher;
import net.labymod.addons.optifine.launch.prepare.PreparationContext;
import net.labymod.addons.optifine.launch.prepare.PreparationPipeline;
import net.labymod.addons.optifine.launch.prepare.PreparationStage;
import net.labymod.addons.optifine.launch.prepare.stage.AsmPatchStage;
import net.labymod.addons.optifine.launch.prepare.stage.ForgeStripStage;
import net.labymod.addons.optifine.launch.prepare.stage.RemapStage;
import net.labymod.addons.optifine.launch.prepare.stage.StripStage;
import net.labymod.addons.optifine.launch.prepare.stage.XdeltaStage;
import net.labymod.api.util.HashUtil;
import net.labymod.api.util.io.IOUtil;
import net.labymod.api.util.logging.Logging;

/**
 * Turns the downloaded OptiFine installer jar into a named, ready-to-load jar. The result is cached
 * per input fingerprint (mirroring {@code DefaultGameProvider#prepare}): if the obfuscated client
 * jar and OptiFine jar are unchanged, the prepared jar is reused and the pipeline is skipped.
 */
public class OptiFinePatcher {

  private static final Logging LOGGER = Logging.getLogger();

  // Bump when the pipeline logic changes in a way that invalidates already-prepared jars.
  private static final int REMAP_VERSION = 2;

  private final Map<String, List<Patcher>> patchers;

  public OptiFinePatcher() {
    this.patchers = new HashMap<>();
    this.registerPatcher("net/optifine/shaders/gui/GuiButtonDownloadShaders",
        new OptiFineShaderDownloadButtonPatcher());
    this.registerPatcher("net/optifine/gui/GuiButtonOF", new OptiFineWidgetIdentifierPatcher());
    this.registerPatcher("net/optifine/shaders/Shaders", new OptiFineShadersPatcher());
  }

  public void registerPatcher(String className, Patcher patcher) {
    this.patchers.computeIfAbsent(className, key -> new ArrayList<>()).add(patcher);
  }

  public Map<String, List<Patcher>> getPatchers() {
    return this.patchers;
  }

  public Path prepare(
      OptiFineVersion optiFineVersion,
      Path rawOptiFineJar,
      Path obfuscatedClientJar,
      String gameVersion,
      boolean stripForge
  ) throws OptiFineException {
    Path directory = OptiFineStorage.directory(gameVersion);
    String qualifiedName = optiFineVersion.getQualifiedJarName();
    Path destination = directory.resolve(qualifiedName + "-PATCHED.jar");
    Path checksumFile = directory.resolve(qualifiedName + "-PATCHED.jar.checksum");

    String fingerprint = this.fingerprint(obfuscatedClientJar, rawOptiFineJar, stripForge);
    if (this.isUpToDate(destination, checksumFile, fingerprint)) {
      LOGGER.info("Prepared OptiFine jar up to date, skipping ({})", destination);
      return destination;
    }

    Path scratchDirectory = directory.resolve("tmp");
    this.deleteRecursively(scratchDirectory);
    try {
      Files.createDirectories(scratchDirectory);
    } catch (IOException exception) {
      throw new OptiFineException("Failed to create OptiFine directory " + scratchDirectory,
          exception);
    }

    LOGGER.info("Preparing OptiFine jar {}", destination);
    PreparationContext context = new PreparationContext(
        rawOptiFineJar,
        obfuscatedClientJar,
        gameVersion,
        optiFineVersion,
        scratchDirectory
    );
    List<PreparationStage> stages = new ArrayList<>();
    stages.add(new AsmPatchStage(this));
    stages.add(new XdeltaStage());
    stages.add(new StripStage());
    stages.add(new RemapStage());
    if (stripForge) {
      stages.add(new ForgeStripStage());
    }
    PreparationPipeline pipeline = new PreparationPipeline(stages);

    try {
      Path prepared = pipeline.run(context);
      if (IOUtil.isCorrupted(prepared)) {
        throw new OptiFineException("Prepared OptiFine jar is corrupted");
      }
      IOUtil.atomicMove(prepared, destination);
    } catch (IOException exception) {
      throw new OptiFineException("Failed to finalize prepared OptiFine jar", exception);
    } finally {
      this.deleteRecursively(scratchDirectory);
    }

    try {
      Files.writeString(checksumFile, fingerprint);
    } catch (IOException exception) {
      LOGGER.warn("Failed to write OptiFine checksum file {}", checksumFile, exception);
    }

    LOGGER.info("Prepared OptiFine jar {}", destination);
    return destination;
  }

  private boolean isUpToDate(Path destination, Path checksumFile, String fingerprint) {
    if (IOUtil.isCorrupted(destination) || !Files.exists(checksumFile)) {
      return false;
    }

    try {
      return fingerprint.equals(Files.readString(checksumFile));
    } catch (IOException exception) {
      LOGGER.warn(
          "Failed to read OptiFine checksum file {}, re-preparing",
          checksumFile,
          exception
      );
      return false;
    }
  }

  private String fingerprint(
      Path obfuscatedClientJar,
      Path rawOptiFineJar,
      boolean stripForge
  ) throws OptiFineException {
    try (InputStream obfuscatedStream = Files.newInputStream(obfuscatedClientJar);
        InputStream optiFineStream = Files.newInputStream(rawOptiFineJar)) {
      return REMAP_VERSION
          + ":"
          + HashUtil.md5Hex(obfuscatedStream)
          + ":"
          + HashUtil.md5Hex(optiFineStream)
          + ":"
          + stripForge;
    } catch (IOException exception) {
      throw new OptiFineException("Failed to compute OptiFine cache fingerprint", exception);
    }
  }

  private void deleteRecursively(Path directory) {
    if (!Files.exists(directory)) {
      return;
    }

    try {
      Files.walkFileTree(directory, new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
            throws IOException {
          Files.deleteIfExists(file);
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path visited, IOException exception)
            throws IOException {
          Files.deleteIfExists(visited);
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException exception) {
      LOGGER.debug("Failed to delete OptiFine scratch directory {}", directory, exception);
    }
  }
}
