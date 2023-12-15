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

package net.labymod.addons.optifine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.labymod.addons.optifine.client.gfx.renderer.shadow.OptiFineShadowRenderPassContext;
import net.labymod.addons.optifine.core.generated.DefaultReferenceStorage;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.widget.converter.MinecraftWidgetType;
import net.labymod.api.client.gui.screen.widget.converter.WidgetConverterRegistry;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.addon.lifecycle.AddonPostEnableEvent;
import net.labymod.api.event.client.render.shader.ShaderPipelineContextEvent;
import net.labymod.api.models.addon.annotation.AddonMain;
import net.labymod.api.reference.ReferenceStorageAccessor;
import net.labymod.api.reference.ReferenceStorageFinder;
import net.labymod.api.util.logging.Logging;

@AddonMain
public class OptiFineAddon {

  private static final Logging LOGGER = Logging.getLogger();
  private static final String GUI_PACKAGE_NAME = "net.optifine.gui";
  private static final String SHADER_GUI_PACKAGE_NAME = "net.optifine.shaders.gui";
  private final Map<MinecraftWidgetType, List<String>> widgets;
  private DefaultReferenceStorage referenceStorage;

  public OptiFineAddon() {
    this.widgets = new HashMap<>();
    this.registerWidgets(
        MinecraftWidgetType.BUTTON,
        GUI_PACKAGE_NAME,
        "GuiButtonOF",
        "GuiOptionButtonOF",
        "GuiScreenButtonOF"
    );
    this.registerWidgets(
        MinecraftWidgetType.SLIDER,
        GUI_PACKAGE_NAME,
        "GuiOptionSliderOF"
    );

    this.registerWidgets(
        MinecraftWidgetType.BUTTON,
        SHADER_GUI_PACKAGE_NAME,
        "GuiButtonDownloadShaders",
        "GuiButtonEnumShaderOption",
        "GuiButtonShaderOption"
    );

    this.registerWidgets(
        MinecraftWidgetType.BUTTON,
        SHADER_GUI_PACKAGE_NAME,
        "GuiSliderShaderOption"
    );

    ReferenceStorageAccessor accessor = ReferenceStorageFinder.find(this.getClass().getClassLoader());


    if (accessor instanceof DefaultReferenceStorage referenceStorage) {
      this.referenceStorage = referenceStorage;
    }

    if (this.referenceStorage == null) {
      LOGGER.error("Failed to find reference storage accessor");
    }

  }

  @Subscribe
  public void onAddonPostEnable(AddonPostEnableEvent event) {

    if (this.referenceStorage != null) {
      this.referenceStorage.screenHandler().initialize();
    }

    WidgetConverterRegistry registry = Laby.references().widgetConverterRegistry();
    this.widgets.forEach((key, classNames) -> {
      for (String className : classNames) {
        try {
          registry.registerIfPresent(key.toString(), Class.forName(className));
        } catch (Exception exception) {
          if (exception instanceof ClassNotFoundException) {
            LOGGER.warn("{} could not be found!", className);
            continue;
          }

          LOGGER.warn(
              "An error occurred during the registration of class {}",
              className,
              exception
          );
        }
      }
    });
  }

  @Subscribe
  public void onShadowRenderPassContext(ShaderPipelineContextEvent event) {
    if (this.referenceStorage != null) {
      event.setShadowRenderPassContext(new OptiFineShadowRenderPassContext(this.referenceStorage.shaderAccessor()));
    }
  }

  private void registerWidgets(MinecraftWidgetType type, String packageName, String... classNames) {
    List<String> names = this.widgets.computeIfAbsent(
        type,
        list -> new ArrayList<>(classNames.length)
    );

    if (!packageName.endsWith(".")) {
      packageName += ".";
    }

    for (String className : classNames) {
      className = packageName + className;
      names.add(className);
    }

  }
}
