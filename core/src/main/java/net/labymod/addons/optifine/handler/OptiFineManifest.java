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

import java.lang.Runtime.Version;
import java.util.ArrayList;
import java.util.List;

public final class OptiFineManifest {

  private final List<OptiFineVersion> versions = new ArrayList<>();

  public OptiFineVersion findVersion(String gameVersion){
    for (OptiFineVersion version : this.versions) {
      if (version.getMinecraftVersion().equals(gameVersion)) {
        return version;
      }
    }

    return null;
  }

  public List<OptiFineVersion> getVersions() {
    return this.versions;
  }

}
