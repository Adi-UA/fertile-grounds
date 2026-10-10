package com.fertilegrounds.block;

/** A soil block with a {@link SoilTier}, so features can ask how rich it is. */
public interface TieredSoil {

  SoilTier tier();
}
