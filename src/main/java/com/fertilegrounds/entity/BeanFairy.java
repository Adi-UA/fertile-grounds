package com.fertilegrounds.entity;

import com.fertilegrounds.fairy.FairyVisit;
import com.fertilegrounds.item.JarredFairyItem;
import com.fertilegrounds.util.ModCriteria;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * A tiny flying edamame pod that visits a crop at night, fully grows the patch around it, then
 * flies away. All of its behavior lives in {@link VisitCropGoal}; this class only sets up how it
 * moves and makes it harmless and temporary.
 */
public class BeanFairy extends PathfinderMob {

  @Nullable private FairyVisit visit;

  private boolean finishedGrowing;

  /** Ticks since a bottle caught this fairy, or 0 if it hasn't been caught. */
  private int caughtTicks;

  /** How long a caught fairy takes to shrink into the bottle before it disappears. */
  private static final int SHRINK_TICKS = 6;

  /** The lowest Y this fairy may reach, or null for no limit (see {@link #keepAbove}). */
  @Nullable private Double floorY;

  /** Where a Fairy Grove builder flies, or null for a fairy on a crop visit. */
  @Nullable private Vec3 groveTarget;

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
    if (this.caughtTicks > 0) {
      shrinkIntoBottle();
    }
  }

  /**
   * Shrinks a caught fairy through vanilla's scale attribute, which syncs to players and resizes
   * the model, then removes it once it has fit into the bottle.
   */
  private void shrinkIntoBottle() {
    this.caughtTicks++;
    this.getAttribute(Attributes.SCALE)
        .setBaseValue(1.0 - (double) this.caughtTicks / SHRINK_TICKS);
    if (this.caughtTicks >= SHRINK_TICKS) {
      this.discard();
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

  /**
   * Makes this fairy a Fairy Grove builder: it flies wherever {@link #setGroveTarget} says instead
   * of visiting a crop, and the grove removes it when done. Builders never finish growing, so they
   * can't be caught.
   */
  public boolean isGroveBuilder() {
    return this.groveTarget != null;
  }

  @Nullable
  Vec3 getGroveTarget() {
    return this.groveTarget;
  }

  public void setGroveTarget(final Vec3 target) {
    this.groveTarget = target;
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 4.0)
        .add(Attributes.FLYING_SPEED, 0.3)
        .add(Attributes.MOVEMENT_SPEED, 0.3);
  }

  @Override
  protected void registerGoals() {
    this.goalSelector.addGoal(0, new BuildGroveGoal(this));
    this.goalSelector.addGoal(1, new VisitCropGoal(this));
  }

  /** The visit this fairy is on. Unset for a fairy from a spawn egg, which picks a crop itself. */
  @Nullable
  public FairyVisit getVisit() {
    return this.visit;
  }

  public void setVisit(final FairyVisit visit) {
    this.visit = visit;
  }

  /**
   * A Glass Bottle catches a fairy that has finished growing its patch, the same way vanilla's
   * {@code Bucketable.bucketMobPickup} buckets an axolotl. A fairy still working is too busy.
   */
  @Override
  protected InteractionResult mobInteract(final Player player, final InteractionHand hand) {
    final ItemStack held = player.getItemInHand(hand);
    if (!held.is(Items.GLASS_BOTTLE) || this.caughtTicks > 0) {
      return super.mobInteract(player, hand);
    }
    // The client only predicts the arm swing; it doesn't know whether the fairy is done growing.
    if (!(this.level() instanceof ServerLevel level)) {
      return InteractionResult.SUCCESS;
    }
    if (!this.finishedGrowing) {
      player.sendOverlayMessage(Component.translatable("message.fertilegrounds.fairy_busy"));
      return InteractionResult.FAIL;
    }
    this.playSound(SoundEvents.BOTTLE_FILL, 1.0F, 1.5F);
    level.sendParticles(
        ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 6, 0.15, 0.15, 0.15, 0.02);
    player.setItemInHand(
        hand, ItemUtils.createFilledResult(held, player, JarredFairyItem.caughtNow(level), false));
    if (player instanceof ServerPlayer serverPlayer) {
      ModCriteria.FAIRY_CAUGHT.trigger(serverPlayer);
    }
    this.caughtTicks = 1;
    return InteractionResult.SUCCESS;
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
