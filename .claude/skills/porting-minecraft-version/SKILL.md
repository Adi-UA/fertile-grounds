---
name: porting-minecraft-version
description: Port Fertile Grounds to a new Minecraft version, or backport a feature from a newer branch to an older one, for all three loaders (Fabric, NeoForge, Forge). Use when asked to port, backport, update to a new Minecraft, Fabric, NeoForge, or Forge version, or bring a feature to other branches.
---

# Porting between Minecraft versions

Move one version hop at a time (26.3 to 26.2, then 26.2 to 26.1, never 26.3 straight to 26.1), and port all three loaders in the same hop. Since 2.0 the targets are `26.2` and `26.1`. `1.21.1` got 2.0.0 straight from 26.1 (its rows are "1.21.2 to 26.1"), `1.21.11` got it from 26.1 in one hop (rows "26.1" give the 1.21.11 form, and rows "1.21.2 to 1.21.11" changed by then), `1.21.10` got it from `1.21.11` (rows "1.21.11" give what that hop undid), and versions before 26.1 get 2.0.0 only; `1.20.1` stays on 1.2.0. Finish and verify each branch before starting the next one, because each hop's fixes feed into the next.

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

Run the `AGENTS.md` verification steps on this branch for every loader: `python3 scripts/run_gametests.py --loader all` and `client_log_check.py --loader <each>`. The GameTests use 26.3's `GameTestHelper`, data-driven test environments (`common/src/gametest/resources/data/fertilegrounds/test_environment/`), test-instance data that `TestCatalog` writes, empty `.nbt` structures (their `DataVersion` is 26.3's), and snake_case game rule names (`random_tick_speed`); check each against the branch's `GameTestHelper`, test environment registry, and `GameRules` class (before 1.21.11, rules are camelCase like `randomTickSpeed`). `run_gametests.py`'s `ERROR_MARKERS` were copied from the 26.3 sources; confirm each string still appears in that branch's sources. The client log check's `LOADED_MARKER` is built at runtime, so it only shows up in a real log: a passing client check confirms it. Add any difference to the table below.

## 5. Finish

Update the branch's `README.md` (version line, requirements, branch table), then commit with `feat: port mod to Minecraft <mc>` or `feat: backport <feature> to <mc>`. Add anything new you learned to the table below in the same commit.

## Known differences between versions

Add a row each time a hop turns up a change. Only list changes that were checked against real code; a range like "1.21.2 to 1.21.10" means the change happened somewhere between those two supported versions. Loader "All" means vanilla, so every loader sees it.

