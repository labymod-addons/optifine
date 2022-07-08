package net.labymod.addons.optifine.launch;

import org.objectweb.asm.tree.ClassNode;

public interface Patcher {

  void patch(ClassNode node);

}
