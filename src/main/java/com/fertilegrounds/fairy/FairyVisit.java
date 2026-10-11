package com.fertilegrounds.fairy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

/**
 * A promised fairy visit to the crop at {@code pos}, which can happen at night once the game time
 * reaches {@code arrivalTick}. The owner is the player the visit is for, who may get a gift.
 */
public record FairyVisit(BlockPos pos, long arrivalTick, Source source, Optional<UUID> owner) {

  /** Where a visit came from, which decides its gift chance. */
  public enum Source implements StringRepresentable {
    /** A player's nightly roll. */
    NATURAL,
    /** A player used Fairy Dust. */
    DUST,
    /** No player to reward: a dispenser, or a visit saved before owners existed (1.2.0). */
    UNOWNED;

    public static final Codec<Source> CODEC = StringRepresentable.fromEnum(Source::values);

    @Override
    public String getSerializedName() {
      return name().toLowerCase(Locale.ROOT);
    }
  }

  /**
   * Tells Fabric how to save a visit to disk, so a dust-summoned visit survives a restart. Source
   * and owner are optional, so visits saved by 1.2.0 still load, as unowned.
   */
  public static final Codec<FairyVisit> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      BlockPos.CODEC.fieldOf("pos").forGetter(FairyVisit::pos),
                      Codec.LONG.fieldOf("arrival_tick").forGetter(FairyVisit::arrivalTick),
                      Source.CODEC
                          .optionalFieldOf("source", Source.UNOWNED)
                          .forGetter(FairyVisit::source),
                      UUIDUtil.CODEC.optionalFieldOf("owner").forGetter(FairyVisit::owner))
                  .apply(instance, FairyVisit::new));

  /** A visit for {@code owner}, or an unowned one when there's no player (a dispenser). */
  public static FairyVisit of(
      final BlockPos pos, final long arrivalTick, final Source source, @Nullable final UUID owner) {
    return owner == null
        ? new FairyVisit(pos, arrivalTick, Source.UNOWNED, Optional.empty())
        : new FairyVisit(pos, arrivalTick, source, Optional.of(owner));
  }
}
