package com.fertilegrounds.hotel;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Keeps a mob away from occupied Fairy Hotels, the way vanilla's {@code AvoidEntityGoal} keeps a
 * creeper away from cats: pick a spot farther from the hotel, walk there, and sprint while close.
 */
public class AvoidFairyHotelGoal extends Goal {

  /** Mobs this close to an occupied hotel turn away, as creepers do from cats. */
  public static final int AVOID_DISTANCE = 6;

  /** Within this distance (squared) the mob sprints, the same 7 blocks vanilla uses. */
  private static final double SPRINT_DISTANCE_SQR = 49;

  private final PathfinderMob mob;
  private final double walkSpeed;
  private final double sprintSpeed;
  private final PathNavigation navigation;
  @Nullable private Path path;
  private Vec3 hotel = Vec3.ZERO;

  public AvoidFairyHotelGoal(
      final PathfinderMob mob, final double walkSpeed, final double sprintSpeed) {
    this.mob = mob;
    this.walkSpeed = walkSpeed;
    this.sprintSpeed = sprintSpeed;
    this.navigation = mob.getNavigation();
    this.setFlags(EnumSet.of(Goal.Flag.MOVE));
  }

  @Override
  public boolean canUse() {
    final BlockPos nearest =
        FairyHotelLookup.nearestOccupiedHotel(
                (ServerLevel) this.mob.level(), this.mob.blockPosition(), AVOID_DISTANCE)
            .orElse(null);
    if (nearest == null) {
      return false;
    }
    this.hotel = Vec3.atCenterOf(nearest);
    final Vec3 away = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.hotel);
    if (away == null || away.distanceToSqr(this.hotel) < this.mob.distanceToSqr(this.hotel)) {
      return false;
    }
    this.path = this.navigation.createPath(away.x, away.y, away.z, 0);
    return this.path != null;
  }

  @Override
  public boolean canContinueToUse() {
    return !this.navigation.isDone();
  }

  @Override
  public void start() {
    this.navigation.moveTo(this.path, this.walkSpeed);
  }

  @Override
  public void tick() {
    this.navigation.setSpeedModifier(
        this.mob.distanceToSqr(this.hotel) < SPRINT_DISTANCE_SQR
            ? this.sprintSpeed
            : this.walkSpeed);
  }
}
