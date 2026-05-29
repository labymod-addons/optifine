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
package net.labymod.addons.optifine.launch.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import net.labymod.addons.optifine.launch.OptiFineEntrypoint;
import net.minecraft.launchwrapper.IClassTransformer;

/**
 * Substitutes the pre-remapped, named OptiFine bytes for Minecraft/OptiFine classes as they are
 * loaded. The prepared jar is opened lazily on first use.
 *
 * <p>This is the only mechanism that injects OptiFine's modified Minecraft classes, so it fails
 * closed: a class that should be substituted but cannot be read aborts loudly rather than silently
 * falling back to vanilla bytes (which would leave the game in a broken half-modded state). Only the
 * benign case of a class OptiFine never modified falls through to the original bytes.
 */
public class WrappedOptiFineTransformer implements IClassTransformer {

  private JarFile preparedJar;

  @Override
  public byte[] transform(String name, String transformedName, byte... classData) {
    if (name == null || !this.isSubstitutable(name)) {
      return classData;
    }

    JarFile jar = this.preparedJar();
    ZipEntry entry = jar.getEntry(name.replace('.', '/') + ".class");
    if (entry == null) {
      return classData;
    }

    try (InputStream input = jar.getInputStream(entry)) {
      return input.readAllBytes();
    } catch (IOException exception) {
      throw new IllegalStateException("Failed to substitute OptiFine class " + name, exception);
    }
  }

  private synchronized JarFile preparedJar() {
    if (this.preparedJar == null) {
      URI uri = OptiFineEntrypoint.optifineUri();
      if (uri == null) {
        throw new IllegalStateException("Prepared OptiFine jar URI is not initialized");
      }

      try {
        this.preparedJar = new JarFile(Path.of(uri).toFile());
      } catch (IOException exception) {
        throw new IllegalStateException("Failed to open prepared OptiFine jar at " + uri, exception);
      }
    }

    return this.preparedJar;
  }

  private boolean isSubstitutable(String name) {
    return name.startsWith("net.minecraft.")
        || name.startsWith("com.mojang.")
        || name.startsWith("net.optifine.")
        || name.startsWith("optifine.");
  }

  @Override
  public int getPriority() {
    return 0;
  }
}
