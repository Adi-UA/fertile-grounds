package com.fertilegrounds.block;

import net.minecraft.world.level.block.LanternBlock;

/**
 * A Fairy Hotel: a lantern that hangs and sits like vanilla's. Empty and occupied hotels are two
 * blocks of this class, so each has its own item and loot table and an occupied hotel keeps its
 * fairy when broken and placed again, with no block entity or item data.
 */
public class FairyHotelBlock extends LanternBlock {

  private final boolean occupied;

  public FairyHotelBlock(final Properties properties, final boolean occupied) {
    super(properties);
    this.occupied = occupied;
  }

  public boolean isOccupied() {
    return this.occupied;
  }
}
