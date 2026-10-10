package com.fertilegrounds.entity;

import com.fertilegrounds.blight.Blight;
import com.fertilegrounds.fairy.FairyVisit;
import com.fertilegrounds.fairy.FairyVisitEvents;
import com.fertilegrounds.fairy.FairyVisitScheduler;
import com.fertilegrounds.fairy.ShyFlight;
import com.fertilegrounds.util.CropGrowthUtil;
import com.fertilegrounds.util.ModGameRules;
import com.fertilegrounds.util.ModTags;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * The Bean Fairy's whole visit, as five phases: fly down to the crop, spin above it, grow the patch
 * in rings from the center out, linger over the patch for a while, then fly up and vanish. While
 * flying in or lingering it is shy, darting away from players who rush it.
 */
class VisitCropGoal extends Goal {

  private enum Phase {
    FLY_TO_CROP,
    DANCE,
    GROW_RINGS,
    LINGER,
    LEAVE
  }

  /** Gives up and vanishes if it can't reach its crop in 20 seconds. */
  private static final int FLY_GIVE_UP_TICKS = 400;

  private static final int DANCE_TICKS = 60;

  /** One ring grows every half second: the center, then the 8 around it, then the outer 16. */
  private static final int TICKS_PER_RING = 10;

  private static final int LAST_RING = FairyVisitScheduler.GROWTH_RADIUS;

  /** While lingering, it picks a new crop to drift to every 2 seconds. */
  private static final int DRIFT_TICKS = 40;

  private static final int LEAVE_TICKS = 40;

  /** A shy dart lasts 1 second before the fairy goes back to what it was doing. */
  private static final int DART_TICKS = 20;

  /** How close (in blocks, squared) counts as "arrived" above the crop. */
  private static final double ARRIVED_DISTANCE_SQR = 0.5;

  /** Hover height above the crop's block position, in blocks. */
  private static final double HOVER_HEIGHT = 1.2;

  private static final int SPAWN_EGG_SEARCH_RADIUS = 8;

