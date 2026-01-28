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
package net.labymod.addons.optifine.launch.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class GLXTransformer implements IClassTransformer {

  public static final String NAME = "com.mojang.blaze3d.platform.GLX";

  @Override
  public byte[] transform(String name, String transformedName, byte... classData) {
    if (!NAME.equals(name)) {
      return classData;
    }

    ClassNode classNode = new ClassNode();
    ClassReader reader = new ClassReader(classData);
    reader.accept(classNode, 0);

    this.isUsingFBOs(classNode);
    this.useVbo(classNode);

    ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    classNode.accept(writer);

    byte[] data = writer.toByteArray();

    return data;
  }

  private void isUsingFBOs(ClassNode node) {
    MethodNode method = this.createMethod("isUsingFBOs", "()Z");

    LabelNode label = new LabelNode();

    InsnList instructions = method.instructions;
    instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/optifine/Config", "isAntialiasing", "()Z"));
    instructions.add(new JumpInsnNode(Opcodes.IFEQ, label));

    instructions.add(new InsnNode(Opcodes.ICONST_0));
    instructions.add(new InsnNode(Opcodes.IRETURN));

    instructions.add(label);
    instructions.add(new InsnNode(Opcodes.ICONST_1));
    instructions.add(new InsnNode(Opcodes.IRETURN));

    node.methods.add(method);
  }

  private void useVbo(ClassNode node) {

    MethodNode method = this.createMethod("useVbo", "()Z");

    InsnList instructions = method.instructions;
    instructions.add(new InsnNode(Opcodes.ICONST_1));
    instructions.add(new InsnNode(Opcodes.IRETURN));

    node.methods.add(method);
  }


  private MethodNode createMethod(String name, String descriptor) {
    return new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, name, descriptor, null, null);
  }

}
