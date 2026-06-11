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
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Rewires {@code optifine.OptiFineClassTransformer.<init>} to open the prepared OptiFine jar
 * instead of self-locating via its {@code CodeSource}. In this environment the class is served from
 * the prepared jar through the game classloader, so its code source is a {@code jar:} entry URL and
 * {@code new File(url.toURI())} would fail, leaving the transformer without its zip file. The
 * constructor is invoked by {@code OptiFineEntrypoint} after the prepared jar is registered; stock
 * OptiFine constructs this class from its tweaker, and its runtime (Reflector resource loading,
 * OptiFineResourceLocator) expects the instance to exist and serve resources.
 */
public class OptiFineTransformerPatcher implements Patcher {

  private static final String ENTRYPOINT = "net/labymod/addons/optifine/launch/OptiFineEntrypoint";

  @Override
  public void patch(ClassNode node) {
    for (MethodNode method : node.methods) {
      if (!method.name.equals("<init>")) {
        continue;
      }

      for (AbstractInsnNode instruction : method.instructions) {
        if (!(instruction instanceof MethodInsnNode methodInstruction)
            || instruction.getOpcode() != Opcodes.INVOKESPECIAL
            || !methodInstruction.owner.equals("java/io/File")
            || !methodInstruction.name.equals("<init>")
            || !methodInstruction.desc.equals("(Ljava/net/URI;)V")) {
          continue;
        }

        InsnList replacement = new InsnList();
        replacement.add(new InsnNode(Opcodes.POP));
        replacement.add(new MethodInsnNode(
            Opcodes.INVOKESTATIC,
            ENTRYPOINT,
            "optifineUri",
            "()Ljava/net/URI;"
        ));
        method.instructions.insertBefore(instruction, replacement);
        return;
      }
    }
  }
}
