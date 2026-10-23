# Proposal

## Why

Downloads jumped after 2.0.0 (Modrinth: 600 in the 30 days to 2026-10-22, up 249%), but players on Minecraft 1.21.1 still get 1.2.0. Many modpacks still run on 1.21.1. This change is a trial port: if every check passes on all three loaders, 2.0.0 ships for 1.21.1 too. If they don't pass, the branch stays on 1.2.0.

## What Changes

- The `1.21.1` branch moves from 1.2.0 (Fabric only, one Gradle project) to the 2.0.0 multiloader layout, built from the `26.1` tree. It skips the 1.21.11 and 1.21.10 hops, since only 1.21.1 is wanted.
- Every 2.0.0 feature works on Minecraft 1.21.1 with Fabric, NeoForge 21.1, and Forge 52, except Jade on Forge (Jade has no Forge 1.21.1 build), which matches 26.x.
- The build moves back to an obfuscated setup: Loom with Mojang mappings for Fabric, and NeoForm 1.21.1 for `common`. NeoForge and Forge already run with Mojang names on 1.21.1, so they need no renaming step.
- The GameTest harness is rebuilt on 1.21.1's older test API, where tests are generated functions and batches instead of test functions, test-instance files, and test environments. All existing tests port over.
- New differences found during the port go into the porting skill's divergence table.
- Only after the success gate (every GameTest, `client_log_check.py` on each loader, and the maintainer's playtest) does the listing text change to say 2.0 runs on 1.21.1 and that older versions get updates later than 26.x.

Out of scope: 1.21.11, 1.21.10, and 1.20.1 (they stay on 1.2.0), publishing anything before the gate passes, new features, and bringing `add-translations` to 1.21.1.

New art: none. The 26.1 textures ship unchanged; only model and item JSON formats change.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `release-2-0`: the supported versions add Minecraft 1.21.1 on all three loaders, and the listing text says older versions get updates more slowly.

## Impact

- Branch `1.21.1`: the whole tree is replaced by the ported 2.0.0 code. The 1.2.0 history stays in git, and the published 1.2.0 files stay on Modrinth and CurseForge.
- Dependencies, all pinned exactly: Fabric Loader 0.19.5, Fabric API 0.116.17+1.21.1, NeoForm 1.21.1-20240808.144430, NeoForge 21.1.264, Forge 52.1.20 (recommended), Jade 15.10.6 for Fabric and NeoForge, and Java 21.
- `.claude/skills/porting-minecraft-version/SKILL.md` gets new table rows. Once the gate passes, the README, `docs/DETAILS.md`, `docs/ROADMAP.md`, `openspec/config.yaml`, and the release skills list 1.21.1.
