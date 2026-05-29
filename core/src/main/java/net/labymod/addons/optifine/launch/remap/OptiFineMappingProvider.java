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
package net.labymod.addons.optifine.launch.remap;

import net.labymod.addons.optifine.exception.OptiFineException;
import net.labymod.api.mapping.MappingNamespace;
import net.labymod.api.mapping.MappingService;
import net.labymod.api.mapping.provider.MappingProvider;

/**
 * Resolves the obfuscated -&gt; named mapping provider used to remap OptiFine.
 *
 * <p>OptiFine's own method renames (the {@code <version>.srg} overlay) are applied earlier, in the
 * obfuscated namespace, by {@code OverlayRenameStage}; by the time this provider is used the jar is a
 * plain obfuscated jar, so a straight obfuscated -&gt; named map is all that is needed.
 */
public final class OptiFineMappingProvider {

  private OptiFineMappingProvider() {
  }

  public static MappingProvider resolve() throws OptiFineException {
    MappingProvider base = MappingService.instance().mappings(
        MappingNamespace.MINECRAFT_OBFUSCATED,
        MappingNamespace.NAMED
    );
    if (base == null) {
      throw new OptiFineException(
          "No '" + MappingNamespace.MINECRAFT_OBFUSCATED + " -> " + MappingNamespace.NAMED
              + "' mapping provider available"
      );
    }

    return base;
  }
}
