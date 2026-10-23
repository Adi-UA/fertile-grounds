package com.fertilegrounds.platform;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.GameRules;

/**
 * The few things shared code needs from the mod loader: saving data on worlds and players,
 * registering gamerules and spawn eggs, and checking for other mods. Everything else a loader does
 * (events, registration hooks) happens in that loader's own entry point, which calls into shared
 * code.
 */
public interface Platform {

  Platform INSTANCE = Services.load(Platform.class);

  /**
   * A value saved with each world (each dimension has its own), read back after a restart.
   *
   * @param path the id under this mod's namespace, which must never change once released
   * @param empty the value of a world that never saved one
   */
  <T> LevelData<T> levelData(String path, Codec<T> codec, T empty);

  /** A flag saved on each player, kept through death and restarts. */
  PlayerFlag playerFlag(String path);

  /** A whole-number gamerule; the {@code /gamerule} command rejects values outside min to max. */
  GameRules.Key<GameRules.IntegerValue> intRule(
      String name, GameRules.Category category, int defaultValue, int min, int max);

  GameRules.Key<GameRules.BooleanValue> boolRule(
      String name, GameRules.Category category, boolean defaultValue);

  /**
   * A spawn egg for one of the mod's mobs, tinted white so its painted texture shows as drawn.
   * Forge registers items before entity types, so its egg looks the type up later.
   */
  default SpawnEggItem spawnEgg(
      final Supplier<? extends EntityType<? extends Mob>> type, final Item.Properties properties) {
    return new SpawnEggItem(type.get(), 0xFFFFFF, 0xFFFFFF, properties);
  }

  boolean isModLoaded(String modId);
}
