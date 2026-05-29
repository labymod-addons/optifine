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
package net.labymod.addons.optifine.launch.prepare;

import java.nio.file.Path;
import net.labymod.addons.optifine.handler.OptiFineVersion;

/**
 * Immutable inputs shared by every {@link PreparationStage}, plus a scratch directory the stages
 * write their intermediate jars to.
 */
public class PreparationContext {

  private final Path rawOptiFineJar;
  private final Path obfuscatedClientJar;
  private final String gameVersion;
  private final OptiFineVersion optiFineVersion;
  private final Path scratchDirectory;

  public PreparationContext(
      Path rawOptiFineJar,
      Path obfuscatedClientJar,
      String gameVersion,
      OptiFineVersion optiFineVersion,
      Path scratchDirectory
  ) {
    this.rawOptiFineJar = rawOptiFineJar;
    this.obfuscatedClientJar = obfuscatedClientJar;
    this.gameVersion = gameVersion;
    this.optiFineVersion = optiFineVersion;
    this.scratchDirectory = scratchDirectory;
  }

  public Path scratch(String fileName) {
    return this.scratchDirectory.resolve(
        this.optiFineVersion.getQualifiedJarName() + "-" + fileName
    );
  }

  public Path rawOptiFineJar() {
    return this.rawOptiFineJar;
  }

  public Path obfuscatedClientJar() {
    return this.obfuscatedClientJar;
  }

  public String gameVersion() {
    return this.gameVersion;
  }

  public OptiFineVersion optiFineVersion() {
    return this.optiFineVersion;
  }
}
