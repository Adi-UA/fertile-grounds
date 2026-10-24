# Proposal

## Why

1.21.1 players got 2.0.0 on 2026-10-23, but 1.21.11 players still get 1.2.0. 1.21.11 is the last 1.21 release, so packs that skipped the 26.x jump sit on it. The `1.21.11` README already says 2.0.0 is coming.

## What Changes

- The `1.21.11` branch moves from 1.2.0 (Fabric only, one Gradle project) to the 2.0.0 multiloader layout, built from the `26.1` tree in one hop, the next older version as the porting skill asks.
- Every 2.0.0 feature works on Minecraft 1.21.11 with Fabric, NeoForge 21.11, and Forge 61, except Jade on Forge (Jade has no Forge 1.21.11 build), which matches 26.x and 1.21.1.
- The build moves back to an obfuscated setup like 1.21.1: Loom with Mojang mappings for Fabric, NeoForm 1.21.11 for `common`, Java 21.
- The GameTest harness stays on 26.1's data-driven test instances and environments, which 1.21.11 already has.
- Where 1.21.11 lacks a 26.1 feature, the port reuses the 1.21.1 branch's answer (Wandering Trader offers in code, crop mixins instead of crop tags) when it fits 1.21.11's code.
- New differences go into the porting skill's divergence table, under hop "1.21.11".

Out of scope: 1.21.10 (next), publishing before the gate (every GameTest, `client_log_check.py` on each loader, and the maintainer's playtest), new features, and `add-translations`.

New art: none.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `release-2-0`: the supported versions add Minecraft 1.21.11 on all three loaders.

## Impact

- Branch `1.21.11`: the whole tree is replaced by the ported 2.0.0 code; the 1.2.0 history stays in git, and the published 1.2.0 files stay on the stores.
- Dependencies, all pinned exactly (looked up 2026-10-23): Fabric Loader 0.20.1, Fabric API 0.141.6+1.21.11, NeoForm 1.21.11-20251209.172050, NeoForge 21.11.48, Forge 61.2.4 (recommended), Jade 21.1.6+fabric and 21.1.7+neoforge, Java 21.
- `.claude/skills/porting-minecraft-version/SKILL.md` gets new rows. After the gate, the README, `docs/DETAILS.md`, `docs/ROADMAP.md`, `openspec/config.yaml`, and the release skills list 1.21.11.
