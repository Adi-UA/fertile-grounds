package com.fertilegrounds.fairy;

/**
 * Where a shy fairy darts when a player rushes it: straight away from the player, but never so far
 * that it abandons its crop. Kept free of Minecraft types so JUnit can test it.
 */
public final class ShyFlight {

  /** A player closer than this, and not sneaking, scares the fairy. */
  public static final double SCARE_DISTANCE = 4;

  /** How far from the player the fairy darts. */
  public static final double FLEE_DISTANCE = 6;

  /** The fairy never darts farther than this from its crop. */
  public static final double MAX_DISTANCE_FROM_CROP = 8;

  /** A point in the world; the fairy keeps its own height. */
  public record Point(double x, double y, double z) {}

  private ShyFlight() {}

  /** The spot to dart to, horizontally away from {@code player}, clamped to stay near the crop. */
  public static Point target(final Point fairy, final Point player, final Point crop) {
    double dx = fairy.x() - player.x();
    double dz = fairy.z() - player.z();
    final double length = Math.hypot(dx, dz);
    // A player standing exactly on the fairy has no "away"; pick any direction.
    if (length < 1e-6) {
      dx = 1;
      dz = 0;
    } else {
      dx /= length;
      dz /= length;
    }
    double x = player.x() + dx * FLEE_DISTANCE;
    double z = player.z() + dz * FLEE_DISTANCE;
    final double fromCrop = Math.hypot(x - crop.x(), z - crop.z());
    if (fromCrop > MAX_DISTANCE_FROM_CROP) {
      final double scale = MAX_DISTANCE_FROM_CROP / fromCrop;
      x = crop.x() + (x - crop.x()) * scale;
      z = crop.z() + (z - crop.z()) * scale;
    }
    return new Point(x, fairy.y(), z);
  }
}
