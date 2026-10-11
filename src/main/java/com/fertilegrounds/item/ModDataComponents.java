package com.fertilegrounds.item;

import com.fertilegrounds.util.ModIdsUtil;
import com.mojang.serialization.Codec;
import java.util.function.UnaryOperator;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;

/** The mod's item data components, registered like vanilla's {@code DataComponents}. */
public final class ModDataComponents {

  /** The game time a Jarred Fairy slips away; saved with the item and sent to clients. */
  public static final DataComponentType<Long> SLIPS_AWAY_AT =
      register(
          "slips_away_at",
          b -> b.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));

  private ModDataComponents() {}

  /** Loads this class so the static fields above register during mod startup. */
  public static void register() {}

  private static <T> DataComponentType<T> register(
      final String path, final UnaryOperator<DataComponentType.Builder<T>> builder) {
    return Registry.register(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        ModIdsUtil.id(path),
        builder.apply(DataComponentType.builder()).build());
  }
}