These loader facts were checked on 26.3, and 26.2 and 26.1 match them except for the rows marked 26.3 and 26.2 in the table below. On each new hop, compare them with the older branch's loader sources and turn any difference into a table row:

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
| 26.3 | All | Sign click commands (demo world) | A waxed sign runs its `run_command` click event | The sign also needs `allow_op_features:1b`, which only an operator's command sets; the demo script's `sign()` adds it, and 26.2 ignores the extra tag |
| 26.3 | NeoForge | Hoe tilling | `BlockEvent.BlockToolModificationEvent` on `NeoForge.EVENT_BUS` for `ItemAbilities.HOE_TILL`, gated by `HoeItem.onlyIfAirAbove` | Data map `data/neoforge/data_maps/block/transformables.json`; the event is gone |
| 26.3 | Forge | Hoe tilling | `BlockEvent.BlockToolModificationEvent.BUS` (a `Predicate` listener returning false) for `ToolActions.HOE_TILL`, gated by `HoeItem.onlyIfAirAbove` | No tilling hook; `PlayerInteractEvent.RightClickBlock` runs a vanilla `BlockTransformer` |
| 26.3 | Forge | Version numbers | Forge 65.x: `loaderVersion = "[65,)"` in both `mods.toml` files (main and gametest), `data_pack_format` 107 | Forge 66.x: `[66,)`, format 121 |
| 26.3 | All | Falling block codec | `FallingBlock.codec()` is abstract, so Enriched Sand names a `simpleCodec` | Removed (see Block codecs) |
| 26.2 | All | Vanilla entity type constants | `EntityType.COW` and friends | New `EntityTypes` class holds them (`EntityTypes.COW`); `EntityType` remains for the type itself |
| 26.2 | Forge | Version numbers | Forge 64.x: `loaderVersion = "[64,)"` in both `mods.toml` files, `data_pack_format` 101 | Forge 65.x: `[65,)`, format 107 |
| 26.2 | NeoForge, Forge | Minecraft patch release | NeoForge's only stable 26.1 builds (26.1.2.x) and Forge's recommended build (64.1.0) run on 26.1.2, so `forge_minecraft_version=26.1.2`; NeoForge picks 26.1.2 from its own version, while `common` and Fabric stay on 26.1 | Both loaders build for 26.2 itself |
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
| 26.1 | Fabric | Creative tab | `FabricItemGroup.builder()` (item group API) | `FabricCreativeModeTab.builder()` (creative tab API) |
| 26.1 | All | Server-wide gamerules | 1.21.11's `MinecraftServer` has no `getGameRules` (1.21.10's does); read `server.overworld().getGameRules()` | `server.getGameRules()` |
| 26.1 | All | Recipes and entity use | `Recipe.assemble(input, registries)`, `Player.interactOn(entity, hand)` | `assemble(input)`, `interactOn(entity, hand, location)` |
| 26.1 | All | Test environment time | `{"type": "minecraft:time_of_day", "time": 18000}` | `clock_time` with a `clock` |
| 26.1 | All | Test spacing | Test instances have no `padding`, and tests sit 5 blocks apart, so a fairy test keeps the fairy it spawned and `TestSupport.fairies` looks only 4 blocks to each side | `padding` in the test instance |
| 26.1 | Forge | Saved data and mod list | `SavedDataType` takes a file name (`fertilegrounds_<path>`); `ModList.get().isLoaded` | An `Identifier`; static `ModList.isLoaded` |
| 26.1 | Forge | Version numbers | Forge 61.x on 1.21.11: `loaderVersion = "[61,)"`, `data_pack_format` 94 (Forge 60.x on 1.21.10: `[60,)`, format 88); Apple Silicon's `lwjgl-freetype` is only on Mojang's library maven, so `forge/build.gradle` keeps Maven Central from claiming it (also on 1.21.1) | Forge 64.x: `[64,)`, format 101, no repository fix |
| 1.21.11 | All | Package moves | `net.minecraft.world.entity.npc.VillagerTrades` and `npc.WanderingTrader`, `animal.Bee`, `net.minecraft.Util`, `advancements.critereon`; `ItemListing.getOffer(entity, random)` | `npc.villager.VillagerTrades`, `npc.wanderingtrader.WanderingTrader`, `animal.bee.Bee`, `net.minecraft.util.Util`, `advancements.criterion`; `getOffer(level, entity, random)` |
| 1.21.11 | All | `ResourceLocation` | `net.minecraft.resources.ResourceLocation`; `ResourceKey.location()` | Renamed `Identifier`; `ResourceKey.identifier()` |
| 1.21.11 | All | Game rule names | camelCase (`randomTickSpeed`); the clock stops with `doDaylightCycle` (`GameRules.RULE_DAYLIGHT`); a `game_rules` test environment lists `bool_rules` and `int_rules` as `{"rule", "value"}` entries | snake_case (`random_tick_speed`); `doDaylightCycle` became `advance_time`; one `rules` map |
| 1.21.11 | All | GameTest messages | `assertTrue`, `assertValueEqual`, `fail`, and the other asserts take a `Component`, so test text goes in `Component.literal` | `String` overloads |
| 1.21.11 | All | Moon phase | `level.getMoonPhase()`, 0 is the full moon | Positional environment attribute `EnvironmentAttributes.MOON_PHASE` (see dev-only checks above) |
| 1.21.2 to 1.21.10 | All | Entity rendering | `MobRenderer<Entity, Model>`, `HierarchicalModel` with `root()` and `setupAnim(entity, limbSwing, ..., ageInTicks, ...)`; reset pose yourself with `getAllParts().forEach(ModelPart::resetPose)` | Render states: `MobRenderer<Entity, State, Model>`, `createRenderState()`, `setupAnim(state)` resets the pose |
| 1.21.2 to 1.21.10 | All | Entity creation and ids | `EntityType.create(level)`, `Builder.build(String)` (on Fabric, logs a harmless "No data fixer" error at startup, in dev and in play; `client_log_check.py` skips it) | `create(level, EntitySpawnReason)`, `Builder.build(ResourceKey)` |
| 1.21.2 to 1.21.10 | All | Item ids | `new Item.Properties()` | `Properties.setId(key)` before building |
| 1.21.2 to 1.21.10 | All | Night check | `level.isNight()` | `level.isDarkOutside()` |
| 1.21.2 to 1.21.10 | All | Goal helpers | Cast `(ServerLevel) mob.level()` | `Goal.getServerLevel(mob)` |
| 1.21.2 to 1.21.10 | All | Recipe ingredients | `{"item": "<id>"}` objects | Plain `"<id>"` strings |
| 1.21.2 to 1.21.10 | All | Item models | Only `models/item/<id>.json` | Also needs `items/<id>.json` |
| 1.21.2 to 1.21.10 | All | Spawn eggs | `new SpawnEggItem(type, bgColor, spotColor, props)`, tinted by colors (white keeps a painted texture as is) | `Properties.spawnEgg(type)`, per-mob texture |
| 1.21.2 to 1.21.10 | Fabric | Renderer registration | Vanilla `EntityRenderers.register` is private; use Fabric `EntityRendererRegistry` | Public |
| 1.21.2 to 1.21.11 | All | Random ticks | Only chunks within 128 blocks of a non-spectator player get them (`ChunkMap.anyPlayerCloseEnoughForSpawning`); GameTests that wait on random ticks stand a mock player in the test (`TestSupport.randomTickWatcher`) and register it with `getChunkSource().move` | Force-loaded test chunks get them with no player near |
| 1.21.2 to 1.21.11 | All | GameTest API | `@GameTestGenerator` returns `TestFunction`s, `@BeforeBatch` sets up each batch, tests sit 5 blocks apart; `getBlockEntity(pos)` is generic with no class argument, `assertBlockState(pos, predicate, Supplier<String>)`, no `assertBlockTag` or `assertionException` (throw `GameTestAssertException`) | Test instances and test environments as data |
| 1.21.2 to 1.21.11 | All | Player join | `placeNewPlayer` loads the player's save itself | The caller loads it (`TagValueInput`) |
| 1.21.2 to 1.21.11 | All | Text in data components | A JSON string holding the text, e.g. `"minecraft:item_name": "{\"translate\":\"...\"}"` (guide book recipe) | A JSON object |
| 26.1 | All | Wandering Trader rare offers | Code in `VillagerTrades.WANDERING_TRADER_TRADES`: on 1.21.1 a map whose level 2 holds 6 vanilla offers, on 1.21.11 a list of three pools whose index 1 holds 15 (2 picked); vanilla's `ItemsForEmeralds` is package-private, though Fabric API widens it, so its decompiled sources show it public | Data-driven trade sets |
| 1.21.2 to 1.21.11 | All | Data-load log lines | Recipes and advancements that don't decode log `Parsing error loading recipe` and `Parsing error loading custom advancement` | Registry loading errors |
| 1.21.2 to 1.21.11 | Forge | `pack.mcmeta` | `"pack_format": <data_pack_format>`; the newer keys fail to parse and Forge then loads none of the mod's data (`Couldn't load pack metadata`) | `"min_format"` and `"max_format"` |
| 1.21.2 to 1.21.11 | Forge | Registration order | Items register before entity types, so a spawn egg takes a supplier (`ForgeSpawnEggItem`, through `Platform.spawnEgg`) | `Properties.spawnEgg` |
| 1.21.2 to 1.21.11 | NeoForge | Mock players | A mock connection must list the mod channels (`NetworkRegistry.configureMockConnection`), or a mod that sends a packet on join (Jade) throws; the NeoForge test mod passes it to `TestPlayer.setConnectionSetup` | Not needed |
| 1.21.11 | All | Gamerules | `GameRules.Key<IntegerValue>`, registered through `Platform.intRule`/`boolRule`: Fabric `GameRuleRegistry`, NeoForge and Forge the private ranged `IntegerValue.create` (which takes a `FeatureFlagSet` on 1.21.10) through the `IntegerRuleInvoker` mixin, each loaded from the entry point | A `GameRule` registry entry |
| 1.21.2 to 1.21.11 | All | Saved data | `SavedData.Factory` with NBT (Forge stores the codec's value in `fertilegrounds_<path>.dat`) | Codec-based `SavedDataType` |
| 26.1 | All | Crops on mod farmland | Crops and melon and pumpkin stems check `Blocks.FARMLAND`: shared `CropBlockMixin`, `PitcherCropBlockMixin`, `StemBlockMixin`, and `AttachedStemBlockMixin` allow any `FarmBlock` (1.21.1 lacks the stem ones); growth speed through a Fabric-only mixin, NeoForge's own `FarmBlock` check, and Forge's `isFertile` (on 1.21.1 the tiers override it; Forge 61's default already accepts any `FarmBlock`) | `supports_crops` and `grows_crops` block tags |
| 1.21.2 to 1.21.11 | All | Model glow | No element `light_emission`; glow layers draw unlit, and item icons put the glow in `layer1` | `light_emission` on the glow elements |
| 1.21.2 to 1.21.11 | All | Mob effect icons | `assets/minecraft/atlases/mob_effects.json` | The `gui` atlas |
| 1.21.2 to 1.21.11 | All | Firefly, Firefly Bush, pale moss | No firefly particle, Firefly Bush, or pale moss: heralds use `GLOW` plus `AMETHYST_BLOCK_CHIME`, and the hotel recipe uses Glow Berries | Present |
| 1.21.2 to 1.21.11 | All | Structure files | `DataVersion` 3955 | Newer data versions |
| 1.21.2 to 1.21.11 | Forge | Build and events | ForgeGradle 7.0.40 sets up Forge 52.1.20 but applies no access transformer; EventBus 6 (`MinecraftForge.EVENT_BUS`, mod events through `getModEventBus()`); no MixinExtras, so mixins needing it go in a Fabric-only config; `loaderVersion "[52,)"`, `data_pack_format` 48; Mojang's library maven must serve `lwjgl-freetype` on Apple Silicon | EventBus 7, MixinExtras bundled |
| 1.21.2 to 1.21.11 | NeoForge, Forge | Farmland trampling near hotels | `BlockEvent.FarmlandTrampleEvent`, cancelled near an occupied hotel | The shared trample mixin |
| 1.21.2 to 26.1 | All | Sign text in commands (demo world) | Each line a JSON string, clicks as `clickEvent` with `action` and `value`; the game re-saves the JSON, so checks search the line instead of matching it | SNBT text with `click_event` |
| 1.21.2 to 26.1 | All | Block render layers | Blocks draw solid unless told, so see-through pixels turn black: Fabric `BlockRenderLayerMap.putBlocks(RenderType.cutout(), ...)`, NeoForge and Forge `"render_type": "minecraft:cutout"` in the model (children inherit it) | Picked from the textures |
| 1.20.2 to 1.21.1 | All | Bone meal | `isValidBonemealTarget(level, pos, state, isClient)` | No `isClient` argument |
| 1.20.2 to 1.21.1 | All | `ItemStack.consume` | Missing; use `shrink(1)` (creative mode restores the stack) | `consume(amount, entity)` |
| 1.20.2 to 1.21.1 | All | `EntityType.Builder.eyeHeight` | Missing; the default eye height is used | Present |
| 1.20.2 to 1.21.1 | All | Data folders | Plural: `recipes/`, `advancements/`, `loot_tables/`, `tags/blocks/`. Singular folders are silently ignored, so check every new data file's path | Singular: `recipe/`, `advancement/`, `loot_table/`, `tags/block/` |
| 1.20.2 to 1.21.1 | All | Recipe result | `"result": {"item": "<id>"}` | `"result": {"id": "<id>"}` |
