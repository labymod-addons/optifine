package net.labymod.addons.optifine.launch;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.labymod.addons.optifine.handler.OptiFineVersion;
import net.labymod.addons.optifine.launch.patches.OptiFineTransformerPatcher;
import net.labymod.addons.optifine.util.PatchApplierClassEntryTransformer;
import net.labymod.api.util.io.zip.ZipTransformer;

public class OptiFinePatcher {

  private final Map<String, List<Patcher>> patchers;

  public OptiFinePatcher() {
    this.patchers = new HashMap<>();

    this.registerPatcher("optifine/OptiFineClassTransformer", new OptiFineTransformerPatcher());
  }

  public void registerPatcher(String className, Patcher patcher) {
    this.patchers.computeIfAbsent(className, l -> new ArrayList<>()).add(patcher);
  }

  public Map<String, List<Patcher>> patchers() {
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
