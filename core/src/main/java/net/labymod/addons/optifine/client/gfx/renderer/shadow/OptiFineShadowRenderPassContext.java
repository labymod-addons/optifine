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

import net.labymod.api.client.gfx.pipeline.renderer.shadow.ShadowRenderPassContext;
import net.labymod.api.util.math.vector.FloatMatrix4;
import net.optifine.shaders.Shaders;
import org.jetbrains.annotations.Nullable;

public class OptiFineShadowRenderPassContext implements ShadowRenderPassContext {

  private static final FloatMatrix4 SHADOW_MODEL_VIEW_MATRIX = FloatMatrix4.newIdentity();
  private static final FloatMatrix4 SHADOW_MODEL_VIEW_INVERSE_MATRIX = FloatMatrix4.newIdentity();
  private static final FloatMatrix4 SHADOW_PROJECTION_MATRIX = FloatMatrix4.newIdentity();
  private static final FloatMatrix4 SHADOW_PROJECTION_INVERSE_MATRIX = FloatMatrix4.newIdentity();

  @Override
  public boolean isShadowRenderPass() {
    return Shaders.isShadowPass;
  }

  @Nullable
  @Override
  public FloatMatrix4 getShadowModelViewMatrix() {
    SHADOW_MODEL_VIEW_MATRIX.identity();
    SHADOW_MODEL_VIEW_MATRIX.load(Shaders.shadowModelView);
    return SHADOW_MODEL_VIEW_MATRIX;
  }

  @Override
  public @Nullable FloatMatrix4 getShadowModelViewInverseMatrix() {
    SHADOW_MODEL_VIEW_INVERSE_MATRIX.identity();
    SHADOW_MODEL_VIEW_INVERSE_MATRIX.load(Shaders.shadowModelViewInverse);
    return SHADOW_MODEL_VIEW_INVERSE_MATRIX;
  }

  @Nullable
  @Override
  public FloatMatrix4 getShadowProjectionMatrix() {
    SHADOW_PROJECTION_MATRIX.identity();
    SHADOW_PROJECTION_MATRIX.load(Shaders.shadowProjection);
    return SHADOW_PROJECTION_MATRIX;
  }

  @Override
  public @Nullable FloatMatrix4 getShadowProjectionInverseMatrix() {
    SHADOW_PROJECTION_INVERSE_MATRIX.identity();
    SHADOW_PROJECTION_INVERSE_MATRIX.load(Shaders.shadowProjectionInverse);
    return SHADOW_PROJECTION_INVERSE_MATRIX;
  }
}
