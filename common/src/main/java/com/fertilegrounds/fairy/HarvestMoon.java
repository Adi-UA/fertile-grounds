package com.fertilegrounds.fairy;

import com.fertilegrounds.FertileGrounds;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/** The Harvest Moon: full-moon nights, when Bean Fairies are likelier to visit. */
public final class HarvestMoon {

  /**
   * Whether the last nightfall was a Harvest Moon, per world. Visits grown at dawn belong to that
   * night, but by dawn the moon phase has already moved on. Weak keys let closed worlds unload.
   */
  private static final Map<ServerLevel, Boolean> LAST_NIGHT = new WeakHashMap<>();

  private HarvestMoon() {}

  static void rememberTonight(final ServerLevel level, final boolean harvestMoon) {
    LAST_NIGHT.put(level, harvestMoon);
  }

  /** Whether the night that just ended had a Harvest Moon, for visits grown at dawn. */
  public static boolean wasLastNight(final ServerLevel level) {
    return LAST_NIGHT.getOrDefault(level, false);
  }

  /** Phase 0 is the full moon, so the very first night of a world is one. */
  public static boolean isTonight(final ServerLevel level) {
    return level.getMoonPhase() == 0;
  }

  /** Tells every player in {@code level} with a chat line and a soft bell. */
  public static void announce(final ServerLevel level) {
    FertileGrounds.LOGGER.info("A Harvest Moon rises tonight");
    for (final ServerPlayer player : level.players()) {
      player.sendSystemMessage(Component.translatable("message.fertilegrounds.harvest_moon"));
      level.playSound(
          null, player.blockPosition(), SoundEvents.BELL_RESONATE, SoundSource.AMBIENT, 0.4F, 1.0F);
    }
  }
}
