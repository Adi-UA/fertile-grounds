package com.fertilegrounds.client;

import com.fertilegrounds.entity.BeanFairy;
import com.fertilegrounds.util.ModIdsUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

/** Draws the Bean Fairy with its model and texture, fully lit so it glows at night. */
public class BeanFairyRenderer extends MobRenderer<BeanFairy, BeanFairyModel> {

  private static final ResourceLocation TEXTURE = ModIdsUtil.id("textures/entity/bean_fairy.png");

  /** Shadow radius in blocks. */
  private static final float SHADOW_RADIUS = 0.2F;

  public BeanFairyRenderer(final EntityRendererProvider.Context context) {
    super(context, new BeanFairyModel(context.bakeLayer(BeanFairyModel.LAYER)), SHADOW_RADIUS);
  }

  @Override
  public ResourceLocation getTextureLocation(final BeanFairy fairy) {
    return TEXTURE;
  }

  /** Shrinks a caught fairy into its bottle. */
  @Override
  protected void scale(final BeanFairy fairy, final PoseStack poseStack, final float partialTick) {
    final float size = fairy.bottleScale();
    poseStack.scale(size, size, size);
  }

  @Override
  protected int getBlockLightLevel(final BeanFairy entity, final BlockPos pos) {
    return 15;
  }
}
