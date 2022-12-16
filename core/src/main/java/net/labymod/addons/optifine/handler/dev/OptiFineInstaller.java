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
import java.lang.reflect.Method;
import java.nio.file.Path;
import net.labymod.addons.optifine.exception.OptiFineException;
import net.labymod.api.util.IsolatedClassLoader;

public class OptiFineInstaller {

  public static void executeInstaller(
      IsolatedClassLoader loader,
      Path minecraftJar,
      File installer,
      File output
  ) throws OptiFineException {
    if (output.exists()) {
      return;
    }

    executeInstaller(loader, minecraftJar.toFile(), installer, output);
  }

  public static void executeInstaller(
      IsolatedClassLoader loader,
      File minecraftJar,
      File installer,
      File output
  ) throws OptiFineException {
    try {
      Class<?> patcherClass = loader.loadClass("optifine.Patcher");
      Method method = patcherClass.getDeclaredMethod("process", File.class, File.class, File.class);
      method.invoke(null, minecraftJar, installer, output);
    } catch (Exception exception) {
      throw new OptiFineException("Could not execute optifine installer...", exception);
    }
  }

}
