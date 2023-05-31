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

package net.labymod.addons.optifine.handler.dev;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.labymod.addons.optifine.exception.OptiFineException;
import net.labymod.addons.optifine.handler.OptiFineVersion;
import net.labymod.addons.optifine.util.LogWriter;
import net.labymod.api.util.IsolatedClassLoader;
import net.labymod.api.util.io.zip.EntryTransformer;
import net.labymod.api.util.io.zip.ZipTransformer;
import net.labymod.api.util.io.zip.entry.ClassEntry;
import net.minecraftforge.fart.api.Renamer;
import net.minecraftforge.fart.api.Renamer.Builder;
import net.minecraftforge.fart.api.Transformer;
import net.minecraftforge.srgutils.IMappingFile;

public class OptiFineDevHandler {

  private static final String OBF_MC_JAR_PROPERTY = "optifine.dev.obf-mc-jar";
  private static final String OBF_MAPPINGS_PROPERTY = "optifine.dev.obf-mappings";

  private final Path minecraftJarPath;
  private final Path mappingPath;

  public OptiFineDevHandler() throws OptiFineException {
    this.minecraftJarPath = this.getPath(OBF_MC_JAR_PROPERTY);
    this.mappingPath = this.getPath(OBF_MAPPINGS_PROPERTY);
  }

  private Path getPath(String propertyName) throws OptiFineException {
    String property = System.getProperty(propertyName);
    if (property == null) {
      this.thrownOptiFineException(
          "No path for a file was specified",
          propertyName
      );
    }

    Path path = Paths.get(property);

    if (Files.notExists(path)) {
      this.thrownOptiFineException(
          "There is no file at the specified path (" + path.toAbsolutePath() + ")",
          propertyName
      );
    }

    return path;
  }

  private void thrownOptiFineException(String message, String propertyKey)
      throws OptiFineException {
    throw new OptiFineException(message + "(" + propertyKey + ")");
  }


  public Path handle(OptiFineVersion version, Path optifinePath) throws Exception {
    Path directory = optifinePath.getParent();
    Path output = directory.resolve(version.getQualifiedJarName() + "-DEV.jar");

    if (Files.exists(output)) {
      return output;
    }

    try (IsolatedClassLoader loader = new IsolatedClassLoader()) {
      loader.addPath(optifinePath);
      File moddedOptifine = directory.resolve(version.getQualifiedJarName() + "-MOD.jar")
          .toFile();

      Builder renamerBuilder = Renamer.builder();
      renamerBuilder.lib(this.minecraftJarPath.toFile());
      OptiFineInstaller.executeInstaller(
          loader,
          this.minecraftJarPath,
          optifinePath.toFile(),
          moddedOptifine
      );

      Path strippedJar = directory.resolve(version.getQualifiedJarName() + "-STRIPPED.jar");

      ZipTransformer transformer = ZipTransformer.createDefault(
          moddedOptifine.toPath(),
          strippedJar
      );
      transformer.addTransformer(
          new EntryTransformer<ClassEntry>(entry -> entry instanceof ClassEntry) {
            @Override
            public ClassEntry process(ClassEntry entry) {

              String name = entry.getName();

              if (name.startsWith("srg/")) {
                return null;
              }

              if (!name.startsWith("notch/")) {
                return entry;
              }

              return new ClassEntry(
                  name.replace("notch/", ""),
                  entry.getTime(),
                  entry.getData()
              );
            }
          });
      transformer.transform();

      try (InputStream stream = OptiFineDevHandler.class.getResourceAsStream(
          "/" + version.getMinecraftVersion() + ".tsrg")) {
        if (stream == null) {
          moddedOptifine = strippedJar.toFile();
        } else {
          this.applyCustomOFMappings(strippedJar, moddedOptifine.toPath(), stream);
        }
      } catch (IOException exception) {
        moddedOptifine = strippedJar.toFile();
      }

      IMappingFile mappingFile;
      try (InputStream stream = Files.newInputStream(this.mappingPath)) {
        mappingFile = IMappingFile.load(stream);
      }

      String name = this.mappingPath.getFileName().toString();
      if (!name.endsWith("srg") && !name.endsWith("tsrg")) {
        mappingFile = mappingFile.reverse();
      }

      renamerBuilder.add(new SeargeTransformer());
      renamerBuilder.add(Transformer.renamerFactory(mappingFile));
      renamerBuilder.add(new NotchMoveTransformer());

      renamerBuilder.input(moddedOptifine);
      renamerBuilder.output(output.toFile());
      StringBuilder builder = LogWriter.INSTANCE.builder();
      renamerBuilder.logger(content -> builder.append(content).append(System.lineSeparator()));

      Renamer build = renamerBuilder.build();
      build.run();
      while (true) {
        if (!build.isRunning()) {
          break;
        }
      }

      LogWriter.INSTANCE.write(directory.resolve("optifine-mojang-mappings-log.txt"));
      return output;
    }
  }

  private void applyCustomOFMappings(
      Path input,
      Path output,
      InputStream stream
  ) throws IOException {
    IMappingFile mappingFile = IMappingFile.load(stream);

    Builder renamerBuilder = Renamer.builder();
    renamerBuilder.add(Transformer.renamerFactory(mappingFile));

    renamerBuilder.lib(this.minecraftJarPath.toFile());

    renamerBuilder.input(input.toFile());
    renamerBuilder.output(output.toFile());

    StringBuilder builder = LogWriter.INSTANCE.builder();
    renamerBuilder.logger(content -> builder.append(content).append(System.lineSeparator()));

    Renamer build = renamerBuilder.build();
    build.run();
    while (true) {
      if (!build.isRunning()) {
        break;
      }
    }

    Path directory = output.getParent();
    LogWriter.INSTANCE.write(directory.resolve("optifine-custom-mappings-log.txt"));
  }

  public static class SeargeTransformer implements Transformer {

    @Override
    public ClassEntry process(ClassEntry entry) {
      if (entry.getName().startsWith("srg")) {
        return null;
      }
      return Transformer.super.process(entry);
    }

  }

  public static class NotchMoveTransformer implements Transformer {

    @Override
    public ClassEntry process(ClassEntry entry) {
      String name = entry.getName();
      if (name.startsWith("net/minecraft/") || name.startsWith("com/mojang/")) {
        return ClassEntry.create("notch/" + name, entry.getTime(), entry.getData());
      }

      return Transformer.super.process(entry);
    }
  }
}