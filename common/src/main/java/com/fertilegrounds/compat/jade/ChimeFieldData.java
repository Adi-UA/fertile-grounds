package com.fertilegrounds.compat.jade;

import com.fertilegrounds.hotel.HotelChimes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

/** Sends a Chime Hotel's crop count, which the server counts the same way the chime does. */
public enum ChimeFieldData implements IServerDataProvider<BlockAccessor> {
  INSTANCE;

  public static final String GROWN = "crops_grown";

  public static final String TOTAL = "crops_total";

  @Override
  public void appendServerData(final CompoundTag data, final BlockAccessor accessor) {
    if (accessor.getLevel() instanceof ServerLevel level) {
      final HotelChimes.Field field = HotelChimes.countField(level, accessor.getPosition());
      data.putInt(GROWN, field.grown());
      data.putInt(TOTAL, field.total());
    }
  }

  @Override
  public ResourceLocation getUid() {
    return JadeIds.FAIRY_HOTEL;
  }
}
