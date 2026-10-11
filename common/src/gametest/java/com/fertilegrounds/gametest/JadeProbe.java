package com.fertilegrounds.gametest;

import com.fertilegrounds.compat.jade.ChimeFieldData;
import com.fertilegrounds.compat.jade.PendingVisitData;
import com.fertilegrounds.compat.jade.SoilChanceData;
import java.lang.reflect.Proxy;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

/** Runs the mod's Jade data providers for a block in a test. Only loads when Jade is installed. */
final class JadeProbe {

  private JadeProbe() {}

  static CompoundTag soilChance(final GameTestHelper helper, final BlockPos pos) {
    return serverData(helper, pos, SoilChanceData.INSTANCE);
  }

  static CompoundTag pendingVisit(final GameTestHelper helper, final BlockPos pos) {
    return serverData(helper, pos, PendingVisitData.INSTANCE);
  }

  static CompoundTag chimeField(final GameTestHelper helper, final BlockPos pos) {
    return serverData(helper, pos, ChimeFieldData.INSTANCE);
  }

  private static CompoundTag serverData(
      final GameTestHelper helper,
      final BlockPos pos,
      final IServerDataProvider<BlockAccessor> provider) {
    final CompoundTag data = new CompoundTag();
    provider.appendServerData(data, accessor(helper, helper.absolutePos(pos)));
    return data;
  }

  /** A stand-in for Jade's accessor that answers only what the providers ask. */
  private static BlockAccessor accessor(final GameTestHelper helper, final BlockPos pos) {
    return (BlockAccessor)
        Proxy.newProxyInstance(
            BlockAccessor.class.getClassLoader(),
            new Class<?>[] {BlockAccessor.class},
            (proxy, method, args) ->
                switch (method.getName()) {
                  case "getLevel" -> helper.getLevel();
                  case "getPosition" -> pos;
                  case "getBlockState" -> helper.getLevel().getBlockState(pos);
                  case "getBlock" -> helper.getLevel().getBlockState(pos).getBlock();
                  default -> throw new UnsupportedOperationException(method.getName());
                });
  }
}
