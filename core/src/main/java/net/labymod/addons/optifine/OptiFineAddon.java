package net.labymod.addons.optifine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.labymod.addons.optifine.client.gfx.renderer.shadow.OptiFineShadowRenderPassContext;
import net.labymod.api.Laby;
import net.labymod.api.LabyAPI;
import net.labymod.api.client.gui.screen.widget.converter.MinecraftWidgetType;
import net.labymod.api.client.gui.screen.widget.converter.WidgetConverterRegistry;
import net.labymod.api.event.EventBus;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.addon.lifecycle.AddonPostEnableEvent;
import net.labymod.api.event.client.render.shadow.ShadowRenderPassContextEvent;
import net.labymod.api.models.addon.annotation.AddonListener;
import net.labymod.api.util.logging.Logging;
import javax.inject.Inject;

@AddonListener
public class OptiFineAddon {

  private static final String GUI_PACKAGE_NAME = "net.optifine.gui";
  private static final String SHADER_GUI_PACKAGE_NAME = "net.optifine.shaders.gui";
  private final Map<MinecraftWidgetType, List<String>> widgets;

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
        MinecraftWidgetType.SLIDER,
        SHADER_GUI_PACKAGE_NAME,
        "GuiSliderShaderOption"
    );

  }

  @SuppressWarnings("unchecked")
  @Subscribe
  public void onAddonPostEnable(AddonPostEnableEvent event) {
    LabyAPI labyAPI = Laby.labyAPI();
    Logging logger = Logging.create("Optifine");
    WidgetConverterRegistry registry = labyAPI.widgetConverterRegistry();
    this.widgets.forEach((key, classNames) -> {
      for (String className : classNames) {
        try {
          registry.registerIfPresent(key.toString(), Class.forName(className));
        } catch (Exception exception) {
          if (exception instanceof ClassNotFoundException) {
            logger.warn("{} could not be found!", className);
            continue;
          }

          logger.warn(
              "An error occurred during the registration of class {}",
              className,
              exception
          );
        }
      }

    });
  }

  @Subscribe
  public void onShadowRenderPassContext(ShadowRenderPassContextEvent event) {
    event.setContext(new OptiFineShadowRenderPassContext());
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
