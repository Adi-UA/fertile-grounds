package com.fertilegrounds.fairy;

import java.util.ArrayList;
import java.util.List;

/**
 * The rings a fairy grows outward from its crop. Ring {@code n} holds every spot whose larger
 * horizontal offset from the center is exactly {@code n} (Chebyshev distance), so ring 0 is the
 * center, ring 1 the 8 around it, and ring 2 the outer 16 of a 5x5 patch.
 */
public final class GrowthRings {

  /** A horizontal offset from the center crop. */
  public record Offset(int dx, int dz) {}

  private GrowthRings() {}

  public static List<Offset> ring(final int ring) {
    final List<Offset> offsets = new ArrayList<>();
    for (int dx = -ring; dx <= ring; dx++) {
      for (int dz = -ring; dz <= ring; dz++) {
        if (Math.max(Math.abs(dx), Math.abs(dz)) == ring) {
          offsets.add(new Offset(dx, dz));
        }
      }
    }
    return offsets;
  }
}
