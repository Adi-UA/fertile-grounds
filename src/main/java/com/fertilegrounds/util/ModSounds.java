package com.fertilegrounds.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

/**
 * The mod's sound events. Each one maps to audio files in {@code
 * assets/fertilegrounds/sounds.json}; they reuse vanilla's files, so the mod ships no audio of its
 * own.
 */
public final class ModSounds {

  /** A Bean Fairy appearing for a visit: a soft amethyst shimmer. */
  public static final SoundEvent BEAN_FAIRY_ARRIVE = register("entity.bean_fairy.arrive");

  /** A Chime Hotel's note: vanilla's note block chime, played 3 times at rising pitches. */
  public static final SoundEvent CHIME_HOTEL_CHIME = register("block.chime_fairy_hotel.chime");

  private ModSounds() {}

  /** Loads this class so the static fields above register their sounds during mod startup. */
  public static void register() {}

  private static SoundEvent register(final String path) {
    final Identifier id = ModIdsUtil.id(path);
    return Registry.register(
        BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
  }
}
