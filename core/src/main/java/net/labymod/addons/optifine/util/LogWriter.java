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

package net.labymod.addons.optifine.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class LogWriter {

  public static final LogWriter INSTANCE = new LogWriter();
  private final StringBuilder builder;

  private LogWriter() {
    this.builder = new StringBuilder();
  }

  public StringBuilder builder() {
    return this.builder;
  }

  public void write(Path path) throws IOException {
    Files.write(path, this.builder.toString().getBytes(StandardCharsets.UTF_8));
    this.builder.setLength(0);
  }

}
