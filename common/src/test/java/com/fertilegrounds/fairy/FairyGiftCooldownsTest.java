package com.fertilegrounds.fairy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.serialization.JsonOps;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FairyGiftCooldownsTest {

  @Test
  void cooldownsSurviveSavingAndLoading() {
    final Map<UUID, Long> saved = Map.of(UUID.randomUUID(), 12L, UUID.randomUUID(), 40L);
    final var encoded = FairyGiftCooldowns.CODEC.encodeStart(JsonOps.INSTANCE, saved).getOrThrow();
    assertEquals(saved, FairyGiftCooldowns.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow());
  }
}
