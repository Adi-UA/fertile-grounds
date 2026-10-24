package com.fertilegrounds.grove;

import com.fertilegrounds.hotel.HotelChimes;
import com.fertilegrounds.util.ModGameRules;
import com.fertilegrounds.util.ModTags;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Starting a Fairy Grove: a diamond on a complete shrine's Chime Hotel, over an area of nothing but
 * natural terrain. Every refusal keeps the diamond and tells the player why. {@link GroveBuilder}
 * does the building.
 */
public final class FairyGrove {

  /** How far above the shrine's soil the grove clears. */
  public static final int CLEAR_HEIGHT = 24;

  /** How deep below the shrine's soil the grove fills dips. */
  public static final int FILL_DEPTH = 8;

  private FairyGrove() {}

  /** Checks the shrine and the area, then uses up the diamond and starts building. */
  public static void tryStart(
      final ServerLevel level,
      final BlockPos chimeHotel,
      final Player player,
      final ItemStack diamond) {
    if (!level.getGameRules().get(ModGameRules.FAIRY_GROVE)) {
      refuse(level, chimeHotel, player, Component.translatable("message.fertilegrounds.grove.off"));
      return;
    }
    final BlockPos soil = chimeHotel.below();
    if (GroveBuilder.isBuilding(level, soil)) {
      refuse(
          level, chimeHotel, player, Component.translatable("message.fertilegrounds.grove.busy"));
      return;
    }
    final GroveShrine.Result shrine = GroveShrine.check(level, chimeHotel);
    if (shrine != GroveShrine.Result.OK) {
      refuse(level, chimeHotel, player, Component.translatable(shrine.messageKey()));
      return;
    }
    final Optional<BlockPos> blocker = findBlocker(level, soil);
    if (blocker.isPresent()) {
      final BlockPos pos = blocker.get();
      refuse(
          level,
          chimeHotel,
          player,
          Component.translatable(
              "message.fertilegrounds.grove.blocked",
              level.getBlockState(pos).getBlock().getName(),
              pos.getX(),
              pos.getY(),
              pos.getZ()));
      return;
    }
    diamond.consume(1, player);
    player.displayClientMessage(
        Component.translatable("message.fertilegrounds.grove.started"), true);
    level.playSound(null, chimeHotel, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.2F);
    HotelChimes.ring(level, chimeHotel, true);
    level.sendParticles(
        ParticleTypes.GLOW,
        chimeHotel.getX() + 0.5,
        chimeHotel.getY() + 0.5,
        chimeHotel.getZ() + 0.5,
        30,
        2.0,
        1.0,
        2.0,
        0.0);
    GroveBuilder.start(level, soil);
  }

  /**
   * The first block in the grove's area, outside the shrine's own plot, that isn't natural terrain,
   * or empty if the grove may build.
   */
  public static Optional<BlockPos> findBlocker(final BlockGetter level, final BlockPos soil) {
    for (final GroveLayout.Offset offset : GroveLayout.buildOrder()) {
      for (int dy = -FILL_DEPTH; dy <= CLEAR_HEIGHT; dy++) {
        final BlockPos pos = soil.offset(offset.dx(), dy, offset.dz());
        if (!isReplaceable(level.getBlockState(pos))) {
          return Optional.of(pos);
        }
      }
    }
    return Optional.empty();
  }

  static boolean isReplaceable(final BlockState state) {
    return state.isAir() || state.is(ModTags.GROVE_REPLACEABLE);
  }

  private static void refuse(
      final ServerLevel level,
      final BlockPos chimeHotel,
      final Player player,
      final Component why) {
    player.displayClientMessage(why, true);
    level.playSound(null, chimeHotel, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 1.0F, 1.0F);
    level.sendParticles(
        ParticleTypes.SMOKE,
        chimeHotel.getX() + 0.5,
        chimeHotel.getY() + 0.8,
        chimeHotel.getZ() + 0.5,
        4,
        0.2,
        0.1,
        0.2,
        0.0);
  }
}
