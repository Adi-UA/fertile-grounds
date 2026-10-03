# Design

## Context

Written books store pages as text components in the `written_book_content` data component on every 1.3.0 target. Text components can be `translate` keys, which the client resolves in its own language.

## Goals / Non-Goals

**Goals:** one `GuideBook.create()` that builds the book, used by the join handler and the recipe, so both give the same book.

**Non-Goals:** updating copies players already hold; they craft a new one.

## Decisions

- **Recipe result carries the components directly** (a recipe result with `components` for `written_book_content`), so no custom recipe type is needed. Confirm the 26.3 recipe result format accepts components before writing it.
- **Received flag as a persistent Fabric player attachment**, with `copyOnDeath`, so it survives death and restarts.
- **Join hook through `ServerPlayConnectionEvents.JOIN`**.
- **Mark the book `resolved: true`** so pages stay as translatable components.

## Risks / Trade-offs

- [Translated pages overflow a page in longer languages] → keep each page under about 180 English characters.
