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

package net.labymod.addons.optifine.handler.download;

import java.io.IOException;
import net.labymod.addons.optifine.handler.OptiFineVersion;
import net.labymod.api.models.version.Version;

// TODO: 23.09.2021 Implement fallback service
public class LabyModDownloadService extends DownloadService {

  @Override
  public void download(Version version) throws IOException {

  }

  @Override
  public OptiFineVersion currentOptiFineVersion() {
    return null;
  }
}
