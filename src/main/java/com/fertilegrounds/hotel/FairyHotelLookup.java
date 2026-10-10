package com.fertilegrounds.hotel;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.util.ModIdsUtil;
import java.util.Optional;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PoiHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;

/**
 * Finds occupied Fairy Hotels near a spot. Hotels register as a point of interest, the system
 * vanilla uses to find villager workstations, so a lookup reads the chunk's index instead of
 * scanning blocks.
 */
public final class FairyHotelLookup {

  private static final ResourceKey<PoiType> OCCUPIED_HOTEL =
      ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, ModIdsUtil.id("occupied_fairy_hotel"));

  /** Hotels this close to each other share one boost; see {@link FairyHotelRules}. */
  public static final int CLUSTER_RADIUS = 8;

  private FairyHotelLookup() {}

  /**
   * Registers every hotel block that holds a fairy as a point of interest. No villager can claim it
   * (0 tickets), since only lookups use it.
   */
  public static void register() {
    PoiHelper.register(OCCUPIED_HOTEL.identifier(), 0, 1, ModBlocks.FAIRY_HOTEL);
  }

  /** The closest occupied hotel within {@code radius} blocks (straight-line distance). */
  public static Optional<BlockPos> nearestOccupiedHotel(
      final ServerLevel level, final BlockPos pos, final int radius) {
    return level
        .getPoiManager()
        .findClosest(FairyHotelLookup::isOccupiedHotel, pos, radius, PoiManager.Occupancy.ANY);
  }

  /**
   * Whether an occupied hotel is within a box {@code horizontal} wide and {@code vertical} tall.
   */
  public static boolean isNearOccupiedHotel(
      final ServerLevel level, final BlockPos pos, final int horizontal, final int vertical) {
    return hotelsInSquare(level, pos, horizontal)
        .anyMatch(hotel -> Math.abs(hotel.getY() - pos.getY()) <= vertical);
  }

  /** Whether the hotel at {@code hotel} is the one in its cluster that boosts and adds honey. */
  public static boolean isClusterLeader(final ServerLevel level, final BlockPos hotel) {
    return FairyHotelRules.isClusterLeader(
        hotel.asLong(),
        level
            .getPoiManager()
            .getInRange(
                FairyHotelLookup::isOccupiedHotel, hotel, CLUSTER_RADIUS, PoiManager.Occupancy.ANY)
            .mapToLong(record -> record.getPos().asLong()));
  }

  private static Stream<BlockPos> hotelsInSquare(
      final ServerLevel level, final BlockPos pos, final int radius) {
    return level
        .getPoiManager()
        .getInSquare(FairyHotelLookup::isOccupiedHotel, pos, radius, PoiManager.Occupancy.ANY)
        .map(PoiRecord::getPos);
  }

  private static boolean isOccupiedHotel(final Holder<PoiType> type) {
    return type.is(OCCUPIED_HOTEL);
  }
}
