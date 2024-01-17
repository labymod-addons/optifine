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
import net.labymod.api.client.gui.screen.game.ScreenTags;
import net.labymod.api.client.gui.screen.game.SimpleGameScreen;
import net.labymod.api.tag.Tag;
import java.util.HashMap;
import java.util.Map;

public class OptiFineScreen {
  private static final Map<String, GameScreen> SCREENS = new HashMap<>();
  public static final GameScreen ANIMATION = createScreen("animation", true, ScreenTags.OPTIONS);
  public static final GameScreen DETAIL = createScreen("detail", true, ScreenTags.OPTIONS);
  public static final GameScreen OTHER = createScreen("other", true, ScreenTags.OPTIONS);
  public static final GameScreen PERFORMANCE = createScreen("performance", true, ScreenTags.OPTIONS);
  public static final GameScreen QUALITY = createScreen("quality", true, ScreenTags.OPTIONS);
  public static final GameScreen SHADERS = createScreen("shaders", true, ScreenTags.OPTIONS);


  private static GameScreen createScreen(String id, boolean allowCustomFont, Tag... tags) {
    var newScreen = new SimpleGameScreen(id, allowCustomFont, tags);
    if (SCREENS.containsKey(id)) {
      throw new IllegalStateException("Screen with id " + id + " already exists");
    }

    SCREENS.put(id, newScreen);
    return newScreen;
  }

  public static void register() {
    GameScreenRegistry registry = Laby.references().gameScreenRegistry();
    for (var screen : SCREENS.values()) {
      registry.register(screen);
    }
  }

}