package com.fertilegrounds.client.compat.jade;

import com.fertilegrounds.compat.jade.JadeIds;
import com.fertilegrounds.compat.jade.SoilChanceData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
    accessor
        .getServerData()
        .getInt(SoilChanceData.CHANCE)
        .ifPresent(
            chance ->
                tooltip.add(Component.translatable("jade.fertilegrounds.boost_chance", chance)));
  }

  @Override
  public Identifier getUid() {
    return JadeIds.SOIL_CHANCE;
  }
}
