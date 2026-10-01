package com.fertilegrounds.entity;

import com.fertilegrounds.fairy.FairyVisitScheduler;
import com.fertilegrounds.util.CropGrowthUtil;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * The Bean Fairy's whole visit, as three phases: fly down to the crop, hover over it sparkling,
 * grow the patch, then fly up and vanish.
 */
class VisitCropGoal extends Goal {

  private enum Phase {
    FLY_TO_CROP,
    HOVER,
    LEAVE
  }

  private static final int HOVER_TICKS = 60;
  private static final int LEAVE_TICKS = 40;

  /** Gives up and vanishes after a minute, in case something blocks the way to the crop. */
  private static final int GIVE_UP_TICKS = 1200;

  /** How close (in blocks, squared) counts as "arrived" above the crop. */
  private static final double ARRIVED_DISTANCE_SQR = 0.5;

  /** Hover height above the crop's block position, in blocks. */
  private static final double HOVER_HEIGHT = 1.2;

  private static final int SPAWN_EGG_SEARCH_RADIUS = 8;

  private final BeanFairy fairy;
  private Phase phase = Phase.FLY_TO_CROP;
  private int phaseTicks;
  private int totalTicks;
  private BlockPos target = BlockPos.ZERO;

  VisitCropGoal(final BeanFairy fairy) {
    this.fairy = fairy;
    this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
  }

  @Override
  public boolean canUse() {
    return true;
  }

  @Override
  public boolean requiresUpdateEveryTick() {
    return true;
  }

  @Override
  public void start() {
    final BlockPos assigned = this.fairy.getTargetCrop();
    this.target = assigned != null ? assigned : findNearestCrop();
  }

  @Override
  public void tick() {
    this.phaseTicks++;
    if (++this.totalTicks > GIVE_UP_TICKS) {
      vanish();
      return;
    }

    switch (this.phase) {
      case FLY_TO_CROP -> {
        flyTo(this.target.getY() + HOVER_HEIGHT);
        if (this.fairy.distanceToSqr(
                this.target.getX() + 0.5,
                this.target.getY() + HOVER_HEIGHT,
                this.target.getZ() + 0.5)
            < ARRIVED_DISTANCE_SQR) {
          nextPhase(Phase.HOVER);
        }
      }
      case HOVER -> {
        flyTo(this.target.getY() + HOVER_HEIGHT);
        if (this.phaseTicks % 5 == 0) {
          sparkle(ParticleTypes.HAPPY_VILLAGER, 3);
        }
        if (this.phaseTicks >= HOVER_TICKS) {
          CropGrowthUtil.growArea(level(), this.target, FairyVisitScheduler.GROWTH_RADIUS);
          sparkle(ParticleTypes.HAPPY_VILLAGER, 30);
          this.fairy.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0F, 1.5F);
          nextPhase(Phase.LEAVE);
        }
      }
      case LEAVE -> {
        flyTo(this.target.getY() + 12);
        if (this.phaseTicks >= LEAVE_TICKS) {
          vanish();
        }
      }
    }
  }

  /** A spawn-egg fairy has no assigned crop; use the closest one, or hover where it is. */
  private BlockPos findNearestCrop() {
    final BlockPos origin = this.fairy.blockPosition();
    for (final BlockPos pos : BlockPos.withinManhattan(origin, SPAWN_EGG_SEARCH_RADIUS)) {
      if (CropGrowthUtil.canGrow(level(), pos)) {
        return pos.immutable();
      }
    }
    return origin.below();
  }

  private void flyTo(final double y) {
    this.fairy
        .getMoveControl()
        .setWantedPosition(this.target.getX() + 0.5, y, this.target.getZ() + 0.5, 1.0);
  }

  private void nextPhase(final Phase next) {
    this.phase = next;
    this.phaseTicks = 0;
  }

  private void vanish() {
    sparkle(ParticleTypes.END_ROD, 8);
    this.fairy.discard();
  }

  private void sparkle(final ParticleOptions particle, final int count) {
    level()
        .sendParticles(
            particle,
            this.fairy.getX(),
            this.fairy.getY(),
            this.fairy.getZ(),
            count,
            0.3,
            0.3,
            0.3,
            0.0);
  }

  private ServerLevel level() {
    return getServerLevel(this.fairy);
  }
}
