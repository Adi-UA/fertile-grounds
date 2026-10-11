# Agent guide for Fertile Grounds

A Fabric mod adding tiered soil blocks that passively bone-meal crops, plus the Bean Fairy, a rare night visitor that fully grows a 5x5 patch of crops. `README.md` pitches the mod and `docs/DETAILS.md` describes the gameplay in full; this file covers how to work on the code.

## Layout

| Path | Job |
|---|---|
| `FertileGrounds.java` | Entry point; only calls each `register()` |
| `block/` | Soil blocks and their registration (`ModBlocks`) |
| `item/` | Fairy Dust, non-block items (`ModItems`), creative tab |
| `entity/` | `BeanFairy` mob, its `VisitCropGoal`, registration (`ModEntities`) |
| `fairy/` | When fairies visit: `FairyTiming` (pure rules), `FairyVisits` (saved list), `FairyVisitScheduler` (tick handler) |
| `hotel/` | Jarred Fairies and Fairy Hotels: `JarredFairyRules` (pure rules), `JarredFairies` (slipping away in the world), `ChimeRules` and `HotelChimes` (when a Chime Hotel rings); the hotel blocks live in `block/` |
| `grove/` | The Fairy Grove: `GroveLayout` (pure), `GroveShrine` (the shrine check), `FairyGrove` (start and safety scan), `GroveBuilder` (saved jobs, column building, builder fairies) |
| `compat/jade/` | Optional Jade tooltips: server data providers here, tooltip lines in `src/client/.../compat/jade/`; loaded only through the `jade` entrypoint |
| `mixin/` | Small mixins, only where Fabric API has no hook (container opened, item entity tick, creeper goals, farmland trampling) |
| `util/` | Shared helpers: ids, tags (`ModTags`), gamerules (`ModGameRules`), bone-meal boost, full crop growth |
| `src/client/` | Fairy model and renderer, client registration |
| `src/test/` | JUnit tests for the Minecraft-free logic, plus `AssetConsistencyTest` for resource files |
| `src/gametest/` | Fabric GameTests: in-world checks with mock players, one class per feature, with their test environments and structures under `resources/` |
| `scripts/` | `run_gametests.py` runs the GameTests and fails on data-load errors; `client_log_check.py` boots the client and fails on asset warnings |
| `openspec/` | Feature designs (specs with WHEN/THEN scenarios, tasks); `docs/ROADMAP.md` orders them |
| `art_source/` | The user's Aseprite source files and the fairy texture guide |

Keep one job per class. Registration lives in the `Mod*` classes; the entry points only call `register()`. Logic that doesn't need Minecraft types goes in its own class so JUnit can test it without starting the game (see `FairyTiming`). Comments explain Minecraft quirks for someone new to modding, not what the code already says.

## Branches

There's one branch per Minecraft version (the list is in `docs/DETAILS.md`), and `main` tracks the newest one. Code stays identical across branches, except where a Minecraft or Fabric API change forces a difference. New features land on the newest branch first, then get backported one version at a time, following `.claude/skills/porting-minecraft-version/SKILL.md`.

## Commands

```bash
./gradlew build                       # compile, spotlessCheck, unit tests, GameTests
./gradlew spotlessApply               # format Java (google-java-format)
./gradlew test                        # unit tests only
python3 scripts/run_gametests.py      # GameTests plus the data-load error scan, exits 1 on failure
python3 scripts/run_gametests.py --filter 'fairy_dust*'  # only tests whose names match
python3 scripts/client_log_check.py   # boots the client, fails on mod warnings
./gradlew runGameTest -PnoJade        # GameTests without Jade, to check the mod works without it
./gradlew runClient                   # dev client, for models and textures
./gradlew runClient -Pshaders         # dev client with Sodium and Iris; put a shader pack in run/shaderpacks/
./gradlew genSources                  # decompile Minecraft to read real signatures
```

## Verification

`AssetConsistencyTest` catches missing lang keys, models, and textures and outdated recipe JSON. Data files and mob behavior fail silently until a world loads, so each feature adds GameTests in `src/gametest/` (register new classes in its `fabric.mod.json`), and `run_gametests.py` also fails on data that logs a load error without breaking a test. Tests use mock players for anything a player does; tests that move the clock or change gamerules get their own test environment, since tests sharing one run at the same time, and tests that need day or night set clear weather (as `no_random_ticks` does), since rain and thunder darken the sky. The test server never loads models, textures, or sounds, so for changes that touch assets, run `client_log_check.py`. Only looks, sounds, and sleeping through the night (vanilla needs every real player asleep) go to the user as a short in-game checklist; say which parts went untested. Every fixed bug gets a regression check in a GameTest or JUnit, so it can't silently return on another branch.

## Looking things up

Never guess a Minecraft signature or data format. After `genSources`, the decompiled sources live in `.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-{common,clientOnly}-*/<version>/*-sources.jar`; read a single file with `unzip -p <jar> net/minecraft/<path>.java`. Vanilla JSON (models, recipes, advancements) to compare against sits in `~/.gradle/caches/fabric-loom/<version>/minecraft-client.jar`. Take version pins from `https://meta.fabricmc.net/v2/versions/` and `https://maven.fabricmc.net/`.

## Art

No AI art ships in the mod. The user draws or traces every texture by hand in Aseprite, based on vanilla textures, and AI may only guide that work: UV templates, labeled guides, palettes, and placeholders that the user's art replaces before release. Never generate a texture meant to ship. The user keeps the sources in `art_source/`; never overwrite a painted texture or commit intermediate art files. `art_source/bean_fairy_guide.png` labels which part of the 32x32 fairy texture maps to which model face. Moving a model part's `texOffs` changes that layout, so tell the user before doing it.

## Git and releases

Use Conventional Commits, one commit per step per branch. Never push; the user pushes and publishes releases. Jar versions are `<mod_version>+<minecraft_version>` (set in `build.gradle`); bump `mod_version` by semver on every branch and release all branch jars together. Mirror each GitHub release to Modrinth with `.claude/skills/updating-modrinth/SKILL.md`, then to CurseForge with `.claude/skills/updating-curseforge/SKILL.md`.
