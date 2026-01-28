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
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;
import java.util.Map;

public class MixinOptiFineBufferSourceTransformer implements IClassTransformer {

  private static final String NAME = "net.labymod.v1_16_5.mixins.compatibility.optifine.MixinOptiFineBufferSource";
  private static final Map<String, String> DEFAULT_MAPPINGS = Map.of("net/labymod/v1_16_5/mixins/compatibility/optifine/MixinOptiFineBufferSource.lastState", "c");

  @Override
  public byte[] transform(String name, String transformedName, byte... classData) {
    if (!NAME.equals(name)) {
      return classData;
    }

    ClassReader reader = new ClassReader(classData);
    ClassNode node = new ClassNode();

    ClassRemapper remapper = new ClassRemapper(node, new SimpleRemapper(DEFAULT_MAPPINGS));
    reader.accept(remapper, 0);

    ClassWriter writer = new ClassWriter(0);
    node.accept(writer);
    return writer.toByteArray();
  }
}
