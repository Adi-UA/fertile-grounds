package com.fertilegrounds.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * A tiny flying edamame pod that visits a crop at night, fully grows the patch around it, then
 * flies away. All of its behavior lives in {@link VisitCropGoal}; this class only sets up how it
 * moves and makes it harmless and temporary.
 */
public class BeanFairy extends PathfinderMob {

  @Nullable private BlockPos targetCrop;

  public BeanFairy(final EntityType<? extends BeanFairy> type, final Level level) {
    super(type, level);
    // Same flying setup as vanilla's Allay: steer straight toward a point and hover there.
    this.moveControl = new FlyingMoveControl<>(this, 20, true);
    this.setNoGravity(true);
  }

  /**
   * Flies through blocks, the same way vanilla's Vex does, so a fairy can reach crops under a roof
   * or behind a wall instead of getting stuck against them.
   */
  @Override
  public void tick() {
    this.noPhysics = true;
    super.tick();
    this.noPhysics = false;
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 4.0)
        .add(Attributes.FLYING_SPEED, 0.3)
        .add(Attributes.MOVEMENT_SPEED, 0.3);
  }

  @Override
  protected void registerGoals() {
    this.goalSelector.addGoal(0, new VisitCropGoal(this));
  }

  /** The crop to visit. Unset for a fairy from a spawn egg, which picks the nearest crop itself. */
  @Nullable
  public BlockPos getTargetCrop() {
    return this.targetCrop;
  }

  public void setTargetCrop(final BlockPos targetCrop) {
    this.targetCrop = targetCrop;
  }

  /** Players can't hurt it (creative mode and /kill still can, so it never gets stuck). */
  @Override
  public boolean isInvulnerable() {
    return true;
  }

  /** Never written to disk: if its chunk unloads mid-visit, the fairy just disappears. */
  @Override
  public boolean shouldBeSaved() {
    return false;
  }

  @Override
  public boolean isPushable() {
    return false;
  }
}
