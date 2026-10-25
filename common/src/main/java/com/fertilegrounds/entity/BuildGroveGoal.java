package com.fertilegrounds.entity;

import java.util.EnumSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

/** A Fairy Grove builder flies to the spot the grove gives it, trailing sparkles. */
class BuildGroveGoal extends Goal {

  private final BeanFairy fairy;

  BuildGroveGoal(final BeanFairy fairy) {
    this.fairy = fairy;
    this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
  }

  @Override
  public boolean canUse() {
    return this.fairy.isGroveBuilder();
  }

  @Override
  public boolean requiresUpdateEveryTick() {
    return true;
  }

  @Override
  public void tick() {
    final Vec3 target = this.fairy.getGroveTarget();
    this.fairy.getMoveControl().setWantedPosition(target.x, target.y, target.z, 1.5);
    if (this.fairy.tickCount % 5 == 0 && this.fairy.level() instanceof ServerLevel level) {
      level.sendParticles(
          ParticleTypes.GLOW,
          this.fairy.getX(),
          this.fairy.getY(),
          this.fairy.getZ(),
          1,
          0.1,
          0.1,
          0.1,
          0.0);
    }
  }
}
