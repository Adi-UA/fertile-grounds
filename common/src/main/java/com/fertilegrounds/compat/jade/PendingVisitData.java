package com.fertilegrounds.compat.jade;

import com.fertilegrounds.fairy.FairyVisits;
import com.fertilegrounds.util.ModTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

/** Sends whether a fairy is coming to the crop, since only the server keeps the visit list. */
public enum PendingVisitData implements IServerDataProvider<BlockAccessor> {
  INSTANCE;

  public static final String COMING = "fairy_coming";

  @Override
  public boolean shouldRequestData(final BlockAccessor accessor) {
    return accessor.getBlockState().is(ModTags.FAIRY_CROPS);
  }

  @Override
  public void appendServerData(final CompoundTag data, final BlockAccessor accessor) {
    if (accessor.getLevel() instanceof ServerLevel level
        && FairyVisits.hasVisitAt(level, accessor.getPosition())) {
      data.putBoolean(COMING, true);
    }
  }

  @Override
  public Identifier getUid() {
    return JadeIds.PENDING_VISIT;
  }
}
