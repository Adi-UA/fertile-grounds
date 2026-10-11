package com.fertilegrounds.compat.jade;

import com.fertilegrounds.block.GrowthBoostedFarmlandBlock;
import com.fertilegrounds.block.GrowthBoostedSandBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gamerules.GameRule;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

/** Sends a soil block's boost chance, since clients don't get the server's gamerules. */
public enum SoilChanceData implements IServerDataProvider<BlockAccessor> {
  INSTANCE;

  public static final String CHANCE = "boost_chance";

  @Override
  public void appendServerData(final CompoundTag data, final BlockAccessor accessor) {
    final GameRule<Integer> rule = boostChanceRule(accessor.getBlock());
    if (rule != null) {
      data.putInt(CHANCE, accessor.getLevel().getServer().getGameRules().get(rule));
    }
  }

  @Override
  public Identifier getUid() {
    return JadeIds.SOIL_CHANCE;
  }

  private static GameRule<Integer> boostChanceRule(final Block block) {
    if (block instanceof GrowthBoostedFarmlandBlock farmland) {
      return farmland.boostChanceRule();
    }
    if (block instanceof GrowthBoostedSandBlock sand) {
      return sand.boostChanceRule();
    }
    return null;
  }
}
