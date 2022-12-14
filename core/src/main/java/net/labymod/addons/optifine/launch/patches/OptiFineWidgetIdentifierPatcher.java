package net.labymod.addons.optifine.launch.patches;

import net.labymod.addons.optifine.launch.Patcher;
import net.labymod.api.client.gui.screen.widget.WidgetIdentifier;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class OptiFineWidgetIdentifierPatcher implements Patcher {

  @Override
  public void patch(ClassNode node) {
    node.interfaces.add(Type.getInternalName(WidgetIdentifier.class));

    if (node.name.endsWith("GuiButtonOF")) {

      MethodNode methodNode = new MethodNode(Opcodes.ACC_PUBLIC, "getIdentifier",
          "()Ljava/lang/String;", null, null);

      InsnList insnList = new InsnList();
      Type stringBuilderType = Type.getType(StringBuilder.class);

      insnList.add(new TypeInsnNode(Opcodes.NEW, stringBuilderType.getInternalName()));
      insnList.add(new InsnNode(Opcodes.DUP));
      insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, stringBuilderType.getInternalName(), "<init>", "()V"));
      insnList.add(new LdcInsnNode("of-"));
      insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, stringBuilderType.getInternalName(), "append", "(Ljava/lang/String;)" + stringBuilderType.getDescriptor()));


      insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
      insnList.add(new FieldInsnNode(Opcodes.GETFIELD, node.name, "id", "I"));

      insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, stringBuilderType.getInternalName(), "append", "(I)" + stringBuilderType.getDescriptor()));
      insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, stringBuilderType.getInternalName(), "toString", "()Ljava/lang/String;"));

      insnList.add(new InsnNode(Opcodes.ARETURN));

      methodNode.instructions.add(insnList);

      node.methods.add(methodNode);
    }
  }


}
