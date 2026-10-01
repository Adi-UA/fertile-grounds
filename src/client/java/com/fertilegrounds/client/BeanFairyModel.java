package com.fertilegrounds.client;

import com.fertilegrounds.util.ModIdsUtil;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * The Bean Fairy's shape: an edamame pod made of two 5-pixel-wide beans side by side (wide enough
 * for a pair of eyes each), a 1-pixel stem, and two flat wings on its back.
 *
 * <p>Each {@code texOffs(u, v)} below is the top-left corner of that part's area in the 32x32
 * texture; {@code art_source/bean_fairy_guide.png} shows the same layout with each face labeled.
 * Moving a part's {@code texOffs} here means repainting the texture to match.
 */
public class BeanFairyModel extends EntityModel<LivingEntityRenderState> {

  public static final ModelLayerLocation LAYER =
      new ModelLayerLocation(ModIdsUtil.id("bean_fairy"), "main");

  private static final int TEXTURE_SIZE = 32;

  private final ModelPart pod;
  private final ModelPart leftWing;
  private final ModelPart rightWing;

  public BeanFairyModel(final ModelPart root) {
    super(root);
    this.pod = root.getChild("pod");
    this.leftWing = this.pod.getChild("left_wing");
    this.rightWing = this.pod.getChild("right_wing");
  }

  /** Model units are pixels; y grows downward and 24 is ground level. */
  public static LayerDefinition createBodyLayer() {
    final MeshDefinition mesh = new MeshDefinition();
    final PartDefinition pod =
        mesh.getRoot()
            .addOrReplaceChild("pod", CubeListBuilder.create(), PartPose.offset(0, 20, 0));

    pod.addOrReplaceChild(
        "left_bean",
        CubeListBuilder.create().texOffs(0, 0).addBox(-5, -2, -2, 5, 4, 4),
        PartPose.ZERO);
    pod.addOrReplaceChild(
        "right_bean",
        CubeListBuilder.create().texOffs(0, 8).addBox(0, -2, -2, 5, 4, 4),
        PartPose.ZERO);
    pod.addOrReplaceChild(
        "stem",
        CubeListBuilder.create().texOffs(20, 0).addBox(-3, -3, -0.5F, 1, 1, 1),
        PartPose.ZERO);

    // Wings are flat (zero depth). The tiny deformation stops their two faces from flickering.
    final CubeDeformation flat = new CubeDeformation(0.001F);
    pod.addOrReplaceChild(
        "left_wing",
        CubeListBuilder.create().texOffs(0, 16).addBox(0, -5, 0, 5, 6, 0, flat),
        PartPose.offset(0.5F, -1, 2));
    pod.addOrReplaceChild(
        "right_wing",
        CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-5, -5, 0, 5, 6, 0, flat),
        PartPose.offset(-0.5F, -1, 2));

    return LayerDefinition.create(mesh, TEXTURE_SIZE, TEXTURE_SIZE);
  }

  /** Flaps the wings fast and bobs the whole pod up and down slowly. */
  @Override
  public void setupAnim(final LivingEntityRenderState state) {
    super.setupAnim(state);
    final float flap = Mth.sin(state.ageInTicks * 1.3F) * 0.6F;
    this.leftWing.yRot = -0.4F - flap;
    this.rightWing.yRot = 0.4F + flap;
    this.pod.y += Mth.sin(state.ageInTicks * 0.15F) * 0.6F;
  }
}
