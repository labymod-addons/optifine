package net.labymod.addons.optifine.handler;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class OptiFineVersions {

  private static final List<OptiFineVersion> VERSIONS = new ArrayList<>();
  public static final OptiFineVersion OPTIFINE_1_8_9 = create("1.8.9", "HD_U_M5");
  public static final OptiFineVersion OPTIFINE_1_17_1 = create("1.17.1", "HD_U_H1");
  public static final OptiFineVersion OPTIFINE_1_18_2 = create("1.18.2", "HD_U_H7");
  public static final OptiFineVersion OPTIFINE_1_19_2 = create("1.19.2", "HD_U_H9");

  private static @NotNull OptiFineVersion create(String minecraftVersion, String optifineVersion) {
    OptiFineVersion version = new OptiFineVersion(minecraftVersion, optifineVersion);
    VERSIONS.add(version);
    return version;
  }

  public static @Nullable OptiFineVersion getVersion(String version) {
    for (OptiFineVersion optifineVersion : VERSIONS) {
      if(optifineVersion.getMinecraftVersion().equalsIgnoreCase(version)) {
        return optifineVersion;
      }
    }
    return null;
  }

  @Contract(pure = true)
  public static @NotNull @UnmodifiableView List<OptiFineVersion> getVersions() {
    return Collections.unmodifiableList(OptiFineVersions.VERSIONS);
  }


}
