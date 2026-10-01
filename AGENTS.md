# Agent guide for Fertile Grounds

A Fabric mod adding tiered soil blocks that passively bone-meal crops, plus the Bean Fairy, a rare night visitor that fully grows a 5x5 patch of crops. `README.md` describes the gameplay; this file covers how to work on the code.

## Layout

| Path | Job |
|---|---|
| `FertileGrounds.java` | Entry point; only calls each `register()` |
| `block/` | Soil blocks and their registration (`ModBlocks`) |
| `item/` | Fairy Dust, non-block items (`ModItems`), creative tab |
| `entity/` | `BeanFairy` mob, its `VisitCropGoal`, registration (`ModEntities`) |
| `fairy/` | When fairies visit: `FairyTiming` (pure rules), `FairyVisits` (saved list), `FairyVisitScheduler` (tick handler) |
| `util/` | Shared helpers: ids, bone-meal boost, full crop growth |
| `src/client/` | Fairy model and renderer, client registration |
| `src/test/` | JUnit tests for the Minecraft-free logic |
| `scripts/server_smoke_test.py` | Boots a server and checks the fairy in a real world |
| `art_source/` | The user's Aseprite source files and the fairy texture guide |

Keep one job per class. Registration lives in the `Mod*` classes; the entry points only call `register()`. Logic that doesn't need Minecraft types goes in its own class so JUnit can test it without starting the game (see `FairyTiming`). Comments explain Minecraft quirks for someone new to modding, not what the code already says.

## Branches

There's one branch per Minecraft version (the list is in `README.md`), and `main` tracks the newest one. Code stays identical across branches, except where a Minecraft or Fabric API change forces a difference. New features land on the newest branch first, then get backported one version at a time, following `.claude/skills/porting-minecraft-version/SKILL.md`.

## Commands

```bash
./gradlew build                       # compile, spotlessCheck, unit tests
./gradlew spotlessApply               # format Java (google-java-format)
./gradlew test                        # unit tests only
python3 scripts/server_smoke_test.py  # real-world check, exits 1 on failure
./gradlew runClient                   # dev client, for models and textures
./gradlew genSources                  # decompile Minecraft to read real signatures
```

## Verification

A passing `build` only proves compilation. Data files (recipes, advancements, tags) fail silently until a world loads, so also run the smoke test. The dedicated server never loads models or textures, so for those, start `runClient` in the background, grep its log for `Fertile Grounds initializing` and any `WARN`/`ERROR` lines that name `fertilegrounds` or a missing model, then kill it. Hand anything that needs a real player (Fairy Dust, sleeping through the night, how things look) to the user as a short in-game checklist, and say which parts went untested.

## Looking things up

Never guess a Minecraft signature or data format. After `genSources`, the decompiled sources live in `.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-{common,clientOnly}-*/<version>/*-sources.jar`; read a single file with `unzip -p <jar> net/minecraft/<path>.java`. Vanilla JSON (models, recipes, advancements) to compare against sits in `~/.gradle/caches/fabric-loom/<version>/minecraft-client.jar`. Take version pins from `https://meta.fabricmc.net/v2/versions/` and `https://maven.fabricmc.net/`.

## Art

The user paints textures in Aseprite and keeps the sources in `art_source/`; never overwrite a painted texture or commit intermediate art files. `art_source/bean_fairy_guide.png` labels which part of the 32x32 fairy texture maps to which model face. Moving a model part's `texOffs` changes that layout, so tell the user before doing it.

## Git and releases

Use Conventional Commits, one commit per step per branch. Never push; the user pushes and publishes releases. Jar versions are `<mod_version>+<minecraft_version>` (set in `build.gradle`); bump `mod_version` by semver on every branch and release all branch jars together.
