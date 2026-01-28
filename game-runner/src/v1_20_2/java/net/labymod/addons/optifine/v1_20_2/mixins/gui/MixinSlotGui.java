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
package net.labymod.addons.optifine.v1_20_2.mixins.gui;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.optifine.gui.SlotGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlotGui.class)
public class MixinSlotGui {

  @Unique
  private GuiGraphics labyMod$guiGraphics;

  @Inject(method = "render", at = @At("HEAD"), remap = false)
  private void labyMod$storeGuiGraphics(
      GuiGraphics graphics,
      int p_render_1_,
      int p_render_2_,
      float p_render_3_,
      CallbackInfo ci
  ) {
    this.labyMod$guiGraphics = graphics;
  }

  @Redirect(method = {"renderList", "render", "renderHoleBackground"}, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;vertex(DDD)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
  private VertexConsumer labyMod$applyMatrix(BufferBuilder instance, double x, double y, double z) {
    return instance.vertex(this.labyMod$guiGraphics.pose().last().pose(), (float) x, (float) y, (float) z);
  }

}
