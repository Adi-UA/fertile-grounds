package com.fertilegrounds.grove;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Which block goes where in a Fairy Grove, as offsets from the shrine. Kept free of Minecraft types
 * so JUnit can check the layout.
 *
 * <p>The grove is 35x35: a 3x3 grid of 9x9 plots with a 1-block border around each (31 wide), plus
 * a 2-block meadow rim. The center plot holds the player's shrine and is never touched; the other 8
 * are farmland around one water block, which vanilla farmland reaches up to 4 blocks away.
 */
public final class GroveLayout {

  public enum Cell {
    CENTER,
    FARMLAND,
    WATER,
    BORDER,
    HIVE_POST,
    HEDGE_POST,
    MEADOW,
    OUTSIDE
  }

  /** A horizontal offset from the shrine. */
  public record Offset(int dx, int dz) {

    /** The larger of the two offsets: 0 at the shrine, 17 at the grove's edge. */
    public int ring() {
      return Math.max(Math.abs(this.dx), Math.abs(this.dz));
    }
  }

  /** Half the grove's width. */
  public static final int RADIUS = 17;

  /** Half the width of the shrine's own plot. */
  public static final int CENTER_RADIUS = 4;

  private static final int INNER_LINE = 5;

  private static final int OUTER_LINE = 15;

  private static final int PLOT_SPACING = 10;

  private GroveLayout() {}

  public static Cell cell(final int dx, final int dz) {
    final int ax = Math.abs(dx);
    final int az = Math.abs(dz);
    if (ax > RADIUS || az > RADIUS) {
      return Cell.OUTSIDE;
    }
    if (ax <= CENTER_RADIUS && az <= CENTER_RADIUS) {
      return Cell.CENTER;
    }
    if (ax > OUTER_LINE || az > OUTER_LINE) {
      return Cell.MEADOW;
    }
    final boolean onLineX = ax == INNER_LINE || ax == OUTER_LINE;
    final boolean onLineZ = az == INNER_LINE || az == OUTER_LINE;
    if (onLineX && onLineZ) {
      return ax == INNER_LINE && az == INNER_LINE ? Cell.HIVE_POST : Cell.HEDGE_POST;
    }
    if (onLineX || onLineZ) {
      return Cell.BORDER;
    }
    return dx == plotCenter(dx) && dz == plotCenter(dz) ? Cell.WATER : Cell.FARMLAND;
  }

  /**
   * Every spot the fairies rebuild, nearest the shrine first and going around each ring, so the
   * garden grows outward from the shrine.
   */
  public static List<Offset> buildOrder() {
    final List<Offset> order = new ArrayList<>();
    for (int dx = -RADIUS; dx <= RADIUS; dx++) {
      for (int dz = -RADIUS; dz <= RADIUS; dz++) {
        if (cell(dx, dz) != Cell.CENTER) {
          order.add(new Offset(dx, dz));
        }
      }
    }
    order.sort(
        Comparator.comparingInt(Offset::ring)
            .thenComparingDouble(offset -> Math.atan2(offset.dz(), offset.dx())));
    return List.copyOf(order);
  }

  /** The center of the plot this offset falls in, along one axis. */
  private static int plotCenter(final int d) {
    return Math.abs(d) <= CENTER_RADIUS ? 0 : Integer.signum(d) * PLOT_SPACING;
  }
}