  private final BeanFairy fairy;
  private Phase phase = Phase.FLY_TO_CROP;
  private int phaseTicks;
  private int lingerTicks;
  private int nextHumTick;
  private BlockPos target = BlockPos.ZERO;
  @Nullable private FairyVisit visit;
  private BlockPos driftSpot = BlockPos.ZERO;
  private List<BlockPos> patchCrops = List.of();
  @Nullable private ShyFlight.Point dartTarget;
  private int dartTicks;

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
    final FairyVisit assigned = this.fairy.getVisit();
    final BlockPos crop = assigned != null ? assigned.pos() : findNearestCrop();
    if (crop == null) {
      // Nothing to grow (a spawn-egg fairy far from any crop), so just fly away.
      this.target = this.fairy.blockPosition();
      nextPhase(Phase.LEAVE);
      return;
    }
    this.target = crop;
    // A spawn-egg fairy has no visit, so it gets an unowned one: it grows crops but gives no gift.
    this.visit =
        assigned != null ? assigned : FairyVisit.of(crop, 0, FairyVisit.Source.UNOWNED, null);
    // A covered crop (under glass or a roof) keeps the fly-through-blocks path; an open one
    // gets a floor 1 block above the crop so the fairy never dips into the ground.
    if (level().canSeeSky(crop.above())) {
      this.fairy.keepAbove(crop.getY() + 1);
    }
  }

  @Override
  public void tick() {
    this.phaseTicks++;
    if (this.dartTicks > 0) {
      dart();
      return;
    }
    switch (this.phase) {
      case FLY_TO_CROP -> flyToCrop();
      case DANCE -> dance();
      case GROW_RINGS -> growRings();
      case LINGER -> linger();
      case LEAVE -> leave();
    }
  }

  private void flyToCrop() {
    if (this.phaseTicks > FLY_GIVE_UP_TICKS) {
      vanish();
      return;
    }
    if (startDartIfScared()) {
      return;
    }
    flyTo(this.target, hoverY(), 1.0);
    if (this.fairy.distanceToSqr(this.target.getX() + 0.5, hoverY(), this.target.getZ() + 0.5)
        < ARRIVED_DISTANCE_SQR) {
      nextPhase(Phase.DANCE);
    }
  }

  /** Hovers in place and spins, using the body rotation vanilla already syncs to players. */
  private void dance() {
    flyTo(this.target, hoverY(), 1.0);
    this.fairy.setYRot(this.fairy.getYRot() + 24);
    this.fairy.yBodyRot = this.fairy.getYRot();
    if (this.phaseTicks % 10 == 0) {
      sparkle(ParticleTypes.GLOW, 2);
    }
    if (this.phaseTicks >= DANCE_TICKS) {
      nextPhase(Phase.GROW_RINGS);
    }
  }

  private void growRings() {
    flyTo(this.target, hoverY(), 1.0);
    if ((this.phaseTicks - 1) % TICKS_PER_RING != 0) {
      return;
    }
    final int ring = (this.phaseTicks - 1) / TICKS_PER_RING;
    for (final BlockPos grown : CropGrowthUtil.growRing(level(), this.target, ring)) {
      level()
          .sendParticles(
              ParticleTypes.HAPPY_VILLAGER,
              grown.getX() + 0.5,
              grown.getY() + 0.5,
              grown.getZ() + 0.5,
              5,
              0.3,
              0.3,
              0.3,
              0.0);
    }
    this.fairy.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0F, 1.0F + 0.2F * ring);
    if (ring == LAST_RING) {
      Blight.cureArea(level(), this.target, LAST_RING);
      if (this.visit != null) {
        FairyVisitEvents.onVisitGrown(level(), this.visit, false);
      }
      this.fairy.markFinishedGrowing();
      startLinger();
    }
  }

  private void startLinger() {
    this.lingerTicks = level().getGameRules().get(ModGameRules.FAIRY_LINGER_SECONDS) * 20;
    this.patchCrops = cropsInPatch();
    this.driftSpot = this.target;
    this.nextHumTick = 0;
    nextPhase(this.lingerTicks > 0 ? Phase.LINGER : Phase.LEAVE);
  }

  /** Drifts slowly between the patch's crops, humming now and then. */
  private void linger() {
    if (this.phaseTicks >= this.lingerTicks) {
      nextPhase(Phase.LEAVE);
      return;
    }
    if (startDartIfScared()) {
      return;
    }
    if (this.phaseTicks % DRIFT_TICKS == 1 && !this.patchCrops.isEmpty()) {
      this.driftSpot = this.patchCrops.get(this.fairy.getRandom().nextInt(this.patchCrops.size()));
    }
    flyTo(this.driftSpot, this.driftSpot.getY() + HOVER_HEIGHT, 0.5);
    if (this.phaseTicks >= this.nextHumTick) {
      this.fairy.playSound(SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, 0.6F, 1.4F);
      this.nextHumTick = this.phaseTicks + 80 + this.fairy.getRandom().nextInt(81);
    }
    if (this.phaseTicks % 20 == 0) {
      sparkle(ParticleTypes.GLOW, 1);
    }
  }

  private void leave() {
    flyTo(this.target, this.target.getY() + 12, 1.0);
    if (this.phaseTicks >= LEAVE_TICKS) {
      vanish();
    }
  }

  /**
   * Starts a shy dart if a player who isn't sneaking (or spectating) is too close.
   *
   * @return true if the fairy is now darting
   */
  private boolean startDartIfScared() {
    final Player player =
        level()
            .getNearestPlayer(
                this.fairy.getX(),
                this.fairy.getY(),
                this.fairy.getZ(),
                ShyFlight.SCARE_DISTANCE,
                EntitySelector.NO_SPECTATORS.and(p -> !p.isShiftKeyDown()));
    if (player == null) {
      return false;
    }
    this.dartTarget =
        ShyFlight.target(
            new ShyFlight.Point(this.fairy.getX(), this.fairy.getY(), this.fairy.getZ()),
            new ShyFlight.Point(player.getX(), player.getY(), player.getZ()),
            new ShyFlight.Point(
                this.target.getX() + 0.5, this.target.getY(), this.target.getZ() + 0.5));
    this.dartTicks = DART_TICKS;
    this.fairy.playSound(SoundEvents.ALLAY_THROW, 0.5F, 1.2F);
    sparkle(ParticleTypes.END_ROD, 3);
    return true;
  }

  private void dart() {
    this.dartTicks--;
    if (this.dartTarget != null) {
      this.fairy
          .getMoveControl()
          .setWantedPosition(this.dartTarget.x(), this.dartTarget.y(), this.dartTarget.z(), 1.5);
    }
  }

  /** Every fairy crop in the 5x5 patch, grown or not, for lingering to drift between. */
  private List<BlockPos> cropsInPatch() {
    final List<BlockPos> crops = new ArrayList<>();
    for (final BlockPos pos :
        BlockPos.betweenClosed(
            this.target.offset(-LAST_RING, -1, -LAST_RING),
            this.target.offset(LAST_RING, 1, LAST_RING))) {
      if (level().getBlockState(pos).is(ModTags.FAIRY_CROPS)) {
        crops.add(pos.immutable());
      }
    }
    return crops;
  }

  /** A spawn-egg fairy has no assigned crop, so it uses the closest one, or null if none. */
  @Nullable
  private BlockPos findNearestCrop() {
    final BlockPos origin = this.fairy.blockPosition();
    for (final BlockPos pos : BlockPos.withinManhattan(origin, SPAWN_EGG_SEARCH_RADIUS)) {
      if (CropGrowthUtil.canGrow(level(), pos)) {
        return pos.immutable();
      }
    }
    return null;
  }

  private double hoverY() {
    return this.target.getY() + HOVER_HEIGHT;
  }

  private void flyTo(final BlockPos spot, final double y, final double speed) {
    this.fairy.getMoveControl().setWantedPosition(spot.getX() + 0.5, y, spot.getZ() + 0.5, speed);
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
