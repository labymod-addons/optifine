package net.labymod.addons.optifine.handler;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class OptiFineVersion {

  private final String minecraftVersion;
  private final String optifineVersion;

  public OptiFineVersion(String minecraftVersion, String optifineVersion) {
    this.minecraftVersion = minecraftVersion;
    this.optifineVersion = optifineVersion;
  }

  public String getMinecraftVersion() {
    return this.minecraftVersion;
  }

  public String getOptifineVersion() {
    return this.optifineVersion;
  }

  @Contract(pure = true)
  public @NotNull String getQualifiedJarName() {
    return "OptiFine_" + this.minecraftVersion + "_" + this.optifineVersion;
  }

}
