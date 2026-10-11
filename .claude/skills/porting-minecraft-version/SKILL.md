---
name: porting-minecraft-version
description: Port Fertile Grounds to a new Minecraft version, or backport a feature from a newer branch to an older one, for all three loaders (Fabric, NeoForge, Forge). Use when asked to port, backport, update to a new Minecraft, Fabric, NeoForge, or Forge version, or bring a feature to other branches.
---

# Porting between Minecraft versions

Move one version hop at a time (26.3 to 26.2, then 26.2 to 26.1, never 26.3 straight to 26.1), and port all three loaders in the same hop. Since 2.0 the targets are `26.2` and `26.1`; the 1.21.11 and older branches are frozen on 1.2.0, and their rows below stay for history. Finish and verify each branch before starting the next one, because each hop's fixes feed into the next.

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

Run the `AGENTS.md` verification steps on this branch for every loader: `python3 scripts/run_gametests.py --loader all` and `client_log_check.py --loader <each>`. The GameTests use 26.3's `GameTestHelper`, data-driven test environments (`common/src/gametest/resources/data/fertilegrounds/test_environment/`), test-instance data that `TestCatalog` writes, empty `.nbt` structures (their `DataVersion` is 26.3's), and snake_case game rule names (`random_tick_speed`); check each against the branch's `GameTestHelper`, test environment registry, and `GameRules` class (before 1.21.11, rules are camelCase like `randomTickSpeed`). `run_gametests.py`'s `ERROR_MARKERS` and the client log check's `LOADED_MARKER` were copied from the 26.3 sources; confirm each string still appears in that branch's sources, then add any difference to the table below.

## 5. Finish

Update the branch's `README.md` (version line, requirements, branch table), then commit with `feat: port mod to Minecraft <mc>` or `feat: backport <feature> to <mc>`. Add anything new you learned to the table below in the same commit.

## Known differences between versions

Add a row each time a hop turns up a change. Only list changes that were checked against real code; a range like "1.21.2 to 1.21.10" means the change happened somewhere between those two supported versions. Loader "All" means vanilla, so every loader sees it.

These loader facts were checked on 26.3 only. On each hop, compare them with the older branch's loader sources and turn any difference into a table row:

| Loader | Area | On 26.3 |
|---|---|---|
| NeoForge | Hoe tilling | Data map `data/neoforge/data_maps/block/transformables.json` appends to `minecraft:hoe`; there's no `BlockToolModificationEvent` |
| Forge | Hoe tilling | No hook into the hoe's block transformer; `PlayerInteractEvent.RightClickBlock` runs a vanilla `BlockTransformer` built like Fabric's helper |
| Forge | Mixins | Found through the jar manifest's `MixinConfigs`, not `[[mixins]]`; upstream Mixin knows `JAVA_21` at most and wants `minVersion`, so the Forge build rewrites its copy of the config |
| Forge | Mod files | Each mod needs a `pack.mcmeta`; its format is `data_pack_format` |
| Forge | Events | EventBus 7: game events have a static `BUS`; `RegisterEvent.getBus(modBusGroup)` |
| NeoForge, Forge | Metadata | `loaderVersion` `[4,)` (NeoForge FML) and `[66,)` (Forge); NeoForge wants `iconFile`, Forge reads `logoFile` |
| Fabric | Access wideners | Fabric API opens `Blocks.always`, `CreativeModeTab.Output`, and `UseOnContext`'s level constructor; shared code avoids them (see the add-forge-and-neoforge design) |
| All | Dev-only checks | Dev builds throw when a positional environment attribute (moon phase) is read without a position; only NeoForge's and Forge's dev runs set that flag |

