package com.fertilegrounds.client.compat.jade;

import com.fertilegrounds.compat.jade.JadeIds;
import com.fertilegrounds.compat.jade.PendingVisitData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/** "A Bean Fairy is coming tonight" on a crop with a pending visit. */
public enum PendingVisitTooltip implements IBlockComponentProvider {
  INSTANCE;

  @Override
  public void appendTooltip(
      final ITooltip tooltip, final BlockAccessor accessor, final IPluginConfig config) {
    if (accessor.getServerData().getBooleanOr(PendingVisitData.COMING, false)) {
      tooltip.add(Component.translatable("jade.fertilegrounds.fairy_coming"));
    }
  }

  @Override
  public ResourceLocation getUid() {
    return JadeIds.PENDING_VISIT;
  }
}
