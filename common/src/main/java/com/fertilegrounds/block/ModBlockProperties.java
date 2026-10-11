package com.fertilegrounds.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * Factory methods for the {@link BlockBehaviour.Properties} each block tier is constructed with.
 * Kept separate from {@link ModBlocks} so that class stays focused on registration, not
 * block-config authoring.
 *
 * <p>Each method returns a fresh instance on every call, matching vanilla's own convention in
 * {@code Blocks.java} — a single {@code Properties} is never reused across more than one block
 * registration there, so we don't either.
 */
final class ModBlockProperties {

  private ModBlockProperties() {}

  /**
   * @return properties matching vanilla {@code Blocks.DIRT}'s own settings
   */
  static BlockBehaviour.Properties dirtProperties() {
    return BlockBehaviour.Properties.of()
        .mapColor(MapColor.DIRT)
        .strength(0.5F)
        .sound(SoundType.GRAVEL);
  }

  /**
   * @return properties matching vanilla {@code Blocks.FARMLAND}'s own settings
   */
  static BlockBehaviour.Properties farmlandProperties() {
    return BlockBehaviour.Properties.of()
        .mapColor(MapColor.DIRT)
        .randomTicks()
        .strength(0.6F)
        .sound(SoundType.GRAVEL)
        .isSuffocating((state, level, pos) -> true);
  }

  /**
   * @return properties matching vanilla {@code Blocks.SAND}'s own settings, plus {@code
   *     randomTicks()} (vanilla sand has no passive tick behavior; ours needs one)
   */
  static BlockBehaviour.Properties sandProperties() {
    return BlockBehaviour.Properties.of()
        .mapColor(MapColor.SAND)
        .randomTicks()
        .strength(0.5F)
        .sound(SoundType.SAND);
  }

  /**
   * @return vanilla {@code Blocks.LANTERN}'s settings with a wooden map color and the hotel's own
   *     light level
   */
  static BlockBehaviour.Properties fairyHotelProperties(final int lightLevel) {
    return BlockBehaviour.Properties.of()
        .mapColor(MapColor.WOOD)
        .forceSolidOn()
        .strength(3.5F)
        .sound(SoundType.LANTERN)
        .lightLevel(state -> lightLevel)
        .noOcclusion()
        .pushReaction(PushReaction.POPPED);
  }
}
