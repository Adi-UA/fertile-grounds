# Known differences between versions

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
| 26.1 | All | Block render layers | Blocks missing from vanilla's `ItemBlockRenderTypes` draw solid, so see-through pixels turn black: Fabric `BlockRenderLayerMap.putBlocks` (`ChunkSectionLayer.CUTOUT` on 1.21.10 and 1.21.11, `RenderType.cutout()` through `INSTANCE` on 1.21.1), NeoForge and Forge `"render_type": "minecraft:cutout"` in the model (children inherit it); `AssetConsistencyTest.hotelsDrawWithCutout` checks both | Picked from the textures |
| 1.20.2 to 1.21.1 | All | Java | Java 17; no `SequencedCollection` (`getFirst`, `getLast`, `reversed`) | Java 21 |
| 1.20.2 to 1.21.1 | All | Item data | Item NBT through `getOrCreateTag()`, keyed by the old component id (`ModDataComponents.getLong`/`setLong`) | Data components |
| 1.20.2 to 1.21.1 | All | Block methods | `BlockBehaviour` overrides are public; blocks have no codecs, so falling blocks need no `codec()` | `protected`, and `FallingBlock` needs a `MapCodec` |
| 1.20.2 to 1.21.1 | All | Using a block | One `use(state, level, pos, player, hand, hit)` per hand, returning `InteractionResult`; damage a tool with `hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand))` | `useItemOn` and `useWithoutItem`, `ItemInteractionResult` |
| 1.20.2 to 1.21.1 | All | Using up items on a block | Block `use` doesn't restore creative stacks, so check `player.getAbilities().instabuild` before `shrink(1)`, as vanilla's cake does | `consume(1, player)` |
| 1.20.2 to 1.21.1 | All | Advancement triggers | No registry: `CriteriaTriggers.register` (private) through `CriteriaTriggersInvoker`, called from each entry point; `PlayerTrigger` takes its id | `BuiltInRegistries.TRIGGER_TYPES` |
| 1.20.2 to 1.21.1 | All | Ranged integer gamerules | None in vanilla: Forge builds the rule with `IntegerValue.create(default, onChange)` and swaps its argument for `IntegerArgumentType.integer(min, max)` through `GameRuleTypeAccessor` | `IntegerValue.create(default, min, max, onChange)` |
| 1.20.2 to 1.21.1 | All | Entity size | No `Attributes.SCALE`: sync a value and scale in the renderer's `scale()`; no `EntityType.Builder.eyeHeight`, so override `getStandingEyeHeight` | Scale attribute, `eyeHeight` |
| 1.20.2 to 1.21.1 | All | Recipe results with data | A result is an item and count only, so a custom `RecipeSerializer` reads a shapeless recipe and swaps in the stack (`GuideBookRecipeSerializer`); clients get a plain shapeless recipe | `"components"` on the result |
| 1.20.2 to 1.21.1 | All | Item names | No `item_name`; `setHoverName` with italic turned off reads the same | `DataComponents.ITEM_NAME` |
| 1.20.2 to 1.21.1 | All | Ids and holders | `new ResourceLocation(ns, path)`; effects, attributes, and `MobEffectInstance` take plain values; effect modifiers take a UUID string and `MULTIPLY_TOTAL` | `fromNamespaceAndPath`, `Holder`s, `ADD_MULTIPLIED_TOTAL` with an id |
| 1.20.2 to 1.21.1 | All | Blocks | `Blocks.GRASS` | `Blocks.SHORT_GRASS` |
| 1.20.2 to 1.21.1 | All | Loot tables | `server.getLootData().getLootTable(id)`; no inline tables, so a nested table is its own file referenced by `"name"` | `reloadableRegistries()` and `ResourceKey`s; inline `"value"` |
| 1.20.2 to 1.21.1 | All | Advancement JSON | Icons `{"item": ...}`; item predicates list `"items"` as an array | `{"id": ...}`; one item or tag string |
| 1.20.2 to 1.21.1 | All | Dispensers | `net.minecraft.core.BlockSource` with `getPos()`, `getBlockState()`, `getLevel()` | A record in `core.dispenser` |
| 1.20.2 to 1.21.1 | All | GameTests | No barrier roof, so no `skyAccess`; `TestFunction(batch, name, structure, rotation, maxTicks, setupTicks, required, successes, attempts, fn)`; no `assertValueEqual`, public `getBounds`, or `getEntities(type)`; crafting checks use a `TransientCraftingContainer`; `ServerPlayer(server, level, profile)` and `placeNewPlayer(connection, player)`; structures `DataVersion` 3465 | Barrier roof, `CommonListenerCookie`, `CraftingInput` |
| 1.20.2 to 1.21.1 | All | Codecs | `DataResult.getOrThrow(boolean, Consumer)`; use `result().orElseThrow()` | `getOrThrow()` |
| 1.20.2 to 1.21.1 | Fabric | Attachments | Fabric API 0.92: `AttachmentRegistry.<T>builder().persistent(codec).copyOnDeath().buildAndRegister(id)` | `create(id, builder -> ...)` |
| 1.20.2 to 1.21.1 | Fabric, Forge | Jade | Jade 11 sends server data only for block entities (`registerBlockDataProvider` takes a `BlockEntity` class), so block-only lines that need the server go; Jade 11 has a Forge build, loaded by `@WailaPlugin` | Any block class |
| 1.20.2 to 1.21.1 | Forge | Build | Forge 47 runs with SRG names: ModDevGradle 2.0.148's `legacyforge` (also for `common`, on MCP), `reobfJar`, a `mixin {}` refmap, and the `org.spongepowered:mixin:0.8.7:processor` release; the Forge copy of the mixin config names the refmap; `data_pack_format` 15 | No renaming, no refmap |
| 1.20.2 to 1.21.1 | Forge | Loader API | `TickEvent.LevelTickEvent` with `phase == END`; `DimensionDataStorage.computeIfAbsent(load, create, name)` and `SavedData.save(tag)`; a no-registry-lookup codec through `NbtOps.INSTANCE` | `LevelTickEvent.Post`, `SavedData.Factory`, registry-aware codecs |
| 1.20.2 to 1.21.1 | Forge | GameTests | The test server loads its world like a dedicated server (`server.properties`, last save), so `runGameTestServer` deletes the world and writes a flat, structure-free one; Forge turns GameTests off in production (`!FMLLoader.isProduction()`), so the release jar only gets a boot check | Vanilla's fresh flat world |
| 1.20.2 to 1.21.1 | NeoForge | Support | NeoForge 47.1 reports itself as Forge 47.1.106 and rejects vanilla `Registry.register` inside `RegisterEvent` ("locked registry"), so 1.20.1 ships no NeoForge listing; `-PneoforgeCompat` reproduces it | NeoForge 21+ accepts it |
| 1.20.2 to 1.21.1 | All | Bone meal | `isValidBonemealTarget(level, pos, state, isClient)` | No `isClient` argument |
| 1.20.2 to 1.21.1 | All | `ItemStack.consume` | Missing; use `shrink(1)` (item use restores creative stacks; see the block row above) | `consume(amount, entity)` |
| 1.20.2 to 1.21.1 | All | Data folders | Plural: `recipes/`, `advancements/`, `loot_tables/`, `tags/blocks/`. Singular folders are silently ignored, so check every new data file's path | Singular: `recipe/`, `advancement/`, `loot_table/`, `tags/block/` |
| 1.20.2 to 1.21.1 | All | Recipe result | `"result": {"item": "<id>"}` | `"result": {"id": "<id>"}` |
