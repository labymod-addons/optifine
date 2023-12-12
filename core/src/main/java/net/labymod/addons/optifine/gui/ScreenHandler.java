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

package net.labymod.addons.optifine.gui;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.ScreenName;
import net.labymod.api.client.gui.screen.ScreenService;
import net.labymod.api.client.gui.screen.ScreenWrapper;
import net.labymod.api.client.gui.screen.game.GameScreen;
import net.labymod.api.reference.annotation.Referenceable;

@Referenceable
public abstract class ScreenHandler<VANILLA_SCREEN, VANILLA_OPTIONS> {

  private static final ScreenService SCREEN_SERVICE = Laby.references().screenService();

  public final void initialize() {
    OptiFineScreen.register();
    this.onInitialize();
  }

  protected abstract void onInitialize();

  public abstract void registerOptions(
      GameScreen screen,
      Class<?> screenClass,
      BiFunction<VANILLA_SCREEN, VANILLA_OPTIONS, VANILLA_SCREEN> screenFactory
  );

  public void register(GameScreen screen, Class<?> screenClass,
      Supplier<VANILLA_SCREEN> screenFactory) {
    SCREEN_SERVICE.register(screen, ScreenName.optifine(screenClass));
    SCREEN_SERVICE.registerFactory(screen, () -> this.createScreen(screenFactory.get()));
  }

  public abstract ScreenWrapper createScreen(VANILLA_SCREEN screen);

}
