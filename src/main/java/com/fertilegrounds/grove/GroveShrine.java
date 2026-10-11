package com.fertilegrounds.grove;

import com.fertilegrounds.block.FairyHotelBlock;
import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;

/**
 * The shrine a diamond wakes: a Chime Hotel on Super Enriched Dirt, an occupied hotel 3 blocks out
 * on each side, and flowers around it.
 */
public final class GroveShrine {

  /** Whether the shrine is complete, and if not, what the player is told. */
  public enum Result {
    OK(""),
    NO_SOIL("message.fertilegrounds.grove.no_soil"),
    MISSING_HOTEL("message.fertilegrounds.grove.missing_hotel"),
    TOO_FEW_FLOWERS("message.fertilegrounds.grove.too_few_flowers");

    private final String messageKey;

    Result(final String messageKey) {
      this.messageKey = messageKey;
    }

    public String messageKey() {
      return this.messageKey;
    }
  }

  public static final int HOTEL_DISTANCE = 3;

  public static final int MIN_FLOWERS = 4;

  private GroveShrine() {}

  public static Result check(final BlockGetter level, final BlockPos chimeHotel) {
    if (!level.getBlockState(chimeHotel.below()).is(ModBlocks.SUPER_ENRICHED_DIRT)) {
      return Result.NO_SOIL;
    }
    for (final Direction side : Direction.Plane.HORIZONTAL) {
      if (!(level.getBlockState(chimeHotel.relative(side, HOTEL_DISTANCE)).getBlock()
              instanceof FairyHotelBlock hotel)
          || !hotel.isOccupied()) {
        return Result.MISSING_HOTEL;
      }
    }
    int flowers = 0;
    for (final BlockPos pos :
        BlockPos.betweenClosed(
            chimeHotel.offset(-HOTEL_DISTANCE, -1, -HOTEL_DISTANCE),
            chimeHotel.offset(HOTEL_DISTANCE, 1, HOTEL_DISTANCE))) {
      if (level.getBlockState(pos).is(ModTags.FAIRY_FLOWERS)) {
        flowers++;
      }
    }
    return flowers >= MIN_FLOWERS ? Result.OK : Result.TOO_FEW_FLOWERS;
  }
}
