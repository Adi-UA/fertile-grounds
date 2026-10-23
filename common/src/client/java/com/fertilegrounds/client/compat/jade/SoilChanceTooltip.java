package com.fertilegrounds.client.compat.jade;

import com.fertilegrounds.compat.jade.JadeIds;
import com.fertilegrounds.compat.jade.SoilChanceData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/** "Boost chance: 50%" on enriched farmland and sand. */
public enum SoilChanceTooltip implements IBlockComponentProvider {
  INSTANCE;

  @Override
  public void appendTooltip(
      final ITooltip tooltip, final BlockAccessor accessor, final IPluginConfig config) {
    final CompoundTag data = accessor.getServerData();
    if (data.contains(SoilChanceData.CHANCE)) {
      tooltip.add(
          Component.translatable(
              "jade.fertilegrounds.boost_chance", data.getInt(SoilChanceData.CHANCE)));
    }
  }

  @Override
  public ResourceLocation getUid() {
    return JadeIds.SOIL_CHANCE;
  }
}
