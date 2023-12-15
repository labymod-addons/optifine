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

package net.labymod.addons.optifine.client.gfx.renderer.shadow;

import net.labymod.addons.optifine.client.gfx.renderer.shader.ShaderAccessor;
import net.labymod.api.client.gfx.pipeline.renderer.shadow.ShadowRenderPassContext;
import net.labymod.api.util.math.vector.FloatMatrix4;
import org.jetbrains.annotations.Nullable;
import java.nio.FloatBuffer;

public class OptiFineShadowRenderPassContext implements ShadowRenderPassContext {

  private static final FloatMatrix4 SHADOW_MODEL_VIEW_MATRIX = FloatMatrix4.newIdentity();
  private static final FloatMatrix4 SHADOW_MODEL_VIEW_INVERSE_MATRIX = FloatMatrix4.newIdentity();
  private static final FloatMatrix4 SHADOW_PROJECTION_MATRIX = FloatMatrix4.newIdentity();
  private static final FloatMatrix4 SHADOW_PROJECTION_INVERSE_MATRIX = FloatMatrix4.newIdentity();

  private final ShaderAccessor shaderAccessor;

  public OptiFineShadowRenderPassContext(ShaderAccessor shaderAccessor) {
    this.shaderAccessor = shaderAccessor;
  }

  @Override
  public boolean isShadowRenderPass() {
    return this.shaderAccessor.isShadowPass();
  }

  @Nullable
  @Override
  public FloatMatrix4 getShadowModelViewMatrix() {
    SHADOW_MODEL_VIEW_MATRIX.identity();
    SHADOW_MODEL_VIEW_MATRIX.load(this.shaderAccessor.getShadowModelViewBuffer());
    return SHADOW_MODEL_VIEW_MATRIX;
  }

  @Override
  public @Nullable FloatMatrix4 getShadowModelViewInverseMatrix() {
    SHADOW_MODEL_VIEW_INVERSE_MATRIX.identity();
    SHADOW_MODEL_VIEW_INVERSE_MATRIX.load(this.shaderAccessor.getShadowModelViewInverseBuffer());
    return SHADOW_MODEL_VIEW_INVERSE_MATRIX;
  }

  @Nullable
  @Override
  public FloatMatrix4 getShadowProjectionMatrix() {
    SHADOW_PROJECTION_MATRIX.identity();
    SHADOW_PROJECTION_MATRIX.load(this.shaderAccessor.getShadowProjectionBuffer());
    return SHADOW_PROJECTION_MATRIX;
  }

  @Override
  public @Nullable FloatMatrix4 getShadowProjectionInverseMatrix() {
    SHADOW_PROJECTION_INVERSE_MATRIX.identity();
    SHADOW_PROJECTION_INVERSE_MATRIX.load(this.shaderAccessor.getShadowProjectionInverseBuffer());
    return SHADOW_PROJECTION_INVERSE_MATRIX;
  }
}
