# Design

## Context

Soil blocks live in `ModBlocks`. On 26.3, `FarmlandBlock` takes its base block, so our farmland is vanilla farmland with a different revert target. Moisture is the vanilla `MOISTURE` property on every farmland.

## Goals / Non-Goals

**Goals:** a pure lookup from "block I clicked" to "block it becomes", testable without Minecraft.

**Non-Goals:** upgrading modded soils (Farmer's Delight Rich Soil keeps its own behavior).

## Decisions

- **`SoilUpgrades` maps each source block to its upgraded block** (`dirt → super_enriched_dirt`, `farmland → super_enriched_farmland`, and so on). `FertilizerItem.useOn` and the dispenser behavior both call it, so player and dispenser rules can't drift apart.
- **Copy matching state properties** from the old state to the new one (`MOISTURE` for farmland), instead of special-casing farmland, so any shared property carries over.
- **Dispenser behavior through `DispenserBlock.registerBehavior`** with an `OptionalDispenseItemBehavior`, the same pattern vanilla uses for Bone Meal.

## Look and sound

| Moment | Look | Sound | Particle |
|---|---|---|---|
| Upgrading soil by hand | Block swaps to Super Enriched or Enriched Sand, crop stays | `block.composter.fill_success` | `happy_villager`, 8 |
| Upgrading by dispenser | Same as by hand | Dispenser click, then `block.composter.fill_success` | `happy_villager`, 8 |
| Using on a block it can't upgrade | Nothing changes | None | None |

## Risks / Trade-offs

- [A 9-ingredient recipe is expensive per block] → each craft makes 4, so one upgraded block costs about 2.25 items, close to crafting Super Enriched Dirt (3 items) while saving the rebuild.
- [Players on 1.21.11 might expect tilling behavior to differ] → 1.21.11 used `TillableBlockRegistry` (see the divergence table); Fertilizer doesn't till, so it's unaffected.