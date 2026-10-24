# Design

## Context

Each version branch (`26.1`, `26.2`, `main` for 26.3) is today a single Fabric Loom project. Of 68 main classes, 13 import Fabric API, and each piece has a NeoForge and a Forge counterpart:

| Fabric API | Used in | NeoForge | Forge |
|---|---|---|---|
| `ModInitializer`, `ClientModInitializer` | entry points | `@Mod` class, plus a `@Mod(dist = Dist.CLIENT)` one | `@Mod` class, plus a `Dist.CLIENT` `@Mod.EventBusSubscriber` |
| `Registry.register` in static fields | 8 `Mod*` classes | `RegisterEvent` | `RegisterEvent` |
| `ServerTickEvents.END_LEVEL_TICK` | visit scheduler, speed aura, chimes, grove builder | `LevelTickEvent.Post` | `TickEvent.LevelTickEvent.Post` |
| `ServerLifecycleEvents.SERVER_STOPPED` | chimes, grove builder | `ServerStoppedEvent` | `ServerStoppedEvent` |
| `ServerPlayConnectionEvents.JOIN` | guide book gift | `PlayerEvent.PlayerLoggedInEvent` | `PlayerEvent.PlayerLoggedInEvent` |
| `AttachmentRegistry` | visits, gift cooldowns, grove jobs, guide flag | `AttachmentType` in `NeoForgeRegistries.ATTACHMENT_TYPES` | vanilla `SavedData` (decision 3) |
| `BlockTransformerHelper.registerTilling` (26.3), `TillableBlockRegistry` (26.2 and 26.1) | hoe tilling | 26.3: `neoforge:transformables` data map; 26.2 and 26.1: `BlockEvent.BlockToolModificationEvent` for `ItemAbilities.HOE_TILL` | 26.3: `PlayerInteractEvent.RightClickBlock` running a vanilla `BlockTransformer`; 26.2 and 26.1: `BlockEvent.BlockToolModificationEvent` for `ToolActions.HOE_TILL` |
| `PoiHelper` | Fairy Hotel lookup | vanilla `PoiType` through `RegisterEvent` | same |
| `FabricDefaultAttributeRegistry` | Bean Fairy | `EntityAttributeCreationEvent` | `EntityAttributeCreationEvent` |
| `GameRuleBuilder` | 13 gamerules | none: vanilla's `GameRule` constructor, registered with the other `Mod*` classes | same |
| `FabricCreativeModeTab` | creative tab | vanilla `CreativeModeTab.builder()` through `RegisterEvent` | same |
| `ModelLayerRegistry` | fairy model | `EntityRenderersEvent.RegisterLayerDefinitions` and `RegisterRenderers` | same names in Forge |
| `jade` entrypoint | Jade plugins | `@WailaPlugin` | none, since Jade has no Forge build for 26.x |
| `fabric-gametest` entrypoint, `@GameTest` | 26 test classes | vanilla `Registries.TEST_FUNCTION` through `RegisterEvent`, in a dev-only test mod | same |

The four mixins (`CreeperMixin`, `FarmlandBlockMixin`, `ItemEntityMixin`, `ServerPlayerMixin`) target vanilla code only. NeoForge and Forge both ship Mixin and MixinExtras, so the mixins stay shared. Minecraft 26.x ships without obfuscation, so all three loaders see the same class and method names. These versions were checked on 2026-10-10 at `maven.neoforged.net` and `maven.minecraftforge.net`:

| Minecraft | NeoForge | Forge |
|---|---|---|
| 26.1 | 26.1.2.x, stable | 64.1.x, recommended 64.1.0 |
| 26.2 | 26.2.0.x, stable | 65.1.x, recommended 65.1.0 |
| 26.3 | 26.3.0.x, beta | 66.0.x, no recommended build |

Every row holds on all three branches: the code compiles against NeoForge 26.1.2.116, 26.2.0.90, and 26.3.0.69-beta and Forge 64.1.0, 65.1.0, and 66.0.10, and every GameTest passes on each. Hoe tilling is the only hook that differs between Minecraft versions.

## Goals / Non-Goals

**Goals:**
- Build Fabric, NeoForge, and Forge jars from one shared source on each version branch.
- Run the same JUnit tests once and the same GameTests on every loader, locally and in CI.
- Keep the Fabric jar's content and behavior unchanged.

