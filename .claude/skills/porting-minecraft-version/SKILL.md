---
name: porting-minecraft-version
description: Port Fertile Grounds to a new Minecraft version, or backport a feature from a newer branch to an older one. Use when asked to port, backport, update to a new Minecraft or Fabric version, or bring a feature to other branches.
---

# Porting between Minecraft versions

Move one version hop at a time (26.3 to 26.2, then 26.2 to 26.1, never 26.3 straight to 1.21.11). Finish and verify each branch before starting the next one, because each hop's fixes feed into the next.

## 1. Set up the branch

- **New version:** branch from `main` with the version as its name (`git checkout -b 26.4`).
- **Backport:** check out the older branch and bring the feature over from the newer one with `git cherry-pick <commits>`. If the pick conflicts, resolve it toward the older branch's existing code, and don't touch anything outside the feature.

Look up the pins, never from memory:

```bash
curl -s https://meta.fabricmc.net/v2/versions/game          # released versions
curl -s https://meta.fabricmc.net/v2/versions/loader/<mc>    # loader for that version
curl -s https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml  # API builds end in +<mc>
```

Only bump `loom_version` if the build demands it, since a newer Loom can require a newer Gradle. Update `gradle.properties` and the `minecraft`/`fabricloader` ranges in `src/main/resources/fabric.mod.json`.

## 2. Fix compile errors against real signatures

Run `./gradlew build` and `./gradlew genSources`. For each error, read the vanilla class in the sources jar (path in `AGENTS.md` under "Looking things up") and copy how vanilla itself does it, for example `Items.java` for item registration or `Allay.java` for a flying mob. Check Fabric API classes with `javap -cp <jar> <class>` on the module jar in `~/.gradle/caches/modules-2/files-2.1/net.fabricmc.fabric-api/`. When an API change removes the reason a workaround existed, delete the workaround instead of porting it.

## 3. Fix data and asset formats

These fail silently at build time. Compare every JSON type the mod ships against a vanilla file of the same kind in that version's jars:

```bash
unzip -p ~/.gradle/caches/fabric-loom/<mc>/minecraft-client.jar assets/minecraft/models/block/farmland.json
unzip -p <common jar> data/minecraft/advancement/recipes/building_blocks/coarse_dirt.json
```

## 4. Verify

Run the `AGENTS.md` verification steps on this branch. In `scripts/server_smoke_test.py`, use that version's game rule name and command syntax: 26.3 uses `random_tick_speed`, and older versions use the camelCase `randomTickSpeed` (check the branch's `GameRules.java`).

## 5. Finish

Update the branch's `README.md` (version line, requirements, branch table), then commit with `feat: port mod to Minecraft <mc>` or `feat: backport <feature> to <mc>`. Add anything new you learned to the table below in the same commit.

## Known differences between versions

Add a row each time a hop turns up a change. Only list changes that were checked against real code.

| Changed in | Area | Before | After |
|---|---|---|---|
| 26.3 | Hoe tilling | `TillableBlockRegistry.register` | `BlockTransformerHelper.registerTilling(BlockPredicate, Block)`; tilling is a data-driven block transformer |
| 26.3 | Bone meal | `isValidBonemealTarget(level, pos, state)` and siblings | Extra `BonemealSource` argument (`BonemealSource.INTERACTION`) |
| 26.3 | Farmland | Hardcoded revert to `Blocks.DIRT`, so the mod reimplemented moisture and trampling | `new FarmlandBlock(baseBlock, properties)` reverts to `baseBlock` |
| 26.3 | Block codecs | `codec()` override and `propertiesCodec()` | Removed |
| 26.3 | `Properties.isViewBlocking` | Took `Blocks::always` | Takes an `AABB` predicate; vanilla farmland no longer sets it |
| 26.3 | Farmland model | Parent `block/template_farmland` | Parent `block/template_cube_bottom_top_indented` with `bottom`/`side`/`top` textures |
| 26.3 | Recipe-unlock advancement | `"recipe": "<id>"` | `"recipes": "<id>"` |
| 26.3 | Entity renderer registration | Fabric `EntityRendererRegistry.register` | Deprecated; vanilla `EntityRenderers.register` is public |
| 26.3 | `BlockPos.withinManhattan` | `(origin, reachX, reachY, reachZ)` | `(origin, reach)`; per-axis forms are `withinClippedManhattan` and `withinBoxByManhattanDistance` |
| 26.2 | `FlyingMoveControl` | Not generic: `new FlyingMoveControl(mob, ...)` | Generic: `new FlyingMoveControl<>(mob, ...)` |
