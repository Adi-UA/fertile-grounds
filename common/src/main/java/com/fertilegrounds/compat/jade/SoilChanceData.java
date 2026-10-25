package com.fertilegrounds.compat.jade;

import com.fertilegrounds.block.GrowthBoostedFarmlandBlock;
import com.fertilegrounds.block.GrowthBoostedSandBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

/** Sends a soil block's boost chance, since clients don't get the server's gamerules. */
public enum SoilChanceData implements IServerDataProvider<BlockAccessor> {
  INSTANCE;

  public static final String CHANCE = "boost_chance";

  @Override
  public void appendServerData(final CompoundTag data, final BlockAccessor accessor) {
    final GameRules.Key<GameRules.IntegerValue> rule = boostChanceRule(accessor.getBlock());
    if (rule != null) {
      data.putInt(CHANCE, accessor.getLevel().getServer().getGameRules().getInt(rule));
    }
  }

  @Override
  public ResourceLocation getUid() {
    return JadeIds.SOIL_CHANCE;
  }

  private static GameRules.Key<GameRules.IntegerValue> boostChanceRule(final Block block) {
    if (block instanceof GrowthBoostedFarmlandBlock farmland) {
      return farmland.boostChanceRule();
    }
    if (block instanceof GrowthBoostedSandBlock sand) {
      return sand.boostChanceRule();
    }
    return null;
  }
}
