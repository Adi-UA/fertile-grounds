package com.fertilegrounds.fairy;

import com.fertilegrounds.util.ModSavedData;
import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;

/**
 * The in-game day each player last got a fairy gift. Saved on the world (like {@link FairyVisits})
 * rather than on the player, so it survives restarts and keeps counting while they're offline.
 */
public final class FairyGiftCooldowns {

  /** Player UUID (as text, since JSON-like keys must be strings) to last gift day. */
  public static final Codec<Map<UUID, Long>> CODEC =
      Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.LONG);

  private FairyGiftCooldowns() {}

  /** The day of the player's last gift, or -1 if they never got one. */
  public static long lastGiftDay(final ServerLevel level, final UUID player) {
    return ModSavedData.FAIRY_GIFT_COOLDOWNS.get(level).getOrDefault(player, -1L);
  }

  public static void recordGift(final ServerLevel level, final UUID player, final long day) {
    final Map<UUID, Long> updated = new HashMap<>(ModSavedData.FAIRY_GIFT_COOLDOWNS.get(level));
    updated.put(player, day);
    ModSavedData.FAIRY_GIFT_COOLDOWNS.set(level, Map.copyOf(updated));
  }
}
