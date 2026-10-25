package com.fertilegrounds.platform;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.GameRules;

/**
 * What shared code needs from the mod loader: saving data on worlds and players, registering
 * gamerules, and checking for other mods.
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

  boolean isModLoaded(String modId);
}
