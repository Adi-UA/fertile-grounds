package com.fertilegrounds.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * The {@link BlockBehaviour.Properties} for each block. Each call returns a new instance, since
 * vanilla's {@code Blocks} never shares one between blocks.
 */
final class ModBlockProperties {

  private ModBlockProperties() {}

  /** Vanilla dirt's settings. */
  static BlockBehaviour.Properties dirtProperties() {
    return BlockBehaviour.Properties.of()
        .mapColor(MapColor.DIRT)
        .strength(0.5F)
        .sound(SoundType.GRAVEL);
  }

  /** Vanilla farmland's settings. */
  static BlockBehaviour.Properties farmlandProperties() {
    return BlockBehaviour.Properties.of()
        .mapColor(MapColor.DIRT)
        .randomTicks()
        .strength(0.6F)
        .sound(SoundType.GRAVEL)
        .isSuffocating((state, level, pos) -> true);
  }

  /** Vanilla sand's settings, plus random ticks for the boost. */
  static BlockBehaviour.Properties sandProperties() {
    return BlockBehaviour.Properties.of()
        .mapColor(MapColor.SAND)
        .randomTicks()
        .strength(0.5F)
        .sound(SoundType.SAND);
  }

  /** Vanilla lantern's settings with a wood map color and the given light level. */
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
