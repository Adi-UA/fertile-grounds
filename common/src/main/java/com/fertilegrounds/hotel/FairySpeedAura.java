package com.fertilegrounds.hotel;

import com.fertilegrounds.util.ModGameRules;
import com.fertilegrounds.util.ModMobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * Gives Fairy Speed to players near an occupied hotel, like a beacon: every second it refreshes a
 * 3-second effect, so the effect ends within 3 seconds of leaving. Refreshing one effect, rather
 * than adding a modifier per hotel, means several hotels never stack.
 */
public final class FairySpeedAura {

  /** Players this close to an occupied hotel get Fairy Speed. */
  public static final int RADIUS = 8;

  private static final int CHECK_INTERVAL_TICKS = 20;

  private static final int DURATION_TICKS = 60;

  private FairySpeedAura() {}

  public static void onLevelTick(final ServerLevel level) {
    if (level.getGameTime() % CHECK_INTERVAL_TICKS != 0
        || !level.getGameRules().get(ModGameRules.FAIRY_HOTEL_SPEED)) {
      return;
    }
    for (final ServerPlayer player : level.players()) {
      if (FairyHotelLookup.nearestOccupiedHotel(level, player.blockPosition(), RADIUS)
          .isPresent()) {
        // Ambient and with an icon, the same look a beacon's effect has.
        player.addEffect(
            new MobEffectInstance(ModMobEffects.FAIRY_SPEED, DURATION_TICKS, 0, true, true, true));
      }
    }
  }
}
