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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class IsolatedClassLoader extends URLClassLoader {

  public IsolatedClassLoader() {
    // Parent must never reach the game classloader: URLClassLoader is parent-first, and in
    // production the addon's classloader parent IS the LabyClassLoader, which permanently caches
    // every miss in its invalid-class set. The optifine.* lookups happening here (before the
    // prepared jar is on the game classpath) would poison those names for the entire session.
    super(new URL[0], ClassLoader.getPlatformClassLoader());
  }

  public void addPath(Path path) {
    try {
      this.addURL(path.toUri().toURL());
    } catch (MalformedURLException ignored) {
    }
  }
}
