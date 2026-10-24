package com.fertilegrounds.grove;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

/**
 * A grove being built: the shrine's soil block, which sets the grove's ground level, and the index
 * of the next column in {@link GroveLayout#buildOrder()}.
 */
public record GroveJob(BlockPos soil, int next) {

  public static final Codec<GroveJob> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      BlockPos.CODEC.fieldOf("soil").forGetter(GroveJob::soil),
                      Codec.INT.fieldOf("next").forGetter(GroveJob::next))
                  .apply(instance, GroveJob::new));

  public GroveJob advancedBy(final int columns) {
    return new GroveJob(this.soil, this.next + columns);
  }
}
