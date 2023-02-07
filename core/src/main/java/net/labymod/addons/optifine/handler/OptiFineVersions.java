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

package net.labymod.addons.optifine.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

public final class OptiFineVersions {

  private static final List<OptiFineVersion> VERSIONS = new ArrayList<>();
  public static final OptiFineVersion OPTIFINE_1_8_9 = create("1.8.9", "HD_U_M5");
  public static final OptiFineVersion OPTIFINE_1_12_2 = create("1.12.2", "HD_U_G5");
  public static final OptiFineVersion OPTIFINE_1_17_1 = create("1.17.1", "HD_U_H1");
  public static final OptiFineVersion OPTIFINE_1_18_2 = create("1.18.2", "HD_U_H7");
  public static final OptiFineVersion OPTIFINE_1_19_2 = create("1.19.2", "HD_U_I1");
  public static final OptiFineVersion OPTIFINE_1_19_3 = create("1.19.3", "HD_U_I2_pre3", true);

  private static @NotNull OptiFineVersion create(String minecraftVersion, String optifineVersion) {
    return OptiFineVersions.create(minecraftVersion, optifineVersion, false);
  }

  private static @NotNull OptiFineVersion create(
      String minecraftVersion,
      String optifineVersion,
      boolean preview) {
    OptiFineVersion version = new OptiFineVersion(minecraftVersion, optifineVersion, preview);
    VERSIONS.add(version);
    return version;
  }

  public static @Nullable OptiFineVersion getVersion(String version) {
    for (OptiFineVersion optifineVersion : VERSIONS) {
      if (optifineVersion.getMinecraftVersion().equalsIgnoreCase(version)) {
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
