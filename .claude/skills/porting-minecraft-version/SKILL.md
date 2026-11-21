---
name: porting-minecraft-version
description: Port Fertile Grounds to a new Minecraft version, or backport a feature from a newer branch to an older one, for all three loaders (Fabric, NeoForge, Forge). Use when asked to port, backport, update to a new Minecraft, Fabric, NeoForge, or Forge version, or bring a feature to other branches.
---

# Porting between Minecraft versions

Move one version hop at a time (26.3 to 26.2, then 26.2 to 26.1, never 26.3 straight to 26.1), and port all three loaders in the same hop. Since 2.0 the targets are `26.2` and `26.1`. `1.21.1` got 2.0.0 straight from 26.1 (its rows are "1.21.2 to 26.1"), `1.21.11` got it from 26.1 in one hop (rows "26.1" give the 1.21.11 form, and rows "1.21.2 to 1.21.11" changed by then), `1.21.10` got it from `1.21.11` (rows "1.21.11" give what that hop undid), `1.20.1` got 2.0.1 from `1.21.1` (rows "1.20.2 to 1.21.1"), and versions before 26.1 get 2.0.x only. Finish and verify each branch before starting the next one, because each hop's fixes feed into the next.

## 1. Set up the branch

- **New version:** branch from `main` with the version as its name (`git checkout -b 26.4`).
- **Backport:** check out the older branch and bring the feature over from the newer one with `git cherry-pick <commits>`. If the pick conflicts, resolve it toward the older branch's existing code, and don't touch anything outside the feature.

Look up the pins, never from memory:

```bash
curl -s https://meta.fabricmc.net/v2/versions/game          # released versions
curl -s https://meta.fabricmc.net/v2/versions/loader/<mc>    # loader for that version
curl -s https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml  # API builds end in +<mc>
curl -s https://maven.neoforged.net/releases/net/neoforged/neoform/maven-metadata.xml      # NeoForm for common/
curl -s https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml     # NeoForge builds start with <mc>
curl -s https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json     # Forge latest and recommended
unzip -p ~/.gradle/caches/fabric-loom/<mc>/minecraft-client.jar version.json  # pack_version.data_major for Forge
```

Only bump `loom_version`, `moddevgradle_version`, or `forgegradle_version` if the build demands it, since a newer plugin can require a newer Gradle. Update every pin in `gradle.properties` (`minecraft_version_range`, `neo_form_version`, `neoforge_version`, `jade_neoforge_version`, `forge_version`, `data_pack_format`) and the `minecraft`/`fabricloader` ranges in `fabric/src/main/resources/fabric.mod.json`; `LoaderMetadataTest` checks they agree.

## 2. Fix compile errors against real signatures

Run `./gradlew build` and `./gradlew :fabric:genSources`. Fix `common` first (it compiles against plain Minecraft), then each loader. For each error, read the vanilla class in the sources jar (path in `AGENTS.md` under "Looking things up") and copy how vanilla itself does it, for example `Items.java` for item registration or `Allay.java` for a flying mob. Check Fabric API classes with `javap -cp <jar> <class>` on the module jar in `~/.gradle/caches/modules-2/files-2.1/net.fabricmc.fabric-api/`, and NeoForge and Forge classes in their sources jars (paths in `AGENTS.md`). When an API change removes the reason a workaround existed, delete the workaround instead of porting it.

## 3. Fix data and asset formats

These fail silently at build time. Compare every JSON type the mod ships against a vanilla file of the same kind in that version's jars:

```bash
unzip -p ~/.gradle/caches/fabric-loom/<mc>/minecraft-client.jar assets/minecraft/models/block/farmland.json
unzip -p <common jar> data/minecraft/advancement/recipes/building_blocks/coarse_dirt.json
```

## 4. Verify

Run the `AGENTS.md` verification steps on this branch for every loader: `python3 scripts/run_gametests.py --loader all` and `client_log_check.py --loader <each>`. The GameTests use 26.3's `GameTestHelper`, data-driven test environments (`common/src/gametest/resources/data/fertilegrounds/test_environment/`), test-instance data that `TestCatalog` writes, empty `.nbt` structures (their `DataVersion` is 26.3's), and snake_case game rule names (`random_tick_speed`); check each against the branch's `GameTestHelper`, test environment registry, and `GameRules` class (before 1.21.11, rules are camelCase like `randomTickSpeed`). `run_gametests.py`'s `ERROR_MARKERS` were copied from the 26.3 sources; confirm each string still appears in that branch's sources. The client log check's `LOADED_MARKER` is built at runtime, so it only shows up in a real log: a passing client check confirms it. Add any difference to `differences.md`.

## 5. Finish

Update the branch's `README.md` (version line, requirements, branch table), then commit with `feat: port mod to Minecraft <mc>` or `feat: backport <feature> to <mc>`. Add anything new you learned to `differences.md` in the same commit.

## Known differences between versions

`differences.md`, next to this file, lists every API and data-format change found between supported versions and the loader facts checked on 26.3. Read it before a hop, and add a row each time a hop turns up a new change.
