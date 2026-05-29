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
package net.labymod.addons.optifine.launch;

import java.nio.file.Path;
import net.labymod.api.Constants;

/**
 * Single source of truth for where OptiFine artifacts live, mirroring LabyMod's own client-jar
 * layout ({@code LABYMOD_DIRECTORY/client-jars/<version>/}). Shared by the downloader and the
 * patcher so both always resolve the same directory regardless of the process working directory.
 */
public final class OptiFineStorage {

  private OptiFineStorage() {
  }

  public static Path directory(String gameVersion) {
    return Constants.Files.LABYMOD_DIRECTORY
        .resolve("client-jars")
        .resolve(gameVersion)
        .resolve("optifine");
  }
}
