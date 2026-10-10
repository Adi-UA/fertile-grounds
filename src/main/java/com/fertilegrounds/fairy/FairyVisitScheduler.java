package com.fertilegrounds.fairy;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.blight.Blight;
import com.fertilegrounds.entity.BeanFairy;
import com.fertilegrounds.entity.ModEntities;
import com.fertilegrounds.util.CropGrowthUtil;
import com.fertilegrounds.util.ModGameRules;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

/**
 * Decides when Bean Fairies show up. Once a second, in the overworld only, it:
 *
 * <ol>
 *   <li>when night starts, announces a Harvest Moon if the moon is full, then rolls a rare natural
 *       visit for each player,
 *   <li>twice a second, shows firefly heralds over visits due within a minute,
 *   <li>sends a fairy to each visit that is due during the night,
 *   <li>grows any visits still pending at dawn directly, so sleeping through the night doesn't skip
 *       the fairy (it "came while you slept").
 * </ol>
 */
public final class FairyVisitScheduler {

  /** Visits are checked once a second; fairies don't need tick-perfect timing. */
  private static final int CHECK_INTERVAL_TICKS = 20;

  /** How far from a player a natural visit looks for crops (horizontal, and vertical). */
  private static final int NATURAL_SEARCH_RADIUS = 16;

  private static final int NATURAL_SEARCH_HEIGHT = 4;

  /** Crops within this many blocks of the target grow too (2 means a 5x5 patch). */
  public static final int GROWTH_RADIUS = 2;

  /** The fairy appears this many blocks above its target and flies down to it. */
  private static final int SPAWN_HEIGHT_ABOVE_TARGET = 6;

  /**
   * Night state from the previous check, per world. Weak keys let a world be garbage collected
   * after a singleplayer world is closed.
   */
  private static final Map<ServerLevel, Boolean> WAS_NIGHT = new WeakHashMap<>();

  private FairyVisitScheduler() {}

  public static void register() {
    ServerTickEvents.END_LEVEL_TICK.register(FairyVisitScheduler::onLevelTick);
  }

  /**
   * Promises a visit to the crop at {@code pos}, used by Fairy Dust.
   *
   * @param user the player who used the dust and owns the visit, or null for a dispenser
   * @return false if a visit to that crop is already pending
   */
  public static boolean requestVisit(
      final ServerLevel level, final BlockPos pos, @Nullable final Player user) {
    if (FairyVisits.hasVisitAt(level, pos)) {
      return false;
    }
    final int delay =
        level
            .getRandom()
            .nextIntBetweenInclusive(
                FairyTiming.DUST_DELAY_MIN_TICKS, FairyTiming.DUST_DELAY_MAX_TICKS);
    final long arrival =
        FairyTiming.dustArrivalTick(level.getGameTime(), level.isDarkOutside(), delay);
    FairyVisits.add(
        level,
        FairyVisit.of(
            pos.immutable(),
            arrival,
            FairyVisit.Source.DUST,
            user == null ? null : user.getUUID()));
    return true;
  }

  private static void onLevelTick(final ServerLevel level) {
    if (level.dimension() != Level.OVERWORLD) {
      return;
    }
    if (level.getGameTime() % FairyHeralds.INTERVAL_TICKS == 0) {
      FairyHeralds.tick(level);
    }
    if (level.getGameTime() % CHECK_INTERVAL_TICKS != 0) {
      return;
    }

    final boolean isNight = level.isDarkOutside();
    switch (FairyTiming.transition(WAS_NIGHT.put(level, isNight), isNight)) {
      case NIGHT_STARTED -> startNight(level);
      case DAY_STARTED -> growVisitsMissedOvernight(level);
      case NONE -> {}
    }
    if (isNight) {
      sendFairiesToDueVisits(level);
    }
  }

  private static void startNight(final ServerLevel level) {
    final boolean harvestMoon = HarvestMoon.isTonight(level);
    HarvestMoon.rememberTonight(level, harvestMoon);
    if (harvestMoon) {
      HarvestMoon.announce(level);
    }
    rollNaturalVisits(level, harvestMoon);
  }

