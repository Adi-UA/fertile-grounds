package com.fertilegrounds.grove;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fertilegrounds.grove.GroveLayout.Cell;
import com.fertilegrounds.grove.GroveLayout.Offset;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class GroveLayoutTest {

  private static final int R = GroveLayout.RADIUS;

  @Test
  void countsMatchTheSpec() {
    final Map<Cell, Integer> counts = new EnumMap<>(Cell.class);
    for (int dx = -R; dx <= R; dx++) {
      for (int dz = -R; dz <= R; dz++) {
        counts.merge(GroveLayout.cell(dx, dz), 1, Integer::sum);
      }
    }
    assertEquals(81, counts.get(Cell.CENTER));
    assertEquals(8, counts.get(Cell.WATER));
    assertEquals(8 * 80, counts.get(Cell.FARMLAND));
    assertEquals(4, counts.get(Cell.HIVE_POST));
    assertEquals(12, counts.get(Cell.HEDGE_POST));
    assertEquals(35 * 35 - 31 * 31, counts.get(Cell.MEADOW));
  }

  @Test
  void beyondTheEdgeIsOutside() {
    assertEquals(Cell.OUTSIDE, GroveLayout.cell(R + 1, 0));
    assertEquals(Cell.MEADOW, GroveLayout.cell(R, -R));
  }

  /** Vanilla farmland stays wet when water sits within 4 blocks horizontally. */
  @Test
  void everyFarmlandBlockReachesWater() {
    final List<Offset> water = new ArrayList<>();
    final List<Offset> farmland = new ArrayList<>();
    for (int dx = -R; dx <= R; dx++) {
      for (int dz = -R; dz <= R; dz++) {
        final Cell cell = GroveLayout.cell(dx, dz);
        if (cell == Cell.WATER) {
          water.add(new Offset(dx, dz));
        } else if (cell == Cell.FARMLAND) {
          farmland.add(new Offset(dx, dz));
        }
      }
    }
    for (final Offset spot : farmland) {
      assertTrue(
          water.stream()
              .anyMatch(
                  w -> Math.abs(w.dx() - spot.dx()) <= 4 && Math.abs(w.dz() - spot.dz()) <= 4),
          "farmland at " + spot + " is out of water's reach");
    }
  }

  @Test
  void buildOrderCoversEverythingButTheShrineFromTheInsideOut() {
    final List<Offset> order = GroveLayout.buildOrder();
    assertEquals(35 * 35 - 81, order.size());
    assertEquals(GroveLayout.CENTER_RADIUS + 1, order.getFirst().ring());
    assertEquals(R, order.getLast().ring());
    for (int i = 1; i < order.size(); i++) {
      assertTrue(order.get(i - 1).ring() <= order.get(i).ring());
    }
  }
}
