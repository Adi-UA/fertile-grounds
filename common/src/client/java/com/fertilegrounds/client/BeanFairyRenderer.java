package com.fertilegrounds.client;

import com.fertilegrounds.entity.BeanFairy;
import com.fertilegrounds.util.ModIdsUtil;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

/** Draws the Bean Fairy with its model and texture, fully lit so it glows at night. */
public class BeanFairyRenderer
    extends MobRenderer<BeanFairy, LivingEntityRenderState, BeanFairyModel> {

  private static final Identifier TEXTURE = ModIdsUtil.id("textures/entity/bean_fairy.png");

  /** Shadow radius in blocks. */
  private static final float SHADOW_RADIUS = 0.2F;

  public BeanFairyRenderer(final EntityRendererProvider.Context context) {
    super(context, new BeanFairyModel(context.bakeLayer(BeanFairyModel.LAYER)), SHADOW_RADIUS);
  }

  @Override
  public Identifier getTextureLocation(final LivingEntityRenderState state) {
    return TEXTURE;
  }

  @Override
  public LivingEntityRenderState createRenderState() {
    return new LivingEntityRenderState();
  }

  @Override
  protected int getBlockLightLevel(final BeanFairy entity, final BlockPos pos) {
    return 15;
  }
}
