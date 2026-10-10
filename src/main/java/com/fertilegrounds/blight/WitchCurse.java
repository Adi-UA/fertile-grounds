package com.fertilegrounds.blight;

import com.fertilegrounds.util.ModGameRules;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** Now and then, a witch killed by a player curses the soil under it with blight. */
public final class WitchCurse {

  /** Players this close to the witch hear about the curse. */
  private static final double MESSAGE_RANGE = 32;

  private WitchCurse() {}

  public static void register() {
    ServerLivingEntityEvents.AFTER_DEATH.register(WitchCurse::afterDeath);
  }

  private static void afterDeath(final LivingEntity entity, final DamageSource source) {
    if (entity.getType() != EntityTypes.WITCH
        || !(source.getEntity() instanceof Player)
        || !(entity.level() instanceof ServerLevel level)) {
      return;
    }
    final int chance = level.getGameRules().get(ModGameRules.WITCH_BLIGHT_CHANCE);
    if (level.getRandom().nextInt(100) >= chance) {
      return;
    }
    final BlockPos under = entity.blockPosition().below();
    boolean blighted = false;
    for (final BlockPos pos :
        BlockPos.betweenClosed(under.offset(-1, 0, -1), under.offset(1, 0, 1))) {
      if (Blight.canTake(level, pos)) {
        Blight.take(level, pos.immutable());
        blighted = true;
      }
    }
    if (!blighted) {
      return;
    }
    level.playSound(null, under, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.HOSTILE, 1.5F, 0.8F);
    level.sendParticles(
        ParticleTypes.WITCH,
        under.getX() + 0.5,
        under.getY() + 1.2,
        under.getZ() + 0.5,
        20,
        1.0,
        0.3,
        1.0,
        0.0);
    final Component message = Component.translatable("message.fertilegrounds.witch_blight");
    for (final ServerPlayer player : level.players()) {
      if (player.distanceToSqr(entity) <= MESSAGE_RANGE * MESSAGE_RANGE) {
        player.sendSystemMessage(message);
      }
    }
  }
}
