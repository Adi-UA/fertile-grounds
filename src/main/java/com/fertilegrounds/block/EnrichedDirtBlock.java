package com.fertilegrounds.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Enriched or Super Enriched Dirt: plain dirt that a comparator reads as its tier. */
public class EnrichedDirtBlock extends Block implements TieredSoil {

  private final SoilTier tier;

  public EnrichedDirtBlock(final Properties properties, final SoilTier tier) {
    super(properties);
    this.tier = tier;
  }

  @Override
  public SoilTier tier() {
    return this.tier;
  }

  @Override
  protected boolean hasAnalogOutputSignal(final BlockState state) {
    return true;
  }

  @Override
  protected int getAnalogOutputSignal(
      final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
    return this.tier.comparatorSignal();
  }
}
