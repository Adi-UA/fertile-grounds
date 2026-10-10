package com.fertilegrounds.block;

/**
 * How rich a soil block is. Kept free of Minecraft types so JUnit can test the signal each tier
 * gives a comparator, which lets redstone sort or detect soil by tier.
 */
public enum SoilTier {
  ENRICHED(7),
  SUPER_ENRICHED(15);

  private final int comparatorSignal;

  SoilTier(final int comparatorSignal) {
    this.comparatorSignal = comparatorSignal;
  }

  /** The signal strength, 0 to 15, a comparator reads from soil of this tier. */
  public int comparatorSignal() {
    return this.comparatorSignal;
  }
}
