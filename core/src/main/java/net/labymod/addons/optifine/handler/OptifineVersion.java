package net.labymod.addons.optifine.handler;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class OptifineVersion {

  private final String minecraftVersion;
  private final String optifineVersion;

  public OptifineVersion(String minecraftVersion, String optifineVersion) {
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
    return "Optifine_" + this.minecraftVersion + "_" + this.optifineVersion;
  }

}
