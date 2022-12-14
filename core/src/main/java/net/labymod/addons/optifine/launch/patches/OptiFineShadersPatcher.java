package net.labymod.addons.optifine.launch.patches;

import net.labymod.addons.optifine.launch.Patcher;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class OptiFineShadersPatcher implements Patcher {

  @Override
  public void patch(ClassNode node) {
    for (FieldNode field : node.fields) {
      field.access =
          (field.access & ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PUBLIC;
    }

  }
}
