# Design

## Context

`FairyVisit` stores only `pos` and `arrivalTick`. Gifts need the visit's source and owner, and the cooldown needs per-player memory that works while the player is offline.

## Goals / Non-Goals

**Goals:** gift rules (chance, cooldown check, tier) as a pure `FairyGifts` class with JUnit coverage; Minecraft code only gathers inputs and drops items.

**Non-Goals:** a custom gift block or container; vanilla item entities keep the look vanilla.

## Decisions

- **Extend `FairyVisit` with `source` (NATURAL, DUST, UNOWNED) and `Optional<UUID> owner`** using `optionalFieldOf` in the codec, so 1.2.0 saves decode as UNOWNED with no owner.
- **Cooldowns in a level attachment** (`Map<UUID, Long>` of last gift day), like `FairyVisits`. A level attachment works for offline players, which a player attachment can't.
- **Loot tables over hardcoded items**, so packs can retheme gifts. Default contents, counts, and weights live in the spec's "Default gift tables" requirement; the flower entry uses a `minecraft:tag` entry with `expand: true` on the `fairy_flowers` item tag.
- **Never-despawn items** through `ItemEntity.setUnlimitedLifetime()`, which vanilla uses for items that must persist.
- **Crop count reuses the Harvest Moon scan approach**: one bounded box scan per gift roll, only after the chance roll succeeds, so most visits skip the scan.

## Look and sound

| Moment | Look | Sound | Particle |
|---|---|---|---|
| Gift drops | Item appears at the crop, never despawns | `block.amethyst_cluster.place`, pitch 1.3 | `glow`, 6, then `end_rod` |
| Owner message | Overlay message | None | None |

## Risks / Trade-offs

- [Unlimited-lifetime items pile up on abandoned farms] → the 3-day cooldown caps it at about one stack of items per player per 3 days.
- [Rare items flood markets on servers] → server owners lower the chance gamerules or override the rare table.
