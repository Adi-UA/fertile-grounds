package com.fertilegrounds.fairy;

import com.fertilegrounds.FertileGrounds;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.MoonPhase;

/** The Harvest Moon: full-moon nights, when Bean Fairies are likelier to visit. */
public final class HarvestMoon {

  private HarvestMoon() {}

  /**
   * Since 1.21.11, the moon phase is an "environment attribute" of the dimension rather than a
   * method on the level. Phase 0 is the full moon, so the very first night of a world is one.
   */
  public static boolean isTonight(final ServerLevel level) {
    return level.environmentAttributes().getDimensionValue(EnvironmentAttributes.MOON_PHASE)
        == MoonPhase.FULL_MOON;
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
