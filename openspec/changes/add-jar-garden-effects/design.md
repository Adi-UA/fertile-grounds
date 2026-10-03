# Design

## Context

Creepers and farmland need to ask "is a filled jar near this spot?" often, so a block scan per check is too slow. Vanilla's point-of-interest (POI) system answers that question for villager workstations with an indexed lookup.

## Goals / Non-Goals

**Goals:** one `FairyJarLookup.nearestFilledJar(level, pos, radius)` that every effect uses.

**Non-Goals:** changing how creepers explode once they've started swelling.

## Decisions

- **Register a POI type for filled jars** with Fabric's `PointOfInterestHelper`, so lookups use vanilla's chunk-indexed POI storage. Check the helper's signature in the 26.3 and 1.21.11 Fabric API jars.
- **Cluster leader = the filled jar with the smallest `BlockPos.asLong()`** within 8 blocks. Every jar computes the same answer, so exactly one boosts, and no shared state is needed.
- **Creeper avoidance by mixin** into `Creeper.registerGoals`, adding a goal modeled on vanilla's `AvoidEntityGoal` for cats but targeting the POI position. Fabric API has no hook for adding mob goals, so this is the mod's only mixin; keep it small.
- **Trample protection by mixin** into `FarmlandBlock.fallOn` (or the 26.3 method that converts farmland), cancelling only the conversion. On 26.3 our farmland is a vanilla `FarmlandBlock`, so one mixin covers vanilla, Enriched, Super Enriched, and modded farmland that extends it.
- **Bee check with an entity AABB query** of `Bee` with `hasNectar()`, run only inside the jar's random tick.

## Risks / Trade-offs

- [Mixins break across versions] → both mixins target small, stable methods; the backport phase verifies each and records signatures in the divergence table.
- [Modded farmland that doesn't extend `FarmlandBlock` isn't protected] → acceptable; document it.
