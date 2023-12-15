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

package net.labymod.addons.optifine.v1_12_2.gui;

import java.util.function.BiFunction;
import javax.inject.Singleton;
import net.labymod.addons.optifine.gui.OptiFineScreen;
import net.labymod.addons.optifine.gui.ScreenHandler;
import net.labymod.api.client.gui.screen.ScreenWrapper;
import net.labymod.api.client.gui.screen.game.GameScreen;
import net.labymod.api.models.Implements;
import net.labymod.v1_12_2.client.gui.screen.VersionedScreenWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.optifine.gui.GuiAnimationSettingsOF;
import net.optifine.gui.GuiDetailSettingsOF;
import net.optifine.gui.GuiOtherSettingsOF;
import net.optifine.gui.GuiPerformanceSettingsOF;
import net.optifine.gui.GuiQualitySettingsOF;
import net.optifine.shaders.gui.GuiShaders;

@Singleton
@Implements(ScreenHandler.class)
public class VersionedScreenHandler extends ScreenHandler<GuiScreen, GameSettings> {

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
      BiFunction<GuiScreen, GameSettings, GuiScreen> screenFactory
  ) {
    this.register(
        screen,
        screenClass,
        () -> screenFactory.apply(Minecraft.getMinecraft().currentScreen, Minecraft.getMinecraft().gameSettings)
    );
  }

  @Override
  public ScreenWrapper createScreen(GuiScreen screen) {
    return new VersionedScreenWrapper(screen);
  }
}
