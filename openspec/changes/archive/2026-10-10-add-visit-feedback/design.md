# Design

## Context

Visits grow crops in two places: `VisitCropGoal` (a fairy flew) and `FairyVisitScheduler.growVisitsMissedOvernight` (dawn). Both need the same feedback hook.

## Goals / Non-Goals

**Goals:** one `FairyVisitEvents.onVisitGrown(level, pos, overnight)` hook that both paths call, so feedback, the trigger, and later gifts share one entry point.

**Non-Goals:** client-side code; sounds, subtitles, toasts, and messages all ride on vanilla packets.

## Decisions

- **Custom trigger extends `SimpleCriterionTrigger`** and registers in the vanilla `CRITERION_TRIGGER` registry, the same way vanilla triggers do. Check the 26.3 and 1.21.11 constructor shapes before writing it.
- **Fertilizer advancement uses vanilla `item_used_on_block`** with the Fertilizer item, so no custom trigger is needed. Fire it only on a successful upgrade by checking the block after use (`location` predicate on Super Enriched blocks or Enriched Sand).
- **One dawn message per player** by collecting players across all dawn visits into a set before messaging.
- **Sound**: register `fertilegrounds:entity.bean_fairy.arrive` reusing vanilla amethyst chime sound files via `sounds.json`, so no new audio files are needed.

## Look and sound

| Moment | Look | Sound | Particle |
|---|---|---|---|
| Fairy arrives | Overlay message for players within 32 blocks | `fertilegrounds:entity.bean_fairy.arrive` (amethyst chime files) with subtitle | `end_rod`, 5 at the fairy |
| Dawn after an overnight visit | Chat message | None, players are just waking | `happy_villager` on grown crops, as today |
| Advancement earned | Vanilla toast | Vanilla toast sound | None |

## Risks / Trade-offs

- [Item-used trigger fires on failed use] → the location predicate checks the result block, so failed uses don't count.
