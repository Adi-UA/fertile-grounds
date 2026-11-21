# Proposal

## Why

A CurseForge player asked for a 1.20.1 port on 2026-11-18, and 1.20.1 is still a common Forge modpack version. The `1.20.1` branch holds 1.2.0 for Fabric only, so 1.20.1 has never had a Forge jar. Downloads for 1.20.1 total 186 (93 on Modrinth, 93 on CurseForge, read 2026-11-20), all from Fabric. This change is a trial port: if every check passes, 2.0 ships for 1.20.1, and if the checks fail, the branch stays on 1.2.0.

## What Changes

- The `1.20.1` branch moves from 1.2.0 (one Fabric project) to the 2.0 multiloader layout, built from the `1.21.1` tree at 2.0.1.
- Every 2.0 feature works on Minecraft 1.20.1 with Fabric and Forge 47, except where 1.20.1 lacks something; the design names each gap. Jade tooltips work on both loaders, since Jade ships Forge and Fabric builds for 1.20.1, but only with the lines the client already knows: Jade 11 sends server data only for block entities.
- The build moves to a fully obfuscated setup: 1.20.1 Forge runs with Forge's own names, so the Forge jar needs a renaming step and mixin refmaps, which no 1.21.1 project needs.
- Item data stored as item components on 1.21.1 moves to item NBT tags, since components arrived in 1.20.5.
- The 1.21.1 GameTest harness carries over to 1.20.1's test API, and every existing test ports.
- New differences go into the porting skill's divergence table, under the hop "1.20.1 (from 1.21.1)".
- **BREAKING (policy):** the version policy says 1.20.1 stays on 1.2.0. This change revisits that rule. Only after the success gate (every GameTest, `client_log_check.py` on each loader, and the maintainer's playtest) do the docs and store listings say 2.0 runs on 1.20.1.

Out of scope: NeoForge on 1.20.1 (NeoForge 47.1 rejects the mod's registration; the design explains), features newer than 2.0.1, `add-translations`, and publishing anything before the gate passes.

New art: none. The 1.21.1 textures ship unchanged; only model and data JSON formats change.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `release-2-0`: the supported versions add Minecraft 1.20.1 for Fabric and Forge, the 1.2.0 file for 1.20.1 stays listed, and the 1.20.1 branch README says it carries 2.0 with no later features.

## Impact

- Branch `1.20.1`: the whole tree gets replaced by the ported 2.0.1 code. The 1.2.0 history stays in git, and the published 1.2.0 files stay on both stores.
- Dependencies, all pinned exactly (looked up 2026-11-20): Fabric Loader 0.20.1, Fabric API 0.92.12+1.20.1, Forge 47.4.28 (`1.20.1-recommended`), NeoForge 47.1.106 for the `-PneoforgeCompat` check, Jade 11.13.3 for Forge and Fabric, and Java 17. The build plugins get chosen in task 1.
- `.claude/skills/porting-minecraft-version/SKILL.md` gets new table rows. After the gate, the README, `docs/DETAILS.md`, `docs/ROADMAP.md`, `openspec/config.yaml`, and the release skills list 1.20.1.
