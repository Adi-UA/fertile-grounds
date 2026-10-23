package com.fertilegrounds.client.compat.jade;

import com.fertilegrounds.block.FairyHotelBlock;
import com.fertilegrounds.compat.jade.ChimeFieldData;
import com.fertilegrounds.compat.jade.JadeIds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/**
 * On an occupied hotel, whether its fairy was petted since dawn (the client already has this in the
 * block state); on a Chime Hotel, also its crop count from the server, such as "Crops ready:
 * 41/50".
 */
public enum FairyHotelTooltip implements IBlockComponentProvider {
  INSTANCE;

  @Override
  public void appendTooltip(
      final ITooltip tooltip, final BlockAccessor accessor, final IPluginConfig config) {
    if (!(accessor.getBlock() instanceof FairyHotelBlock hotel) || !hotel.isOccupied()) {
      return;
    }
    tooltip.add(
        Component.translatable(
            accessor.getBlockState().getValue(FairyHotelBlock.PETTED)
                ? "jade.fertilegrounds.petted"
                : "jade.fertilegrounds.ready_for_brush"));
    final CompoundTag data = accessor.getServerData();
    if (data.contains(ChimeFieldData.TOTAL)) {
      tooltip.add(
          Component.translatable(
              "jade.fertilegrounds.crops_ready",
              data.getInt(ChimeFieldData.GROWN),
              data.getInt(ChimeFieldData.TOTAL)));
    }
  }

  @Override
  public ResourceLocation getUid() {
    return JadeIds.FAIRY_HOTEL;
  }
}
