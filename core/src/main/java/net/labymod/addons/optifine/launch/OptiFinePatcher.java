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
  private final boolean legacyVersion;
  private final boolean developmentEnvironment;

  public OptiFinePatcher(boolean legacyVersion, boolean developmentEnvironment) {
    this.patchers = new HashMap<>();

    this.legacyVersion = legacyVersion;
    this.developmentEnvironment = developmentEnvironment;

    this.registerPatcher("optifine/OptiFineClassTransformer", "", new OptiFineTransformerPatcher());
    this.registerPatcher("net/optifine/shaders/gui/GuiButtonDownloadShaders", new OptiFineShaderDownloadButtonPatcher());
    this.registerPatcher("net/optifine/gui/GuiButtonOF", new OptiFineWidgetIdentifierPatcher());
    this.registerPatcher("net/optifine/shaders/Shaders", new OptiFineShadersPatcher());
  }

  private String getPrefix() {
    if (this.legacyVersion) {
      return "";
    }

    return this.developmentEnvironment ? "" : "notch/";
  }

  public void registerPatcher(String className, Patcher patcher) {
    this.registerPatcher(className, this.getPrefix(), patcher);
  }

  public void registerPatcher(String className, String prefix, Patcher patcher) {
    this.patchers.computeIfAbsent(prefix + className, l -> new ArrayList<>()).add(patcher);
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
