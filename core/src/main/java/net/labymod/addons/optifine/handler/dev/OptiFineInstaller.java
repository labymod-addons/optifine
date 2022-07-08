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
