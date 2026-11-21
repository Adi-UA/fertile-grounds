package com.fertilegrounds.fairy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fertilegrounds.fairy.GrowthRings.Offset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GrowthRingsTest {

  @Test
  void ringZeroIsTheCenter() {
    assertEquals(List.of(new Offset(0, 0)), GrowthRings.ring(0));
  }

  @Test
  void ringsHoldOneEightAndSixteen() {
    assertEquals(8, GrowthRings.ring(1).size());
    assertEquals(16, GrowthRings.ring(2).size());
  }

  @Test
  void ringsTogetherCoverTheFiveByFiveOnce() {
    final Set<Offset> seen = new HashSet<>();
    for (int ring = 0; ring <= 2; ring++) {
      for (final Offset offset : GrowthRings.ring(ring)) {
        assertTrue(seen.add(offset), "offset in two rings: " + offset);
      }
    }
    assertEquals(25, seen.size());
  }
}
