# Design

## Context

Written books store pages as text components in the `written_book_content` data component on every 2.0.0 target. Text components can be `translate` keys, which the client resolves in its own language.

## Goals / Non-Goals

**Goals:** one `GuideBook.create()` that builds the book, used by the join handler and the recipe, so both give the same book.

**Non-Goals:** updating copies players already hold; they craft a new one.

## Decisions

- **Recipe result carries the components directly** (a recipe result with `components` for `written_book_content`), so no custom recipe type is needed. Confirm the 26.3 recipe result format accepts components before writing it.
- **Received flag as a persistent Fabric player attachment**, with `copyOnDeath`, so it survives death and restarts.
- **Join hook through `ServerPlayConnectionEvents.JOIN`**.
- **Voice follows Lexica Botania**, the best-known mod guidebook: it talks to the player as "you", says what each thing does in one or two short sentences, and keeps a little warmth ("Fairies love it!"). Plain words a high schooler reads easily (about grade 3 on the Flesch-Kincaid scale), no modding terms such as "random tick", and each page fits 14 book lines.
- **Mark the book `resolved: true`** so pages stay as translatable components.
- **Title through `item_name`.** The book's `title` field holds only plain text, so it stays blank and a translatable `minecraft:item_name` names the book; with a blank title, vanilla falls back to the item name.
- **Full inventory** drops the book the way `/give` does (`createItemStackToDrop`, no pickup delay, targeted at the player); 26.2 and older use `player.drop(stack, false, false)` instead.

## Risks / Trade-offs

- [Translated pages overflow a page in longer languages] → keep each page under about 180 English characters.