  /**
   * Each player gets one roll per night, for one random crop near them. The crop is picked first
   * because the flowers and hives around it change the odds.
   */
  private static void rollNaturalVisits(final ServerLevel level, final boolean harvestMoon) {
    final GameRules rules = level.getGameRules();
    final int baseChance = rules.get(ModGameRules.FAIRY_VISIT_CHANCE);
    final FairyTiming.Bonuses bonuses =
        new FairyTiming.Bonuses(
            rules.get(ModGameRules.FAIRY_FLOWER_BONUS),
            rules.get(ModGameRules.FAIRY_HIVE_BONUS),
            rules.get(ModGameRules.HARVEST_MOON_MULTIPLIER));
    for (final ServerPlayer player : level.players()) {
      findCropNear(level, player.blockPosition())
          .ifPresent(
              crop -> {
                final FarmAttractions farm = FarmAttractions.around(level, crop);
                final double chance =
                    FairyTiming.visitChance(
                        baseChance, farm.flowerTypes(), farm.hasHive(), harvestMoon, bonuses);
                if (FairyTiming.rollsNaturalVisit(chance, level.getRandom().nextDouble() * 100)) {
                  scheduleNaturalVisit(level, crop, player);
                }
              });
    }
  }

  private static void scheduleNaturalVisit(
      final ServerLevel level, final BlockPos crop, final ServerPlayer player) {
    final int delay =
        level
            .getRandom()
            .nextIntBetweenInclusive(
                FairyTiming.NATURAL_DELAY_MIN_TICKS, FairyTiming.NATURAL_DELAY_MAX_TICKS);
    FairyVisits.add(
        level,
        FairyVisit.of(
            crop, level.getGameTime() + delay, FairyVisit.Source.NATURAL, player.getUUID()));
    FertileGrounds.LOGGER.debug("Natural fairy visit scheduled at {}", crop);
  }

  /**
   * Picks a random growable crop near {@code center}. It checks every spot (about 10,000, once per
   * player per night), since sampling a few hundred random spots would usually miss a small farm.
   */
  private static Optional<BlockPos> findCropNear(final ServerLevel level, final BlockPos center) {
    final List<BlockPos> crops =
        BlockPos.betweenClosedStream(
                center.offset(
                    -NATURAL_SEARCH_RADIUS, -NATURAL_SEARCH_HEIGHT, -NATURAL_SEARCH_RADIUS),
                center.offset(NATURAL_SEARCH_RADIUS, NATURAL_SEARCH_HEIGHT, NATURAL_SEARCH_RADIUS))
            .filter(pos -> CropGrowthUtil.canGrow(level, pos))
            .map(BlockPos::immutable)
            .toList();
    return Util.getRandomSafe(crops, level.getRandom());
  }

  private static void sendFairiesToDueVisits(final ServerLevel level) {
    final long now = level.getGameTime();
    // Copy first: the loop removes visits from the stored list.
    for (final FairyVisit visit : new ArrayList<>(FairyVisits.get(level))) {
      if (FairyTiming.isDue(visit.arrivalTick(), now, true) && level.isLoaded(visit.pos())) {
        spawnFairy(level, visit);
        FairyVisits.remove(level, visit);
      }
    }
  }

  private static void spawnFairy(final ServerLevel level, final FairyVisit visit) {
    final BlockPos target = visit.pos();
    final BeanFairy fairy = ModEntities.BEAN_FAIRY.create(level, EntitySpawnReason.EVENT);
    if (fairy == null) {
      return;
    }
    // Start above whatever is tallest there (trees, a roof), so the fairy comes down from open sky.
    final int spawnY =
        Math.max(
            target.getY() + SPAWN_HEIGHT_ABOVE_TARGET,
            level.getHeight(Heightmap.Types.MOTION_BLOCKING, target.getX(), target.getZ()) + 2);
    fairy.setPos(target.getX() + 0.5, spawnY, target.getZ() + 0.5);
    fairy.setVisit(visit);
    level.addFreshEntity(fairy);
    FairyVisitEvents.onFairyArrived(level, fairy);
    FertileGrounds.LOGGER.debug("Bean Fairy sent to {}", target);
  }

  private static void growVisitsMissedOvernight(final ServerLevel level) {
    final Set<ServerPlayer> toldPlayers = new HashSet<>();
    for (final FairyVisit visit : new ArrayList<>(FairyVisits.get(level))) {
      if (!level.isLoaded(visit.pos())) {
        continue;
      }
      CropGrowthUtil.growArea(level, visit.pos(), GROWTH_RADIUS);
      Blight.cureArea(level, visit.pos(), GROWTH_RADIUS);
      toldPlayers.addAll(FairyVisitEvents.onVisitGrown(level, visit, true));
      final BlockPos pos = visit.pos();
      level.sendParticles(
          ParticleTypes.HAPPY_VILLAGER,
          pos.getX() + 0.5,
          pos.getY() + 0.5,
          pos.getZ() + 0.5,
          30,
          GROWTH_RADIUS,
          0.5,
          GROWTH_RADIUS,
          0.0);
      FairyVisits.remove(level, visit);
    }
    FairyVisitEvents.tellVisitedOvernight(toldPlayers);
  }
}
