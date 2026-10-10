# Design

## Context

Soil blocks live in `block/` and soil changes go through `SoilUpgrades` (pure) and `FertilizerItem.upgrade`, which the dispenser behavior reuses. Fairy visits grow crops through `CropGrowthUtil.growRing` (Fairy Dust visits) and `growArea` (natural visits). Spreading blocks in vanilla (grass, mycelium) use `randomTick`, which this follows.

## Goals / Non-Goals

**Goals:** the spread odds and tier resistance in a pure `BlightRules` class with JUnit coverage; one block, no block entity.

**Non-Goals:** a separate blighted farmland or sand block; blight that spreads through air gaps or diagonals.

## Decisions

- **One `BlightedDirtBlock extends Block`** with `isRandomlyTicking` true. Each random tick picks one of the 6 neighbors with `Direction.getRandom`, the way vanilla's spreading blocks test one spot per tick, so cost stays at one block read per tick. A target only falls if the block on top of it has no sturdy bottom face, so blight creeps across the surface instead of through every buried dirt block. At the default random tick speed of 3 a block gets a random tick about every 68 seconds, so a 20% chance moves a blight front about one block every 6 minutes, and a 9x9 farm falls in roughly half an in-game day if nobody acts.
- **Odds:** `BlightRules.spreadChance(basePercent, tier)` returns the base, half, or a quarter for no tier, `SoilTier.ENRICHED`, and `SoilTier.SUPER_ENRICHED`. The target's tier comes from the existing `GrowthBoostedFarmlandBlock`, `GrowthBoostedSandBlock`, and `EnrichedDirtBlock` getters.
- **Tag** `fertilegrounds:blightable` decides targets, so packs extend it without code. Blighted Dirt is not in `minecraft:dirt`, so saplings, flowers, and grass refuse it, and vanilla's grass spread (which targets plain dirt only) never covers it. It joins `minecraft:supports_dry_vegetation` so dead bushes survive on it, and `minecraft:mineable/shovel`.
- **Withering:** before swapping the block, if the block above is in `minecraft:crops`, `minecraft:flowers`, `fertilegrounds:fairy_crops`, or is any `VegetationBlock`, set it to `Blocks.DEAD_BUSH` with flag 2 and no drops, then set the soil. Swapping the soil first would pop the plant and drop its items. Double-tall plants lose their top half through their own `updateShape`.
- **Witch curse:** a `ServerLivingEntityEvents.AFTER_DEATH` listener checks `entity.getType() == EntityType.WITCH` and `source.getEntity() instanceof Player`, rolls `witch_blight_chance`, then converts the tagged blocks in the 3x3 at `blockPosition().below()`. The message is a translatable chat component sent to players with `distanceToSqr < 32 * 32`.
- **Recipe:** shapeless JSON with plain item ingredients, plus an unlock advancement on `minecraft:fermented_spider_eye`. No component ingredients needed.
- **Fertilizer cure:** `SoilUpgrades` maps upgrades only, so `FertilizerItem.useOn` and `upgrade` first check for Blighted Dirt and set `Blocks.DIRT`, keeping one code path for hand and dispenser.
- **Fairy cure:** a `CropGrowthUtil.cureBlight(level, center, radius)` helper turns Blighted Dirt at the crop's level and one below into dirt. `VisitCropGoal` calls it once after the last ring and `FairyVisitScheduler` calls it next to `growArea`, so both visit kinds cure.
- **Gamerules:** `blight_spread_chance` (default 20, 0 to 100, UPDATES) and `witch_blight_chance` (default 1, 0 to 100, DROPS).
- **No version divergence:** `AFTER_DEATH`, `randomTick`, and the tags exist unchanged on 26.1 to 26.3.

## Look and sound

| Moment | Look | Sound | Particle |
|---|---|---|---|
| Placing | Ashen, grey-purple dirt | Rooted dirt place | None |
| Ambient | Specks drifting up, like mycelium | None | `ash`, 1 now and then, client side |
| Spreading | The touched block turns ashen | Sculk block spread, quiet | `ash`, 6 |
| Withering | Crop turns into a dead bush | Crop break | `smoke`, 4 |
| Witch curse | 3x3 patch turns ashen, chat message | Sculk block spread | `witch`, 20 |
| Cure | Block returns to dirt | Composter fill success | `happy_villager`, 8 |
| Breaking | Normal | Rooted dirt break | Block crumbs |

## Risks / Trade-offs

- [Griefing on servers] → `blight_spread_chance 0` turns spreading off, and any non-tag block stops it, so a one-block path guards a farm.
- [Blight in unwatched loaded chunks eats a large area] → it only enters tagged soil, never stone or wood, and cures stay cheap; document this in the guide.
- [Mods whose crops aren't `VegetationBlock`s or in the tags] → those pop with their drops instead of withering; acceptable.
