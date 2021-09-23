package net.labymod.addons.optifine.handler;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class OptifineVersions {

  private static final List<OptifineVersion> VERSIONS = new ArrayList<>();
  public static final OptifineVersion OPTIFINE_1_8_9 = create("1.8.9", "HD_U_M5");
  public static final OptifineVersion OPTIFINE_1_17_1 = create("1.17.1", "HD_U_G9");

  private static @NotNull OptifineVersion create(String minecraftVersion, String optifineVersion) {
    OptifineVersion version = new OptifineVersion(minecraftVersion, optifineVersion);
    VERSIONS.add(version);
    return version;
  }

  public static @Nullable OptifineVersion getVersion(String version) {
    for (OptifineVersion optifineVersion : VERSIONS) {
      if(optifineVersion.getMinecraftVersion().equalsIgnoreCase(version)) {
        return optifineVersion;
      }
    }
    return null;
  }

  @Contract(pure = true)
  public static @NotNull @UnmodifiableView List<OptifineVersion> getVersions() {
    return Collections.unmodifiableList(OptifineVersions.VERSIONS);
  }


}
