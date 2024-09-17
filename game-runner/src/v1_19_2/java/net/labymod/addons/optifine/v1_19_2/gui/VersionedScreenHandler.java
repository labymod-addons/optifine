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

package net.labymod.addons.optifine.v1_19_2.gui;

import net.labymod.addons.optifine.gui.OptiFineScreen;
import net.labymod.addons.optifine.gui.ScreenHandler;
import net.labymod.api.client.gui.screen.game.GameScreen;
import net.labymod.api.models.Implements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.optifine.gui.*;
import net.optifine.shaders.gui.GuiShaders;
import javax.inject.Singleton;
import java.util.function.BiFunction;

@Singleton
@Implements(ScreenHandler.class)
public class VersionedScreenHandler extends ScreenHandler<Screen, Options> {

  @Override
  public void onInitialize() {
    this.registerOptions(OptiFineScreen.ANIMATION, GuiAnimationSettingsOF.class, GuiAnimationSettingsOF::new);
    this.registerOptions(OptiFineScreen.DETAIL, GuiDetailSettingsOF.class, GuiDetailSettingsOF::new);
    this.registerOptions(OptiFineScreen.OTHER, GuiOtherSettingsOF.class, GuiOtherSettingsOF::new);
    this.registerOptions(OptiFineScreen.PERFORMANCE, GuiPerformanceSettingsOF.class, GuiPerformanceSettingsOF::new);
    this.registerOptions(OptiFineScreen.QUALITY, GuiQualitySettingsOF.class, GuiQualitySettingsOF::new);
    this.registerOptions(OptiFineScreen.SHADERS, GuiShaders.class, GuiShaders::new);
  }

  @Override
  public void registerOptions(
      GameScreen screen,
      Class<?> screenClass,
      BiFunction<Screen, Options, Screen> screenFactory
  ) {
    this.register(
        screen,
        screenClass,
        () -> screenFactory.apply(Minecraft.getInstance().screen, Minecraft.getInstance().options)
    );
  }

}
