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

import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.game.GameScreen;
import net.labymod.api.client.gui.screen.game.GameScreenRegistry;

public enum OptiFineScreen implements GameScreen {
  ANIMATION("animation", true, true),
  DETAIL("detail", true, true),
  OTHER("other", true, true),
  PERFORMANCE("performance", true, true),
  QUALITY("quality", true, true),
  SHADERS("shaders", true, true),
  ;
  private static final OptiFineScreen[] VALUES = values();

  private final String id;
  private final boolean allowCustomFont;
  private final boolean options;

  OptiFineScreen(String id, boolean allowCustomFont, boolean options) {
    this.id = id;
    this.allowCustomFont = allowCustomFont;
    this.options = options;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public boolean allowCustomFont() {
    return this.allowCustomFont;
  }

  @Override
  public boolean isOptions() {
    return this.options;
  }

  public static void register() {
    GameScreenRegistry registry = Laby.references().gameScreenRegistry();
    for (OptiFineScreen screen : VALUES) {
      registry.register(screen);
    }
  }

}