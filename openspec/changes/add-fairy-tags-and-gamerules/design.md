# Design

## Context

`CropGrowthUtil.canGrow` checks `BlockTags.CROPS`. `FairyTiming.NATURAL_VISIT_CHANCE` and the boost chances in `ModBlockProperties` are constants. Gamerule registration differs across our targets: 1.21.11 renamed vanilla rules to snake_case, and the registration API may differ again by 26.3.

## Goals / Non-Goals

**Goals:** one place that defines each tag key and each gamerule, so every later change reuses them.

**Non-Goals:** a config file. Gamerules are per world and editable in game, which suits servers and modpacks, so a second config system would duplicate them.

## Decisions

- **Tags as `TagKey` constants in a new `ModTags` class**, matching the `Mod*` registration pattern in `AGENTS.md`. Default tag JSON references the vanilla tag (`"#minecraft:crops"`) so new vanilla crops join automatically.
- **Gamerules in a new `ModGameRules` class**, using Fabric's gamerule API (`GameRuleRegistry` with `GameRuleFactory.createIntRule(default, min, max)` on older versions). Read the 26.3 and 1.21.11 Fabric API jars with `javap` before writing code, and record any difference in the porting divergence table.
- **Chances stay pure math in `FairyTiming`**, which takes the gamerule value as a parameter, so JUnit tests the math without a world.
- **Percent integers over floats**, because gamerules only store integers and booleans.

## Risks / Trade-offs

- [Gamerule API differs between 1.21.11 and 26.3] → check both jars first; the backport phase adds a divergence row.
- [A pack empties `fairy_crops`] → fairies never visit; this is the pack's choice, and no error is raised.
