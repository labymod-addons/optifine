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
