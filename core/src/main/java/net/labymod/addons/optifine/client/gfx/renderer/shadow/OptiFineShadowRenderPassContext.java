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
