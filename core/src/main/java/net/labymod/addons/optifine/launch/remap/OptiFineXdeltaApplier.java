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
package net.labymod.addons.optifine.launch.remap;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import net.labymod.addons.optifine.exception.OptiFineException;

public final class OptiFineXdeltaApplier {

  private static final String PATCHER_CLASS = "optifine.Patcher";

  private OptiFineXdeltaApplier() {
  }

  public static void apply(Path obfuscatedClientJar, Path optifineJar, Path destination)
      throws OptiFineException {
    try (IsolatedClassLoader loader = new IsolatedClassLoader()) {
      loader.addPath(optifineJar);

      Class<?> patcherClass = loader.loadClass(PATCHER_CLASS);
      Method processMethod = patcherClass.getDeclaredMethod("process", File.class, File.class, File.class);
      processMethod.invoke(
          null,
          obfuscatedClientJar.toFile(),
          optifineJar.toFile(),
          destination.toFile()
      );
    } catch (InvocationTargetException exception) {
      // optifine.Patcher.process MD5-validates its patches against the obfuscated client jar, so the
      // cause here is most often a mismatched client jar. Preserve it for a precise log.
      Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
      throw new OptiFineException(
          "optifine.Patcher.process failed (likely an obfuscated client jar mismatch)",
          cause
      );
    } catch (ReflectiveOperationException | IOException exception) {
      throw new OptiFineException("Failed to invoke optifine.Patcher.process", exception);
    }
  }
}
