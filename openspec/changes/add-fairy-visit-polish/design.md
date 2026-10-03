# Design

## Context

`VisitCropGoal` runs three phases (fly to crop, hover 60 ticks, leave 40 ticks) under a 400 tick hard cap. `BeanFairy` turns on `noPhysics` every tick so it can pass through roofs, which also lets the fly-in overshoot into the ground. The fairy has no ambient sound. `FairyVisits` stores each pending visit's arrival tick, which herald timing can read.

## Goals / Non-Goals

**Goals:** pure, JUnit-tested helpers for ring order (`GrowthRings`) and flee targets (`ShyFlight`); one phase enum that later changes read (catching needs "finished growing").

**Non-Goals:** client-side animation code. The spin uses the entity's body rotation, which vanilla syncs.

## Decisions

- **Phases:** `FLY_TO_CROP` (gives up after 20 seconds), `DANCE` (60 ticks, spinning `yBodyRot`), `GROW_RINGS` (one ring every 10 ticks), `LINGER` (gamerule seconds), `LEAVE` (40 ticks). Each phase owns its own limit, replacing the single 400 tick cap. `BeanFairy.hasFinishedGrowing()` returns true from `LINGER` on.
- **Rings by Chebyshev distance** (`max(|dx|, |dz|)`): ring 0 is the center, ring 2 the edge of the 5x5. `CropGrowthUtil.growRing(level, center, ring)` grows one ring; `GrowthRings` lists offsets per ring for JUnit.
- **Linger drift:** every 40 ticks pick a random growing or grown crop in the patch and fly to 1.2 blocks above it at half speed.
- **Shy flight:** each tick in `FLY_TO_CROP` and `LINGER`, find the nearest non-sneaking, non-spectator player within 4 blocks. `ShyFlight.target(fairy, player, crop)` returns the point 6 blocks from the player along the line away from them, clamped to 8 blocks from the crop. Skipped during `DANCE` and `GROW_RINGS`.
- **Dive fix:** first reproduce in `runClient` and record the cause in this file. Planned fix: spawn at `max(crop.y + 6, MOTION_BLOCKING heightmap + 2)`, and when `level.canSeeSky(crop.above())`, clamp the fairy's Y to at least `crop.y + 1` each tick and zero any downward motion. Covered crops keep today's pass-through.
- **Heralds** run in `FairyVisitScheduler`'s tick: every 10 ticks, for each pending visit due within 1,200 ticks with a player within 32 blocks, send a few `FIREFLY` particles over the patch and, every 100 ticks, the firefly bush ambient sound. Both exist in 1.21.11 and 26.3; read the exact `SoundEvents` field name in each.
- **Gamerule** `fairy_linger_seconds` (default 60, range 0 to 600) in `ModGameRules`.

## Look and sound

| Moment | Look | Sound | Particle |
|---|---|---|---|
| Heralds | Motes drift over the patch | Firefly bush ambient, quiet | `firefly` |
| Dance | Fairy spins above the crop | None | `glow`, a few |
| Each ring grows | Ring of crops pops to full growth | `block.amethyst_block.chime`, pitch +0.2 per ring | `happy_villager` per crop |
| Linger | Slow drift between crops | `entity.allay.ambient_without_item`, pitch 1.4, every 4 to 8 seconds | `glow` trail, sparse |
| Shy dart | Quick dash away | `entity.allay.item_thrown`, soft | `end_rod`, 3 |
| Leave | Rises and fades | None | `end_rod`, as today |

## Risks / Trade-offs

- [Longer visits mean more fairy entities alive at once on busy servers] → each lingers 60 seconds and is never saved; a server with 20 visits a night carries at most a few at a time.
- [Shy flight pushes the fairy into a wall] → it flies with `noPhysics`, so walls don't trap it; the vertical clamp still applies over open crops.
- [Dive cause differs from the guess] → task 1.1 confirms it before any fix is written.
