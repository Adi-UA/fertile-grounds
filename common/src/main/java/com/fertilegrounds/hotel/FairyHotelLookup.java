package com.fertilegrounds.hotel;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.util.ModIdsUtil;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;

/**
 * Finds occupied Fairy Hotels near a spot. Hotels register as a point of interest, the system
 * vanilla uses to find villager workstations, so a lookup reads the chunk's index instead of
 * scanning blocks.
 */
public final class FairyHotelLookup {

  public static final ResourceKey<PoiType> OCCUPIED_HOTEL =
      ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, ModIdsUtil.id("occupied_fairy_hotel"));

  /** Hotels this close to each other share one boost; see {@link FairyHotelRules}. */
  public static final int CLUSTER_RADIUS = 8;

  /** Villagers that could claim the point of interest: none, since only lookups use it. */
  public static final int MAX_TICKETS = 0;

  /** How close a villager has to stand to use it, which lookups ignore. */
  public static final int VALID_RANGE = 1;

  private FairyHotelLookup() {}

  /** The hotel blocks that hold a fairy; each loader registers them as this point of interest. */
  public static List<Block> occupiedHotels() {
    return List.of(ModBlocks.FAIRY_HOTEL, ModBlocks.CHIME_FAIRY_HOTEL);
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
