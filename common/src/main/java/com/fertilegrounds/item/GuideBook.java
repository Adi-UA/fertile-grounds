package com.fertilegrounds.item;

import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;

/**
 * The Fertile Grounds Guide: a vanilla written book whose pages are translation keys, so each
 * player reads it in their own language. The book's title field only holds plain text, so it stays
 * blank and a translated item name names the book instead.
 *
 * <p>The crafting recipe ({@code recipe/guide_book.json}) writes the same components in JSON, and a
 * GameTest checks the two books match.
 */
public final class GuideBook {

  public static final int PAGE_COUNT = 13;

  /** Shown as "by Fertile Grounds" in the tooltip; a name, so it isn't translated. */
  public static final String AUTHOR = "Fertile Grounds";

  private GuideBook() {}

  /** A fresh copy of the guide. */
  public static ItemStack create() {
    final List<Filterable<Component>> pages =
        IntStream.rangeClosed(1, PAGE_COUNT)
            .mapToObj(
                page ->
                    Filterable.<Component>passThrough(
                        Component.translatable("book.fertilegrounds.guide.page." + page)))
            .toList();
    final ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
    // Resolved, so the server never bakes the pages into its own language.
    book.set(
        DataComponents.WRITTEN_BOOK_CONTENT,
        new WrittenBookContent(Filterable.passThrough(""), AUTHOR, 0, pages, true));
    book.set(DataComponents.ITEM_NAME, Component.translatable("item.fertilegrounds.guide_book"));
    return book;
  }
}
