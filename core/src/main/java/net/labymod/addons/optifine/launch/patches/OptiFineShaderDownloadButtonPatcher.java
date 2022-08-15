package net.labymod.addons.optifine.launch.patches;

import net.labymod.addons.optifine.gui.ShaderDownloadButtonAccessor;
import net.labymod.addons.optifine.launch.Patcher;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

public class OptiFineShaderDownloadButtonPatcher implements Patcher {

  @Override
  public void patch(ClassNode node) {
    node.interfaces.add(Type.getInternalName(ShaderDownloadButtonAccessor.class));
  }


}
