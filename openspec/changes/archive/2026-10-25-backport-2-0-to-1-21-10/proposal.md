# Proposal

## Why

1.21.1 and 1.21.11 players have 2.0, but 1.21.10 players still get 1.2.0. 1.21.10 is the last 1.21 version without 2.0, and its README already says 2.0.0 is coming.

## What Changes

- The `1.21.10` branch moves from 1.2.0 (Fabric only, one Gradle project) to the 2.0.0 multiloader layout, built from the `1.21.11` tree in one hop, the next newer version as the porting skill asks.
- Every 2.0.0 feature works on Minecraft 1.21.10 with Fabric, NeoForge 21.10, and Forge 60, except Jade on Forge (Jade has no Forge 1.21.10 build), which matches every other 2.0 branch.
- The build stays obfuscated like 1.21.11: Loom with Mojang mappings for Fabric, NeoForm 1.21.10 for `common`, Java 21.
- Where 1.21.10 lacks a 1.21.11 feature, the port reuses the 1.21.1 branch's answer when it fits 1.21.10's code (for example `GameRules.Key` gamerules through `Platform.intRule` and `boolRule`).
- New differences go into the porting skill's divergence table, under hop "1.21.10".

Out of scope: publishing before the gate (every GameTest, `client_log_check.py` on each loader, and the maintainer's playtest or their call to skip it), new features, and `add-translations`.

New art: none.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `release-2-0`: the supported versions add Minecraft 1.21.10 on all three loaders, and the 1.21.10 README says it carries 2.0.0 only.

## Impact

- Branch `1.21.10`: the whole tree is replaced by the ported 2.0.0 code; the 1.2.0 history stays in git, and the published 1.2.0 files stay on the stores.
- Dependencies, all pinned exactly (looked up 2026-10-25): Fabric Loader 0.20.1, Fabric API 0.138.4+1.21.10, NeoForm 1.21.10-20251010.172816, NeoForge 21.10.64, Forge 60.1.18 (recommended), Jade 20.1.0+fabric and 20.0.5+neoforge, Sodium mc1.21.10-0.7.3-fabric, Iris 1.9.7+1.21.10-fabric, `data_pack_format` 88, Java 21.
- `.claude/skills/porting-minecraft-version/SKILL.md` gets new rows. After the gate, the README, `docs/DETAILS.md`, `docs/ROADMAP.md`, `openspec/config.yaml`, `SECURITY.md`, and the release skills list 1.21.10.
