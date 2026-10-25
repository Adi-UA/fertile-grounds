# Agent guide for Fertile Grounds

A mod for Fabric, NeoForge, and Forge adding tiered soil blocks that passively bone-meal crops, plus the Bean Fairy, a rare night visitor that fully grows a 5x5 patch of crops. `README.md` pitches the mod and `docs/DETAILS.md` describes the gameplay in full; this file covers how to work on the code.

## Layout

All game code, data, and assets live in `common/`, compiled against plain Minecraft, so nothing there can use a loader's API. Each loader project builds its own jar from `common/` plus a thin entry point (layout from jaredlll08/MultiLoader-Template).

| Path | Job |
|---|---|
| `common/.../FertileGrounds.java` | What every loader calls: `REGISTRATIONS` (which class fills each registry) and the tick, server-stop, and join handlers |
| `block/` | Soil blocks and their registration (`ModBlocks`, plus `TILLED` for hoe tilling) |
| `item/` | Fairy Dust, all items including block items (`ModItems`), creative tab contents (`ModItemGroups`) |
| `entity/` | `BeanFairy` mob, its `VisitCropGoal`, registration (`ModEntities`) |
| `fairy/` | When fairies visit: `FairyTiming` (pure rules), `FairyVisits` (saved list), `FairyVisitScheduler` (tick handler) |
| `hotel/` | Jarred Fairies and Fairy Hotels: `JarredFairyRules` (pure rules), `JarredFairies` (slipping away in the world), `ChimeRules` and `HotelChimes` (when a Chime Hotel rings); the hotel blocks live in `block/` |
| `grove/` | The Fairy Grove: `GroveLayout` (pure), `GroveShrine` (the shrine check), `FairyGrove` (start and safety scan), `GroveBuilder` (saved jobs, column building, builder fairies) |
| `platform/` | What shared code needs from a loader (`Platform`: saved data and `isModLoaded`), found with `ServiceLoader` |
| `compat/jade/` | Optional Jade tooltips: server data providers here, tooltip lines in `common/src/client/.../compat/jade/`; Fabric loads them through the `jade` entrypoint, NeoForge by `@WailaPlugin`, and Forge leaves them out (Jade has no Forge build for 1.21.1) |
| `mixin/` | Small mixins, only where no loader has a hook (container opened, item entity tick, creeper goals, crops on mod farmland, the ranged gamerule constructor); Fabric's farmland growth and trampling mixins live in `fabric/` |
| `util/` | Shared helpers: ids, tags (`ModTags`), gamerules (`ModGameRules`), saved data (`ModSavedData`), bone-meal boost, full crop growth |
| `common/src/client/` | Fairy model and renderer, Jade tooltip lines |
| `common/src/test/` | JUnit tests for the Minecraft-free logic, plus `AssetConsistencyTest` and `LoaderMetadataTest` for resource files |
| `common/src/gametest/` | GameTests: in-world checks with mock players, one class per feature, listed in `TestCatalog`, with their test environments and structures under `resources/` |
| `fabric/`, `neoforge/`, `forge/` | Each loader's entry points (registration, events, tilling, attributes, creative tab, client renderers), `Platform` implementation, metadata, and dev-only test mod |
| `scripts/` | `run_gametests.py` runs the GameTests and fails on data-load errors; `client_log_check.py` boots a client and fails on asset warnings; `demo/build_demo_world.py` builds the demo world |
| `openspec/` | Feature designs (specs with WHEN/THEN scenarios, tasks); `docs/ROADMAP.md` lists what ships and what is next |
| `art_source/` | Hand-painted textures (`block/`, `item/`) |

Keep one job per class. Registration lives in the `Mod*` classes, and each one only touches its own registry while it loads, because NeoForge and Forge run it inside that registry's event. Logic that doesn't need Minecraft types goes in its own class so JUnit can test it without starting the game (see `FairyTiming`). Loader code reads like that loader's own docs; when sharing would need an awkward abstraction, each loader gets its own short copy instead. Comments stay minimal and describe the code as it is today: a short doc line on classes, methods, and fields, and inline only a one-line why for a Minecraft quirk the code can't show, never a step-by-step walk through the logic or a history of past versions.

## Branches

There's one branch per Minecraft version (the list is in `docs/DETAILS.md`), and `main` tracks the newest one. Code stays identical across branches, except where a Minecraft or loader API change forces a difference. New features land on the newest branch first, then get backported one version at a time, following `.claude/skills/porting-minecraft-version/SKILL.md`.

## Commands

```bash
./gradlew build                       # every project: compile, spotlessCheck, unit tests, Fabric GameTests
./gradlew spotlessApply               # format Java (google-java-format)
./gradlew :common:test                # unit tests only
python3 scripts/run_gametests.py --loader all  # every loader's GameTests at once, plus the data-load error scan
python3 scripts/run_gametests.py --loader neoforge --filter 'fairy_dust*'  # one loader, only matching tests
python3 scripts/client_log_check.py --loader forge  # boots a loader's client, fails on mod warnings
./gradlew :fabric:runGameTest -PnoJade  # GameTests without Jade (also :neoforge:runGameTestServer)
./gradlew :fabric:runClient           # dev client (also :neoforge:runClient, :forge:runClient)
./gradlew :fabric:runClient -Pshaders # dev client with Sodium and Iris; put a shader pack in run/shaderpacks/
python3 scripts/demo/build_demo_world.py  # builds the demo and test world, copies it to the launcher's and each dev client's saves
./gradlew :fabric:genSources          # decompile Minecraft to read real signatures
```

