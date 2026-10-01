package com.fertilegrounds.fairy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

/**
 * A promised fairy visit to the crop at {@code pos}, which can happen at night once the game time
 * reaches {@code arrivalTick}.
 */
public record FairyVisit(BlockPos pos, long arrivalTick) {

  /** Tells Fabric how to save a visit to disk, so a dust-summoned visit survives a restart. */
  public static final Codec<FairyVisit> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      BlockPos.CODEC.fieldOf("pos").forGetter(FairyVisit::pos),
                      Codec.LONG.fieldOf("arrival_tick").forGetter(FairyVisit::arrivalTick))
                  .apply(instance, FairyVisit::new));
}