**Non-Goals:**
- Sharing code between version branches. The one-branch-per-version rule stays.
- Moving saved data between loaders. Blocks and items carry over, while per-world and per-player data starts fresh (see the `forge-loaders` spec).

## Decisions

### 1. Build layout: MultiLoader-Template with each loader's own plugin

Each branch becomes `common/`, `fabric/`, `neoforge/`, and `forge/`, with shared `buildSrc` convention scripts. The common, Fabric, and NeoForge parts copy jaredlll08/MultiLoader-Template (branch `26.1.1`, last commit 2026-04-07). `forge/` follows the Forge MDK build (`mdk/build.gradle` on Forge's `26.1.2` branch, ForgeGradle `[7.0.17,8)`) and reads `common` the same way the other two loaders do. `common` compiles against vanilla through ModDevGradle's NeoForm mode, so it can't import any loader's API. Each loader project compiles `common`'s sources into its own jar. This keeps each loader on the plugin its own team maintains and adds no runtime library for players.

### 2. Registration: each registry's classes load inside that registry's event

On Fabric, registries are open during mod init. On NeoForge and Forge, they only accept entries inside `RegisterEvent`, and Forge locks every registry it wraps (blocks, items, entity types, sounds, effects, points of interest) outside its own event. The `Mod*` classes already register through `Registry.register` in their static fields, the way vanilla does, so they stay unchanged. `FertileGrounds.REGISTRATIONS` maps each registry to the class that fills it, in vanilla's registry order. Fabric runs them all at startup, while NeoForge and Forge run each one from `RegisterEvent` for that registry, the pattern Botania used on Forge (`Forge/src/main/java/vazkii/botania/forge/ForgeCommonInitializer.java:266`, `bind`). This needs one rule: a class touches only its own registry while it loads. Block items therefore move from `ModBlocks` to `ModItems`, where vanilla registers its own. A violation crashes the test server at startup, so GameTests catch it.

The creative tab and the Fairy Hotel point of interest need a loader API, so each loader registers those itself from shared data (`ModItemGroups.contents()`, `FairyHotelLookup.occupiedHotels()`). Gamerules are built with vanilla's `GameRule` constructor, which is all Fabric API's `GameRuleBuilder` does for whole-number and true/false rules, so they need no loader code.

Fabric's transitive access wideners open a few vanilla members that NeoForm compiles as private or protected. Shared code avoids them instead of adding an access transformer: a lambda replaces `Blocks::always`, and a test reaches `UseOnContext`'s protected constructor through an empty subclass.

### 3. Saved data: each loader's own storage behind one interface

Fabric worlds from 2.0.0 already store visits, cooldowns, grove jobs, and the guide flag as Fabric attachments with fixed ids, so Fabric keeps them and those worlds keep their data. `util/ModSavedData` declares the four values once through `platform.Platform` (`LevelData<T>` for per-world values, `PlayerFlag` for the guide flag). Fabric and NeoForge store them as data attachments, each with its own attachment API. Forge has no attachment API, so it stores them as vanilla `SavedData`, and the guide flag there becomes a set of player UUIDs on the overworld.

### 4. Events: each loader's entry point calls shared handlers

`FertileGrounds` exposes three handlers: `onLevelTick`, `onServerStopped`, and `onPlayerJoin`. Each loader's entry point connects them to its own events, along with tilling (`ModBlocks.TILLED`), Bean Fairy attributes, and the client model layer and renderer. Only what shared code must call into (saved data and `isModLoaded`) goes through `java.util.ServiceLoader`, the template's `Services` pattern. Fabric's entry point makes the same Fabric API calls the mod made before, so Fabric behavior can't drift. Each loader's code reads like that loader's own docs and examples. Where sharing would need an abstraction that fits no loader well, each loader gets its own short, idiomatic copy instead: some repetition is fine, but awkward shared code is not. Gameplay logic stays in `common` and is never copied. The Forge jar includes no Jade classes.

### 5. GameTests: one source, vanilla test data

Test classes live in `common/src/gametest` and use only vanilla types plus a common `@FertileTest` annotation with the five fields the tests use (`environment`, `structure`, `maxTicks`, `skyAccess`, `padding`) and Fabric API's defaults. Since 1.21.5, a vanilla test has two parts: a function in the `test_function` registry, and a test-instance data file naming it. `TestCatalog` provides both. Each loader registers `TestCatalog.functions()` in the vanilla registry (Fabric at init, NeoForge and Forge in `RegisterEvent`). The build writes one test-instance file per test with `TestCatalog.main`, the way Forge's own test mod generates them as data. Every loader's test server then loads the same tests like any other data. Fabric API offers no public way to add test instances from code (it uses an internal mixin), so this data route is the one that works the same everywhere. Test ids keep Fabric API's naming, so filters and reports read as before. The gametest structures, which are empty spaces of a given size, become vanilla `.nbt` files under `data/<namespace>/structure/`, since only Fabric reads `.snbt` text structures from mods. `JadeDataTests` carries a loader condition that skips it on Forge. The test-environment JSON files load unchanged everywhere.

### 6. Scripts and CI

`run_gametests.py` and `client_log_check.py` take `--loader fabric|neoforge|forge` and call that loader's GameTest or client task. Each loader's test server writes a JUnit report, which the script reads for per-test results. CI runs a matrix job per loader: `./gradlew :<loader>:build` without its GameTest task, then `python3 scripts/run_gametests.py --loader <loader>`, so the data-load scan runs too. `actions/upload-artifact` keeps the report and `gametest.log` when a run fails. Locally, `--loader all` starts the three test servers at the same time, each in its own run folder, and prints one summary. The workflow already triggers on every push and pull request, so the 26.1 and 26.2 branches get it through the normal merge.

### 7. Version, naming, and release

`mod_version` stays `2.0.0`. The jars are named:

| Loader | Jar name |
|---|---|
| Fabric | `fertilegrounds-2.0.0+<mc>.jar` (unchanged) |
| NeoForge | `fertilegrounds-neoforge-2.0.0+<mc>.jar` |
| Forge | `fertilegrounds-forge-2.0.0+<mc>.jar` |

The new jars join the existing v2.0.0 GitHub release. The `v2.0.0` tag points at the Fabric-only commit, so the release notes name the commit each new jar was built from. On Modrinth, the version numbers are `2.0.0+<mc>-neoforge` and `2.0.0+<mc>-forge`, so they don't collide with Fabric's. A file goes out as Beta while its loader has no stable or recommended build for that Minecraft version (see the `release-2-0` spec). On 2026-10-10 that applies to both 26.3 files.

### Look and sound

This change adds no new moment. Every existing moment looks and sounds the same on each loader, because all of them read the same assets, and the maintainer confirmed each row in the NeoForge and Forge clients:

| Moment | Look, sound, and particle to compare against Fabric |
|---|---|
| Fertilizer upgrades soil | the soil swap and its feedback from the `soil-fertilizer` spec |
| Fairy visit | the descent, growth rings, and sounds from `fairy-visit-feedback` |
| Catching a fairy | the bottle and catch feedback from `fairy-hotel` |
| Brushing a hotel | the petting hearts and sparkles from `fairy-hotel-effects` |
| Chime Hotel rings | the glow and chime notes from `chime-hotel` |
| Grove builds | the builder fairies and column growth from `fairy-grove` |
| Jade tooltips (NeoForge only) | each line from `jade-tooltips` |

### Porting divergence table

The table in `.claude/skills/porting-minecraft-version/SKILL.md` has a Loader column, with a row for each loader difference between versions, such as hoe tilling and each Forge version's `loaderVersion` and data pack format.

## Risks / Trade-offs

- **26.3 loaders aren't final.** Before NeoForge 26.3 goes stable and Forge 66 gets a recommended build, their API can change and break the 26.3 builds. Both 26.3 files ship as Beta. Moving to a final build reruns that loader's tests before a new upload as Release.
- **Registration order.** On NeoForge and Forge, a `Mod*` class that loads another registry's class too early crashes the server at startup. GameTests catch it on every run, and a comment on the registrar states the rule.
- **Fabric jar drift.** The restructure touches every file path. `scripts/compare_release_jar.py` diffs the new Fabric jar's file list and `fabric.mod.json` against the released 2.0.0 jar, and every Fabric GameTest still passes.
- **CI time.** CI runs the three loaders as parallel matrix jobs on separate runners, so a push waits about as long as one loader takes today (about 2 minutes). Only the billed runner minutes triple, and public repositories get them free.
- **Three branches, three loaders.** Each restructure lands on `main` first, then gets ported down one branch at a time with the porting skill, per AGENTS.md.
