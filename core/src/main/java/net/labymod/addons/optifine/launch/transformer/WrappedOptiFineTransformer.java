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

package net.labymod.addons.optifine.launch.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;

public class WrappedOptiFineTransformer implements IClassTransformer {

    private final IClassTransformer optiFineTransformer;

    public WrappedOptiFineTransformer() {
        try{
            Class<?> transformerClass = Launch.classLoader.loadClass("optifine.OptiFineClassTransformer");
            this.optiFineTransformer = (IClassTransformer) transformerClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to invoke OptiFineClassTransformer");
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte... classData) {
        return this.optiFineTransformer.transform(name, transformedName, classData);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
