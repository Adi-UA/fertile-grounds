package com.fertilegrounds.hotel;

import com.fertilegrounds.util.CropGrowthUtil;
import com.fertilegrounds.util.GrowthBoostUtil;
import com.fertilegrounds.util.ModGameRules;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * What an occupied Fairy Hotel does on its random tick: grows a crop near it, a little more often
 * when bees carry nectar nearby, and now and then adds honey to a nearby hive.
 */
public final class FairyHotelEffects {

  /** Crops and hives this far from the hotel, horizontally, get its help. */
  private static final int REACH = 4;

  /** And this far up or down. */
  private static final int REACH_HEIGHT = 2;

  /** Bees this close count as pollinating for the hotel. */
  private static final double BEE_DISTANCE = 6;

  private FairyHotelEffects() {}

  /**
   * Runs one random tick of a hotel's farm help. Only the leader of a cluster of hotels calls this,
   * so hotels side by side don't multiply it.
   *
   * @param petted whether the fairy was brushed since the last dawn
   */
  public static void randomTick(
      final ServerLevel level,
      final BlockPos hotel,
      final boolean petted,
      final RandomSource random) {
    final GameRules rules = level.getGameRules();
    final int chance =
        rules.get(
            petted
                ? ModGameRules.FAIRY_HOTEL_PETTED_BOOST_CHANCE
                : ModGameRules.FAIRY_HOTEL_BOOST_CHANCE);
    final int attempts = FairyHotelRules.boostAttempts(hasNectarBeeNear(level, hotel));
    for (int attempt = 0; attempt < attempts; attempt++) {
      randomSpotNear(level, hotel, random, pos -> CropGrowthUtil.canGrow(level, pos))
          .filter(crop -> GrowthBoostUtil.tryBoostAt(level, crop, random, chance))
          .ifPresent(crop -> sparkle(level, crop));
    }
    if (random.nextInt(100) < rules.get(ModGameRules.FAIRY_HOTEL_HONEY_CHANCE)) {
      randomSpotNear(level, hotel, random, pos -> hasRoomForHoney(level.getBlockState(pos)))
          .ifPresent(hive -> addHoney(level, hive));
    }
  }

  private static boolean hasNectarBeeNear(final ServerLevel level, final BlockPos hotel) {
    return !level
        .getEntitiesOfClass(
            Bee.class,
            AABB.ofSize(
                Vec3.atCenterOf(hotel), 2 * BEE_DISTANCE, 2 * BEE_DISTANCE, 2 * BEE_DISTANCE),
            Bee::hasNectar)
        .isEmpty();
  }

  private static boolean hasRoomForHoney(final BlockState state) {
    return state.is(BlockTags.BEEHIVES)
        && state.getValue(BeehiveBlock.HONEY_LEVEL) < BeehiveBlock.MAX_HONEY_LEVELS;
  }

  private static void addHoney(final ServerLevel level, final BlockPos hive) {
    final BlockState state = level.getBlockState(hive);
    level.setBlockAndUpdate(
        hive,
        state.setValue(BeehiveBlock.HONEY_LEVEL, state.getValue(BeehiveBlock.HONEY_LEVEL) + 1));
    level.playSound(null, hive, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 0.3F, 1.0F);
    level.sendParticles(
        ParticleTypes.WAX_ON,
        hive.getX() + 0.5,
        hive.getY() + 0.5,
        hive.getZ() + 0.5,
        3,
        0.4,
        0.4,
        0.4,
        0.0);
  }

  private static void sparkle(final ServerLevel level, final BlockPos crop) {
    level.sendParticles(
        ParticleTypes.HAPPY_VILLAGER,
        crop.getX() + 0.5,
        crop.getY() + 0.5,
        crop.getZ() + 0.5,
        2,
        0.3,
        0.3,
        0.3,
        0.0);
  }

  /** A random spot within reach of the hotel that passes {@code test}, if any. */
  private static Optional<BlockPos> randomSpotNear(
      final ServerLevel level,
      final BlockPos hotel,
      final RandomSource random,
      final Predicate<BlockPos> test) {
    final List<BlockPos> spots =
        BlockPos.betweenClosedStream(
                hotel.offset(-REACH, -REACH_HEIGHT, -REACH),
                hotel.offset(REACH, REACH_HEIGHT, REACH))
            .filter(test)
            .map(BlockPos::immutable)
            .toList();
    return Util.getRandomSafe(spots, random);
  }
}
