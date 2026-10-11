package com.fertilegrounds.fairy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

class FairyVisitCodecTest {

  @Test
  void aVisitSavedBy120LoadsUnowned() {
    final FairyVisit visit =
        FairyVisit.CODEC
            .parse(
                JsonOps.INSTANCE, JsonParser.parseString("{\"pos\":[1,64,2],\"arrival_tick\":100}"))
            .getOrThrow();
    assertEquals(new BlockPos(1, 64, 2), visit.pos());
    assertEquals(FairyVisit.Source.UNOWNED, visit.source());
    assertTrue(visit.owner().isEmpty());
  }

  @Test
  void anOwnedVisitRoundTrips() {
    final FairyVisit visit =
        FairyVisit.of(new BlockPos(1, 64, 2), 100, FairyVisit.Source.DUST, UUID.randomUUID());
    final var json = FairyVisit.CODEC.encodeStart(JsonOps.INSTANCE, visit).getOrThrow();
    assertEquals(visit, FairyVisit.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow());
  }

  @Test
  void noPlayerMeansUnowned() {
    final FairyVisit visit = FairyVisit.of(BlockPos.ZERO, 0, FairyVisit.Source.DUST, null);
    assertEquals(FairyVisit.Source.UNOWNED, visit.source());
    assertTrue(visit.owner().isEmpty());
  }
}
