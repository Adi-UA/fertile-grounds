# Proposal

## Why

Fertile Grounds 2.0.0 only runs on Fabric, so NeoForge and Forge players can't install it. We counted 26.x downloads across 27 popular cross-loader mods (Jade, JEI, AppleSkin, Sodium and others) on 2026-10-10. On CurseForge, NeoForge jars took 51% of those downloads and Forge jars took 9%, so a Fabric-only mod misses 60% of that site's audience. Minecraft 26.x ships without obfuscation, so all three loaders compile against the same Mojang names. That lets one shared codebase build all three jars, with a thin layer for each loader.

## What Changes

- Split each version branch into four Gradle projects: `common` (all gameplay code, data, and assets), `fabric`, `neoforge`, and `forge`. These follow the MultiLoader-Template layout and use each loader's own build plugin: Fabric Loom, NeoForge's ModDevGradle, and ForgeGradle 7.
- Move the few Fabric API calls behind small loader hooks in `common`. These cover registration, tick and join events, saved world and player data, tilling, creative tab, entity attributes, model layers, and the Jade plugin entry. Fabric keeps calling the same Fabric API it calls today.
- Ship a NeoForge jar and a Forge jar for Minecraft 26.1, 26.2, and 26.3 as version `2.0.0`. They are named `fertilegrounds-neoforge-2.0.0+<mc>.jar` and `fertilegrounds-forge-2.0.0+<mc>.jar` and get added to the existing v2.0.0 GitHub release, Modrinth, and CurseForge. Neither jar has a required dependency.
- Jade tooltips work on Fabric and NeoForge. Jade has published no Forge build since Minecraft 1.20.4, so the Forge jar loads without Jade and shows no tooltips.
- Run the same GameTests (100, of which Forge skips the 3 Jade tests) on all three loaders from one shared test source, with each loader using its own test runner. `run_gametests.py` and `client_log_check.py` take a `--loader` flag.
- CI already runs the Fabric GameTests through `./gradlew build`. It now also runs the data-load error scan and the NeoForge and Forge GameTests on every push to every version branch, and it keeps the test report and log when a run fails.
- The Fabric jar keeps its name, its behavior, and its Fabric API dependency. The restructure must not change what it contains, apart from the class and file moves this change makes.
- Update the README, `docs/DETAILS.md`, `docs/ROADMAP.md`, AGENTS.md, the porting skill, and both store skills to cover three loaders.

Out of scope: new gameplay, balance changes, Minecraft versions before 26.1, and rebuilding or re-uploading the published Fabric 2.0.0 jars.

New art: none. All loaders read the same textures from `common`.

## Capabilities

### New Capabilities
- `forge-loaders`: Fertile Grounds on NeoForge and Forge. It has the same blocks, items, fairy behavior, gamerules, tags, and advancements as on Fabric, plus each loader's own jar metadata and naming and Jade tooltips where Jade exists.

### Modified Capabilities
- `in-world-tests`: the one-command check runs on any loader, and CI runs it for every loader on every push.
- `verification-suite`: the silent-load-failure scan and the client log check cover every loader, and the loader metadata files must agree.
- `release-2-0`: 2.0.0 ships Fabric, NeoForge, and Forge jars for 26.1, 26.2, and 26.3, and the docs name all three loaders.

## Impact

- **Build:** `settings.gradle`, a root `build.gradle`, `buildSrc` convention scripts, and `common/`, `fabric/`, `neoforge/`, `forge/` subprojects on all three version branches. Jars move from `build/libs/` to each loader's `build/libs/`.
- **Code:** `src/main`, `src/client`, and `src/gametest` move under `common/`. The `Mod*` classes keep registering through `Registry.register`, and each loader runs them inside its own registry events. About 13 files touch Fabric API today, and each gets a NeoForge and a Forge counterpart.
- **Tests:** the GameTest classes swap Fabric's `@GameTest` for a loader-neutral annotation. `AssetConsistencyTest` also checks `neoforge.mods.toml` and `mods.toml`.
- **Dependencies, build-time only:**
  - NeoForge 26.1.2.x and 26.2.0.x, plus 26.3.0.x, which is still beta.
  - Forge 64.1.x (26.1.2), 65.1.x (26.2), and 66.0.x (26.3), which has no recommended build yet.
  - ModDevGradle 2.0.x and ForgeGradle 7.0.x.

  Players install nothing extra.
- **CI:** `.github/workflows/build.yml` runs a job per loader.
- **Docs and skills:** the files listed under What Changes.
