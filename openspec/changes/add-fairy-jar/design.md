# Design

## Context

`BeanFairy` is never saved and lives only for its visit; `VisitCropGoal` drives it to the crop, grows the patch, then flies it away. Jars must outlive the fairy entity, so the jar stores state, not an entity.

## Goals / Non-Goals

**Goals:** all jar rules (days left, feeding, daily friendship cap, permanence, light level) in a pure `FairyJarRules` class with JUnit coverage.

**Non-Goals:** rendering a live fairy inside the jar; the fairy is part of the block texture, optionally animated with a `.mcmeta` file.

## Decisions

- **Absolute expiry time, not a countdown.** Store `expiresAtGameTime` so the jar ages the same in a chest, an inventory, or an unloaded chunk without ticking. "Days" are 24,000 game ticks.
- **One `FairyJarState` record** (`expiresAt`, `friendshipCount`, `lastCountedDay`, `countedToday`, `permanent`) with a codec, used as both the block entity's saved data and a custom data component on the item. Breaking and placing copies it across, so nothing is lost.
- **Light through a block state property** (`GLOW`: bright, dim, off) and the block's `lightLevel` function, because light can only depend on block state. A scheduled tick every 1,200 ticks (1 minute) updates `GLOW` and checks expiry; on chunk load, the block entity checks expiry once.
- **Inventory expiry via `Item.inventoryTick`**, which runs for carried items. Containers don't tick items, so stored jars convert on first inventory tick or placement after expiry, which players experience as "it expired while stored."
- **Capture in `BeanFairy.mobInteract`**, gated on a `finishedVisit` flag set by `VisitCropGoal` after growth.
- **Fly-away effect** spawns a short-lived Bean Fairy with a "leave" goal at the jar, reusing the existing flying setup.

## Risks / Trade-offs

- [Custom data components register differently on 1.21.11] → check `DataComponents` registration in both versions before coding, and add a divergence row.
- [Players dupe jars with modded storage] → the state lives on the item, so copies share an expiry and gain nothing.
- [A jar placed in a never-loaded chunk shows "full" in a stale screenshot] → acceptable; it converts on load.
