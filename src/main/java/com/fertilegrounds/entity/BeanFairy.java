package com.fertilegrounds.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * A tiny flying edamame pod that visits a crop at night, fully grows the patch around it, then
 * flies away. All of its behavior lives in {@link VisitCropGoal}; this class only sets up how it
 * moves and makes it harmless and temporary.
 */
public class BeanFairy extends PathfinderMob {

  @Nullable private BlockPos targetCrop;

  private boolean finishedGrowing;

  /** The lowest Y this fairy may reach, or null for no limit (see {@link #keepAbove}). */
  @Nullable private Double floorY;

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
    // Flying through blocks also means nothing stops the fly-in from overshooting into the
    // ground, so an open-sky visit holds the fairy at its floor instead.
    if (this.floorY != null && this.getY() < this.floorY) {
      this.setPos(this.getX(), this.floorY, this.getZ());
      final Vec3 motion = this.getDeltaMovement();
      this.setDeltaMovement(motion.x, Math.max(0, motion.y), motion.z);
    }
  }

  /** True once the fairy has grown its patch, while it lingers and leaves. */
  public boolean hasFinishedGrowing() {
    return this.finishedGrowing;
  }

  void markFinishedGrowing() {
    this.finishedGrowing = true;
  }

  /** Never lets the fairy sink below {@code y}. Used over open-sky crops, not covered ones. */
  public void keepAbove(final double y) {
    this.floorY = y;
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
