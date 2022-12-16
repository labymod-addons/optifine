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
