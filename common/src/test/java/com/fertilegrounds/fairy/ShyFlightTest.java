package com.fertilegrounds.fairy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fertilegrounds.fairy.ShyFlight.Point;
import org.junit.jupiter.api.Test;

class ShyFlightTest {

  private static final Point CROP = new Point(0, 64, 0);

  @Test
  void fleesStraightAwayFromThePlayer() {
    final Point target = ShyFlight.target(new Point(1, 66, 0), new Point(-2, 64, 0), CROP);
    assertEquals(4.0, target.x(), 1e-9); // 6 blocks past the player, on the far side
    assertEquals(0.0, target.z(), 1e-9);
  }

  @Test
  void endsSixBlocksFromThePlayerWhenThereIsRoom() {
    final Point player = new Point(0, 64, -3);
    final Point target = ShyFlight.target(new Point(0, 66, -1), player, CROP);
    assertEquals(6.0, Math.hypot(target.x() - player.x(), target.z() - player.z()), 1e-9);
  }

  @Test
  void neverStraysMoreThanEightBlocksFromTheCrop() {
    final Point target = ShyFlight.target(new Point(6, 66, 0), new Point(4, 64, 0), CROP);
    assertTrue(Math.hypot(target.x(), target.z()) <= 8 + 1e-9);
    assertEquals(8.0, target.x(), 1e-9);
  }

  @Test
  void keepsItsOwnHeight() {
    assertEquals(66.0, ShyFlight.target(new Point(1, 66, 0), new Point(0, 64, 0), CROP).y(), 1e-9);
  }

  @Test
  void picksADirectionWhenThePlayerIsOnTopOfIt() {
    final Point target = ShyFlight.target(new Point(0, 66, 0), new Point(0, 66, 0), CROP);
    assertEquals(6.0, Math.hypot(target.x(), target.z()), 1e-9);
  }
}
