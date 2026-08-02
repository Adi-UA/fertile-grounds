package com.fertilegrounds.block;

import com.fertilegrounds.util.GrowthBoostUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A sand variant that passively fertilizes whatever grows directly on top of it (cactus, sugar
 * cane, melon/pumpkin stems), without introducing any new plantable surface — unlike the farmland
 * tiers, ordinary crops still can't be planted here; this only speeds up things that already grow
 * on sand. Falls under gravity exactly like vanilla sand ({@link FallingBlock}).
 */
public class GrowthBoostedSandBlock extends FallingBlock {

  public static final MapCodec<GrowthBoostedSandBlock> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      propertiesCodec(),
                      Codec.FLOAT
                          .fieldOf("growth_boost_chance")
                          .forGetter(block -> block.growthBoostChance))
                  .apply(instance, GrowthBoostedSandBlock::new));

  private final float growthBoostChance;

  /**
   * @param properties block properties, e.g. from {@code ModBlockProperties.sandProperties()}
   * @param growthBoostChance per-random-tick probability (0.0-1.0) of fertilizing the plant above
   */
  public GrowthBoostedSandBlock(final Properties properties, final float growthBoostChance) {
    super(properties);
    this.growthBoostChance = growthBoostChance;
  }

  @Override
  protected MapCodec<? extends FallingBlock> codec() {
    return CODEC;
  }

  @Override
  public int getDustColor(final BlockState state, final BlockGetter world, final BlockPos pos) {
    return state.getMapColor(world, pos).col;
  }

  @Override
  public void randomTick(
      final BlockState state,
      final ServerLevel world,
      final BlockPos pos,
      final RandomSource random) {
    super.randomTick(state, world, pos, random);
    GrowthBoostUtil.tryBoostAbove(world, pos, random, this.growthBoostChance);
  }
}
