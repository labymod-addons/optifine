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

package net.labymod.addons.optifine.util;

import java.util.List;
import net.labymod.addons.optifine.launch.OptiFinePatcher;
import net.labymod.addons.optifine.launch.Patcher;
import net.labymod.api.util.io.zip.EntryTransformer;
import net.labymod.api.util.io.zip.entry.ClassEntry;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class PatchApplierClassEntryTransformer extends EntryTransformer<ClassEntry> {

  private final OptiFinePatcher optiFinePatcher;

  public PatchApplierClassEntryTransformer(OptiFinePatcher optiFinePatcher) {
    super(entry -> entry instanceof ClassEntry);
    this.optiFinePatcher = optiFinePatcher;
  }

  @Override
  public ClassEntry process(ClassEntry entry) {
    List<Patcher> patches = this.optiFinePatcher.getPatchers().get(entry.getClassName());

    byte[] patchedData = null;
    if (patches != null) {
      byte[] data = null;
      for (Patcher patcher : patches) {
        data = this.applyPatch(data == null ? entry.getData() : data, patcher);
      }

      patchedData = data;
    }

    return patchedData == null ? entry : new ClassEntry(
        entry.getName(),
        entry.getTime(),
        patchedData
    );
  }

  private byte[] applyPatch(byte[] classData, Patcher patcher) {
    ClassNode classNode = new ClassNode();
    ClassReader classReader = new ClassReader(classData);
    classReader.accept(classNode, 0);

    patcher.patch(classNode);

    ClassWriter classWriter = new ClassWriter(
        classReader,
        ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES
    );
    classNode.accept(classWriter);

    return classWriter.toByteArray();
  }
}
