package net.labymod.addons.optifine.launch.patches;

import net.labymod.addons.optifine.launch.Patcher;
import net.labymod.core.loader.DefaultLabyModLoader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class OptiFineTransformerPatcher implements Patcher {

  private static final String NAME = "net/labymod/addons/optifine/launch/OptiFineEntrypoint";

  @Override
  public void patch(ClassNode node) {
    for (MethodNode method : node.methods) {
      if (method.name.equals("<init>")) {
        for (AbstractInsnNode instruction : method.instructions) {
          if (instruction instanceof MethodInsnNode
              && instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
            MethodInsnNode methodNode = (MethodInsnNode) instruction;

            if (!methodNode.name.equals("toURI")) {
              continue;
            }

            InsnList list = new InsnList();
            list.insert(
                new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    NAME,
                    "optifineUri",
                    "()Ljava/net/URI;"
                )
            );
            list.insert(new VarInsnNode(Opcodes.ASTORE, 2));
            method.instructions.insert(instruction, list);
          }

        }
        continue;
      }

      if (
          DefaultLabyModLoader.getInstance().isLabyModDevelopmentEnvironment() &&
              method.name.equals("getOptiFineResourceZip")
      ) {
        AbstractInsnNode first = method.instructions.getFirst();

        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(
            new FieldInsnNode(
                Opcodes.GETFIELD,
                "optifine/OptiFineClassTransformer",
                "ofZipFile",
                "Ljava/util/zip/ZipFile;"
            )
        );

        LabelNode labelNode = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNULL, labelNode));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(
            new FieldInsnNode(
                Opcodes.GETFIELD,
                "optifine/OptiFineClassTransformer",
                "ofZipFile",
                "Ljava/util/zip/ZipFile;"
            )
        );
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, NAME, "readDev", "(Ljava/lang/String;Ljava/util/zip/ZipFile;)[B"));
        list.add(new InsnNode(Opcodes.ARETURN));
        list.add(labelNode);
        method.instructions.insertBefore(first, list);
      }

    }

  }
}
