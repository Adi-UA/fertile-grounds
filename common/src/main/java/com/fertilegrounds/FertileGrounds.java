package com.fertilegrounds;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.entity.ModEntities;
import com.fertilegrounds.fairy.FairyVisitScheduler;
import com.fertilegrounds.grove.GroveBuilder;
import com.fertilegrounds.hotel.FairySpeedAura;
import com.fertilegrounds.hotel.HotelChimes;
import com.fertilegrounds.item.GuideBookGift;
import com.fertilegrounds.item.ModDataComponents;
import com.fertilegrounds.item.ModItems;
import com.fertilegrounds.util.ModCriteria;
import com.fertilegrounds.util.ModMobEffects;
import com.fertilegrounds.util.ModSounds;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * What every loader's entry point calls into: which class fills each registry, and the handlers for
 * the game events the mod listens to. The loader entry points (in fabric/, neoforge/, and forge/)
 * only connect these to their own APIs.
 */
public final class FertileGrounds {
  public static final String MOD_ID = "fertilegrounds";

  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  /**
   * The class that fills each registry, in vanilla's registry order. NeoForge and Forge accept
   * entries only inside that registry's own event, so each class touches only its own registry.
   */
  public static final Map<ResourceKey<? extends Registry<?>>, Runnable> REGISTRATIONS =
      registrations();

  private FertileGrounds() {}

  private static Map<ResourceKey<? extends Registry<?>>, Runnable> registrations() {
    final Map<ResourceKey<? extends Registry<?>>, Runnable> map = new LinkedHashMap<>();
    map.put(Registries.SOUND_EVENT, ModSounds::register);
    map.put(Registries.MOB_EFFECT, ModMobEffects::register);
    map.put(Registries.BLOCK, ModBlocks::register);
    map.put(Registries.ENTITY_TYPE, ModEntities::register);
    map.put(Registries.ITEM, ModItems::register);
    map.put(Registries.TRIGGER_TYPE, ModCriteria::register);
    map.put(Registries.DATA_COMPONENT_TYPE, ModDataComponents::register);
    return map;
  }

  /** Runs at the end of each world's server tick. */
  public static void onLevelTick(final ServerLevel level) {
    FairyVisitScheduler.onLevelTick(level);
    FairySpeedAura.onLevelTick(level);
    HotelChimes.playDueNotes(level);
    GroveBuilder.tick(level);
  }

  /** Runs once the server has stopped, to forget state that belongs to the closed world. */
  public static void onServerStopped() {
    HotelChimes.onServerStopped();
    GroveBuilder.onServerStopped();
  }

  /** Runs each time a player joins the server. */
  public static void onPlayerJoin(final ServerPlayer player) {
    GuideBookGift.giveOnFirstJoin(player);
  }
}
