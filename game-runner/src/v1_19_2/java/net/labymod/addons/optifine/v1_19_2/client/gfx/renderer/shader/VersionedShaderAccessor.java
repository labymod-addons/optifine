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

package net.labymod.addons.optifine.v1_19_2.client.gfx.renderer.shader;

import net.labymod.addons.optifine.client.gfx.renderer.shader.ShaderAccessor;
import net.labymod.api.models.Implements;
import net.optifine.shaders.Shaders;

import javax.inject.Singleton;
import java.nio.FloatBuffer;

@Singleton
@Implements(ShaderAccessor.class)
public class VersionedShaderAccessor implements ShaderAccessor {

  public VersionedShaderAccessor() {
  }

  @Override
  public boolean isShadowPass() {
    return Shaders.isShadowPass;
  }

  @Override
  public FloatBuffer getShadowModelViewBuffer() {
    return Shaders.shadowModelView;
  }

  @Override
  public FloatBuffer getShadowModelViewInverseBuffer() {
    return Shaders.shadowModelViewInverse;
  }

  @Override
  public FloatBuffer getShadowProjectionBuffer() {
    return Shaders.shadowProjection;
  }

  @Override
  public FloatBuffer getShadowProjectionInverseBuffer() {
    return Shaders.shadowProjectionInverse;
  }
}
