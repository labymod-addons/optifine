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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public final class JarEntryFilter {

  private static final String NOTCH_PREFIX = "notch/";
  private static final String SRG_PREFIX = "srg/";
  private static final String FORGE_PREFIX = "net/minecraftforge/";
  private static final String JAVAX_PREFIX = "javax/";

  private JarEntryFilter() {
  }

  /**
   * Drops {@code srg/} entries and strips the {@code notch/} prefix; every other entry is copied
   * through unchanged.
   */
  public static void stripSeargeAndNotch(Path source, Path destination) throws IOException {
    rewrite(source, destination, name -> {
      if (name.startsWith(SRG_PREFIX)) {
        return null;
      }

      if (name.startsWith(NOTCH_PREFIX)) {
        name = name.substring(NOTCH_PREFIX.length());
      }

      return name;
    });
  }

  /**
   * Drops the {@code net/minecraftforge/} and {@code javax/} packages OptiFine bundles for its
   * standalone Forge build; every other entry is copied through unchanged. Used when LabyMod runs on
   * Forge, where those classes already exist on the classpath and would clash with the prepared jar.
   */
  public static void stripForgePackages(Path source, Path destination) throws IOException {
    rewrite(source, destination, name -> {
      if (name.startsWith(FORGE_PREFIX) || name.startsWith(JAVAX_PREFIX)) {
        return null;
      }

      return name;
    });
  }

  private static void rewrite(Path source, Path destination, Function<String, String> mapper) throws IOException {
    Set<String> writtenNames = new HashSet<>();
    try (ZipFile zip = new ZipFile(source.toFile());
         ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(destination))) {
      Enumeration<? extends ZipEntry> entries = zip.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        String mapped = mapper.apply(entry.getName());
        if (mapped == null || !writtenNames.add(mapped)) {
          continue;
        }
        ZipEntry copy = new ZipEntry(mapped);
        copy.setTime(entry.getTime());
        out.putNextEntry(copy);
        try (InputStream in = zip.getInputStream(entry)) {
          transfer(in, out);
        }
        out.closeEntry();
      }
    }
  }

  private static void transfer(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[8192];
    int read;
    while ((read = in.read(buffer)) > 0) {
      out.write(buffer, 0, read);
    }
  }
}
