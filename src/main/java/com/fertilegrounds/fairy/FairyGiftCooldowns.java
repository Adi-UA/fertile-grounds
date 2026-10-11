package com.fertilegrounds.fairy;

import com.fertilegrounds.util.ModIdsUtil;
import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;

/**
 * The in-game day each player last got a fairy gift. Saved on the world (like {@link FairyVisits})
 * rather than on the player, so it survives restarts and keeps counting while they're offline.
 */
public final class FairyGiftCooldowns {

  /** Player UUID (as text, since JSON-like keys must be strings) to last gift day. */
  static final Codec<Map<UUID, Long>> CODEC = Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.LONG);

  /**
   * Holds the attachment in its own class so reading {@link #CODEC} (as JUnit does) doesn't also
   * register it with Fabric; it registers the first time {@link #register} touches it.
   */
  private static final class Attachment {
    static final AttachmentType<Map<UUID, Long>> LAST_GIFT_DAY =
        AttachmentRegistry.createPersistent(ModIdsUtil.id("fairy_gift_cooldowns"), CODEC);
  }

  private FairyGiftCooldowns() {}

  /** Registers the attachment type before any world loads. */
  public static void register() {
    Objects.requireNonNull(Attachment.LAST_GIFT_DAY);
  }

  /** The day of the player's last gift, or -1 if they never got one. */
  public static long lastGiftDay(final ServerLevel level, final UUID player) {
    return level.getAttachedOrElse(Attachment.LAST_GIFT_DAY, Map.of()).getOrDefault(player, -1L);
  }

  public static void recordGift(final ServerLevel level, final UUID player, final long day) {
    final Map<UUID, Long> updated =
        new HashMap<>(level.getAttachedOrElse(Attachment.LAST_GIFT_DAY, Map.of()));
    updated.put(player, day);
    level.setAttached(Attachment.LAST_GIFT_DAY, Map.copyOf(updated));
  }
}