| Changed in | Loader | Area | Before | After |
|---|---|---|---|---|
| 26.3 | All | Hoe tilling | `TillableBlockRegistry.register` | `BlockTransformerHelper.registerTilling(BlockPredicate, Block)`; tilling is a data-driven block transformer |
| 26.3 | All | Bone meal | `isValidBonemealTarget(level, pos, state)` and siblings | Extra `BonemealSource` argument (`BonemealSource.INTERACTION`) |
| 26.3 | All | Farmland | Hardcoded revert to `Blocks.DIRT`, so the mod reimplemented moisture and trampling | `new FarmlandBlock(baseBlock, properties)` reverts to `baseBlock` |
| 26.3 | All | Block codecs | `codec()` override and `propertiesCodec()` | Removed |
| 26.3 | All | `Properties.isViewBlocking` | Took `Blocks::always` | Takes an `AABB` predicate; vanilla farmland no longer sets it |
| 26.3 | All | Farmland model | Parent `block/template_farmland` | Parent `block/template_cube_bottom_top_indented` with `bottom`/`side`/`top` textures |
| 26.3 | All | Recipe-unlock advancement | `"recipe": "<id>"` | `"recipes": "<id>"` |
| 26.3 | Fabric | Entity renderer registration | Fabric `EntityRendererRegistry.register` | Deprecated; vanilla `EntityRenderers.register` is public |
| 26.3 | All | Farmland trample call | `fallOn` calls static `FarmlandBlock.turnToDirt(entity, state, level, pos)` | Instance `turnToBaseBlock(entity, state, level, pos)`; the hotel trample mixin wraps that call, so older branches wrap `turnToDirt` instead and also check hotels in the mod tiers' own `fallOn` (see the Farmland row) |
| 26.3 | All | Dropping an item from a player | `player.drop(stack, false, false)` | `createItemStackToDrop(stack, false, false)`, then add the entity yourself (what `/give` does) |
| 26.3 | All | Loot table tag entry | `{"type": "minecraft:tag", "name": "ns:tag"}` (26.2 and older) | `"items": "#ns:tag"` |
| 26.3 | All | `BlockPos.withinManhattan` | `(origin, reachX, reachY, reachZ)` | `(origin, reach)`; per-axis forms are `withinClippedManhattan` and `withinBoxByManhattanDistance` |
| 26.3 | All | Lantern-like push reaction | `PushReaction.DESTROY` | `PushReaction.POPPED` (Fairy Hotel properties) |
| 26.3 | All | Advancement `location` conditions | A JSON list of conditions keyed `"condition"` | One condition object (`{"type": "minecraft:all_of", "terms": [...]}`) keyed `"type"` |
| 26.3 | All | `TradeSet` | Class with `getTrades()`; vanilla's Wandering Trader rare pool has 15 trades | Record with `trades()`; the pool has 16 |
| 26.3 | All | Falling block codec | `FallingBlock.codec()` is abstract, so Enriched Sand names a `simpleCodec` | Removed (see Block codecs) |
| 26.2 | All | Vanilla entity type constants | `EntityType.COW` and friends | New `EntityTypes` class holds them (`EntityTypes.COW`); `EntityType` remains for the type itself |
| 26.2 | All | Advancement trigger classes | `net.minecraft.advancements.criterion` (`PlayerTrigger`) | `net.minecraft.advancements.triggers` |
| 26.2 | All | `FlyingMoveControl` | Not generic: `new FlyingMoveControl(mob, ...)` | Generic: `new FlyingMoveControl<>(mob, ...)` |
| 26.1 | All | `#minecraft:dirt` block tag | Also holds grass block, podzol, mycelium, moss, and mud | Only dirt, coarse dirt, and rooted dirt; grass, podzol, and mycelium moved to `#minecraft:grass_blocks` (the grove's tag lists both) |
| 26.1 | Fabric | Wandering Trader trades | Code: Fabric `TradeOfferHelper.registerWanderingTraderOffers(b -> b.addOffersToPool(WanderingTraderOffersBuilder.SELL_SPECIAL_ITEMS_POOL, new VillagerTrades.ItemsForEmeralds(item, emeralds, count, maxUses, xp)))` | Data: `data/<ns>/villager_trade/wandering_trader/*.json` listed in the `minecraft:wandering_trader/uncommon` villager trade tag; no code |
| 26.1 | All | Time of day | `level.getDayTime()` | World clocks: `level.getOverworldClockTime()` (used for gift cooldown days and Jarred Fairy expiry) |
| 26.1 | All | Farmland class | `FarmBlock` | `FarmlandBlock` |
| 26.1 | Fabric | Fabric POI registration | `PointOfInterestHelper.register(id, tickets, searchDistance, blocks...)` (object builder 21.1) | `PoiHelper.register` with the same arguments (object builder 23.0) |
| 26.1 | Fabric | Fabric server tick event | `ServerTickEvents.END_WORLD_TICK` | `END_LEVEL_TICK` |
| 26.1 | All | Hotbar message | `player.displayClientMessage(component, true)` | `player.sendOverlayMessage(component)` |
| 26.1 | Fabric | Fabric model layers | `EntityModelLayerRegistry.registerModelLayer` | `ModelLayerRegistry.registerModelLayer` |
| 26.1 | Fabric | Optional mod dependency (Jade) | `modCompileOnly` and `modLocalRuntime "maven.modrinth:jade:<1.21.11 version>"`; check the Jade API jar for changed provider methods | `compileOnly`, `gametestCompileOnly`, and `localRuntime` (skipped with `-PnoJade`) |
| 26.1 | Fabric | Build setup | Obfuscated: `mappings loom.officialMojangMappings()` and `modImplementation` | Unobfuscated: plain `implementation`, no mappings |
| 1.21.11 | All | `ResourceLocation` | `net.minecraft.resources.ResourceLocation` | Renamed `Identifier` |
| 1.21.11 | All | Game rule names | camelCase (`randomTickSpeed`) | snake_case (`random_tick_speed`) |
| 1.21.2 to 1.21.10 | All | Entity rendering | `MobRenderer<Entity, Model>`, `HierarchicalModel` with `root()` and `setupAnim(entity, limbSwing, ..., ageInTicks, ...)`; reset pose yourself with `getAllParts().forEach(ModelPart::resetPose)` | Render states: `MobRenderer<Entity, State, Model>`, `createRenderState()`, `setupAnim(state)` resets the pose |
| 1.21.2 to 1.21.10 | All | Entity creation and ids | `EntityType.create(level)`, `Builder.build(String)` (logs a harmless dev-only "No data fixer" error) | `create(level, EntitySpawnReason)`, `Builder.build(ResourceKey)` |
| 1.21.2 to 1.21.10 | All | Item ids | `new Item.Properties()` | `Properties.setId(key)` before building |
| 1.21.2 to 1.21.10 | All | Night check | `level.isNight()` | `level.isDarkOutside()` |
| 1.21.2 to 1.21.10 | All | Goal helpers | Cast `(ServerLevel) mob.level()` | `Goal.getServerLevel(mob)` |
| 1.21.2 to 1.21.10 | All | Recipe ingredients | `{"item": "<id>"}` objects | Plain `"<id>"` strings |
| 1.21.2 to 1.21.10 | All | Item models | Only `models/item/<id>.json` | Also needs `items/<id>.json` |
| 1.21.2 to 1.21.10 | All | Spawn eggs | `new SpawnEggItem(type, bgColor, spotColor, props)`, tinted by colors (white keeps a painted texture as is) | `Properties.spawnEgg(type)`, per-mob texture |
| 1.21.2 to 1.21.10 | Fabric | Renderer registration | Vanilla `EntityRenderers.register` is private; use Fabric `EntityRendererRegistry` | Public |
| 1.20.2 to 1.21.1 | All | Bone meal | `isValidBonemealTarget(level, pos, state, isClient)` | No `isClient` argument |
| 1.20.2 to 1.21.1 | All | `ItemStack.consume` | Missing; use `shrink(1)` (creative mode restores the stack) | `consume(amount, entity)` |
| 1.20.2 to 1.21.1 | All | `EntityType.Builder.eyeHeight` | Missing; the default eye height is used | Present |
| 1.20.2 to 1.21.1 | All | Data folders | Plural: `recipes/`, `advancements/`, `loot_tables/`, `tags/blocks/`. Singular folders are silently ignored, so check every new data file's path | Singular: `recipe/`, `advancement/`, `loot_table/`, `tags/block/` |
| 1.20.2 to 1.21.1 | All | Recipe result | `"result": {"item": "<id>"}` | `"result": {"id": "<id>"}` |