## Verification

`AssetConsistencyTest` catches missing lang keys, models, and textures and outdated recipe JSON, and `LoaderMetadataTest` keeps the three metadata files in step. Data files and mob behavior fail silently until a world loads, so each feature adds GameTests in `common/src/gametest/` (list new classes in `TestCatalog`), and `run_gametests.py` also fails on data that logs a load error without breaking a test. Run the GameTests on every loader. Tests use mock players for anything a player does; tests that move the clock or change gamerules get their own test environment, since tests sharing one run at the same time, and tests that need day or night set clear weather (as `no_random_ticks` does), since rain and thunder darken the sky, and an environment that sets the time also stops the clock (as `night` does), since the clock runs while a test waits for its chunks to load. The test server never loads models, textures, or sounds, so for changes that touch assets, run `client_log_check.py` for each loader. Only looks, sounds, and sleeping through the night (vanilla needs every real player asleep) go to the maintainer as a short in-game checklist; say which parts went untested. Every fixed bug gets a regression check in a GameTest or JUnit, so it can't silently return on another branch.

## Server safety

The server decides everything and the client only shows it; the mod sends no packets of its own today, so a new one needs a reason the design names. Any packet a client sends to the server is untrusted: decode it with size-capped `StreamCodec`s, reject out-of-range values instead of clamping them, send ids or slot numbers rather than whole `ItemStack`s, and check the player can reach the target and its chunk is loaded (`isLoaded`) before touching a block, since a block position from a client can otherwise load or generate far-off chunks ([NeoForge's advisory](https://neoforged.net/news/mitigating-vulnerabilities-network/)). Other gotchas:

- Client-only classes (`net.minecraft.client`) stay in `common/src/client/`; a reference in shared code crashes a dedicated server the moment the class loads, and singleplayer hides it.
- Per-player state must survive rejoining, death, and leaving the End, which replace the player object: Fabric and NeoForge attachments need `persistent` and `copyOnDeath`, and Forge keys it by UUID in overworld saved data. `GuideBookTests` checks all three ways; copy them for new player data.
- Saved data must still load worlds saved before a change: a field added to a saved codec gets a default (`optionalFieldOf`), and saved ids never change.
- Jade server data sends only what the player could see standing there, and any block scan it runs stays bounded (the Chime Hotel's is 17 by 5 by 17).
- The mod makes no network calls and downloads nothing at runtime, and every dependency is pinned to an exact version (no `+`, `latest`, or `-SNAPSHOT`), the lessons of the [fractureiser](https://github.com/fractureiser-investigation/fractureiser/blob/main/docs/2023-06-08-meeting.md) attack on mod authors.

## Looking things up

Never guess a Minecraft signature or data format. After `:fabric:genSources`, the decompiled sources live in `.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-{common,clientOnly}-*/<version>/*-sources.jar`; read a single file with `unzip -p <jar> net/minecraft/<path>.java`. NeoForge's patched sources are in `neoforge/build/moddev/artifacts/` and its own API in the Gradle cache (`net.neoforged/neoforge/<version>/*-sources.jar`); Forge's are in `~/.gradle/caches/minecraftforge/forgegradle/mavenizer/caches/maven/forge/net/minecraftforge/forge/<version>/`. Vanilla JSON (models, recipes, advancements) to compare against sits in `~/.gradle/caches/fabric-loom/<version>/minecraft-client.jar`. Take version pins from `https://meta.fabricmc.net/v2/versions/`, `https://maven.fabricmc.net/`, `https://maven.neoforged.net/releases/`, and `https://maven.minecraftforge.net/`.

## Art

No AI art ships in the mod. The maintainer draws or traces every texture by hand in Aseprite, based on vanilla textures, and AI may only guide that work: UV templates, labeled guides, palettes, and placeholders (kept out of `art_source/`) that hand-drawn art replaces before release. Never generate a texture meant to ship. The maintainer keeps the sources in `art_source/`; never overwrite a painted texture or commit intermediate art files. Moving a model part's `texOffs` or an element's `uv` changes which texture pixels it reads, so flag it in the pull request.

## Git and releases

Use Conventional Commits, one commit per step per branch. Never push; the maintainer pushes and publishes releases. Jar versions are `<mod_version>+<minecraft_version>` (set in `buildSrc/`), named `fertilegrounds-`, `fertilegrounds-neoforge-`, and `fertilegrounds-forge-`; bump `mod_version` by semver on every branch and release all branch jars together. GitHub releases hold release notes that link to Modrinth and CurseForge, not jars; publish the jars to Modrinth with `.claude/skills/updating-modrinth/SKILL.md`, then to CurseForge with `.claude/skills/updating-curseforge/SKILL.md`.
