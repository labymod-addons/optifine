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

import java.io.IOException;
import net.labymod.addons.optifine.handler.download.DownloadService;
import net.labymod.addons.optifine.handler.download.LabyModDownloadService;
import net.labymod.addons.optifine.handler.download.OptiFineDownloadService;
import net.labymod.api.models.version.Version;

public class OptifineDownloader {

  private boolean shouldUseFallback = false;
  private DownloadService downloadService;

  public void download(Version version) throws IOException {
    this.downloadService =
        this.shouldUseFallback ?
            new LabyModDownloadService() :
            new OptiFineDownloadService();
    this.downloadService.download(version);
  }

  public DownloadService getDownloadService() {
    return this.downloadService;
  }
}
