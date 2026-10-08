package com.fertilegrounds.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * The tags that decide which blocks and items Fertile Grounds treats as crops and flowers. The
 * defaults live in {@code data/fertilegrounds/tags/}, and any datapack or mod can add to them, so
 * modded content works without code.
 */
public final class ModTags {

  /** Crops a fairy may grow. Defaults to {@code #minecraft:crops}. */
  public static final TagKey<Block> FAIRY_CROPS = block("fairy_crops");

  /**
   * Flowers that count for flower-based features. Defaults to {@code #minecraft:small_flowers} plus
   * the conventional {@code #c:flowers/small}, so other mods' small flowers count automatically.
   */
  public static final TagKey<Block> FAIRY_FLOWERS = block("fairy_flowers");

  /** The item form of {@link #FAIRY_FLOWERS}, for features that take a flower item. */
  public static final TagKey<Item> FAIRY_FLOWER_ITEMS =
      TagKey.create(Registries.ITEM, ModIdsUtil.id("fairy_flowers"));

  private ModTags() {}

  private static TagKey<Block> block(final String path) {
    return TagKey.create(Registries.BLOCK, ModIdsUtil.id(path));
  }
}
