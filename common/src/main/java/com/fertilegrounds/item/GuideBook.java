package com.fertilegrounds.item;

import java.util.stream.IntStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;

/**
 * A vanilla written book whose pages are translation keys, so each player reads it in their own
 * language. The title field holds only plain text, so a translated item name names the book. The
 * recipe {@code recipes/guide_book.json} builds the same book.
 */
public final class GuideBook {

  public static final int PAGE_COUNT = 13;

  /** Shown as "by Fertile Grounds" in the tooltip; a name, so it isn't translated. */
  public static final String AUTHOR = "Fertile Grounds";

  private GuideBook() {}

  /** A fresh copy of the guide. */
  public static ItemStack create() {
    final ListTag pages = new ListTag();
    IntStream.rangeClosed(1, PAGE_COUNT)
        .mapToObj(
            page ->
                StringTag.valueOf(
                    Component.Serializer.toJson(
                        Component.translatable("book.fertilegrounds.guide.page." + page))))
        .forEach(pages::add);
    final ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
    final CompoundTag tag = book.getOrCreateTag();
    tag.putString(WrittenBookItem.TAG_TITLE, "");
    tag.putString(WrittenBookItem.TAG_AUTHOR, AUTHOR);
    tag.put(WrittenBookItem.TAG_PAGES, pages);
    // Resolved, so the server never bakes the pages into its own language.
    tag.putBoolean(WrittenBookItem.TAG_RESOLVED, true);
    // Not italic, so it reads as the item's own name, not one given at an anvil.
    book.setHoverName(
        Component.translatable("item.fertilegrounds.guide_book")
            .withStyle(style -> style.withItalic(false)));
    return book;
  }
}
