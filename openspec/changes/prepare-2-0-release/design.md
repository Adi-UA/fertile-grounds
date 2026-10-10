# Design

## Context

Branches `1.20.1`, `1.21.1`, `1.21.10`, `1.21.11`, `26.1`, `26.2`, `26.3`, and `main` exist. The roadmap's release steps still list a 1.21.11 backport. The release skills loop over all seven version branches. Sodium (`mc26.3-0.9.2-fabric`) and Iris (`1.11.7+26.3-fabric`) have Fabric releases for 26.3 on Modrinth. Complementary Reimagined supports 26.3 but uses a custom license, so the user downloads it. The real launcher keeps saves in `~/Library/Application Support/minecraft/saves/`.

## Goals / Non-Goals

**Goals:** one version story across every doc and page; a demo world the user can open and record in, with no setup.

**Non-Goals:** shipping shader support in the mod; scripted camera paths or automated recording.

## Decisions

- **Freeze, don't delete, old branches.** They keep their 1.2.0 code and get one README commit each. Modrinth and CurseForge keep their 1.2.0 files; the release skills already forbid deleting versions.
- **Backport order** stays newest first: `main` (26.3) to `26.2`, then `26.1`, following the porting skill. The skill's target list loses 1.21.11; its divergence table keeps the 1.21.11 rows for history.
- **Demo world built by a script, not by hand.** Claude never drives the game UI, so `scripts/demo/build_demo_world.py` writes a dev-only datapack of `.mcfunction` files that place the whole showcase with `fill`, `setblock`, and `item replace`. It runs a headless dev server (`runServer`) on a fresh world, applies the datapack from the server console, stops the server, and copies the world folder to the saves directory. The world itself is never committed; the script is, so the demo can be rebuilt after changes.
- **Same save for testing.** The datapack stays in the save's `datapacks/` folder as `fgdemo`. Each station is also saved as a structure template (`fgdemo:station/<name>`) right after the script builds it, and `reset/<name>` places it back with `/place template`, so resets match the first build exactly, even for the 35x35 grove area that `/clone` can't copy in one go (its 32,768-block limit). The `mode/*` functions only set gamerules and time, so switching between recording and testing takes one command.
- **Scenery.** A fixed seed with a cherry grove or flower forest, found with `/locate biome` on the headless server, gives a pretty backdrop. Stations sit along a gravel and moss path, lit with lanterns, each with a sign. Time starts at late afternoon so the light is warm, and gamerules keep the scene still while recording (`doWeatherCycle false`, `doMobSpawning false`, `fairy_visit_chance 0` so only Fairy Dust brings fairies, on cue).
- **Stations, in path order:** (1) soil rows of wheat on vanilla, Enriched, and Super Enriched farmland, plus bamboo on Enriched Sand; (2) the fairy farm: a 9x9 mixed crop field ringed by ten kinds of flowers, with a beehive; (3) the hotel garden: occupied hotels standing and hanging from a pergola, with a hive and farmland nearby; (4) the Chime Hotel with a nearly ripe field, a comparator, and a redstone lamp; (5) a blighted plot: a small wheat field with one Blighted Dirt block in it, ringed by a dirt path so the blight can't reach other stations; (6) a Fairy Grove shrine on a wild hillside with a clear 35x35 area around it, ready for a diamond; (7) a lectern holding the guide book; (8) supply chests with diamonds, Fairy Dust, Fertilizer, glass bottles, brushes, every hotel, a Jarred Fairy, spawn eggs, and a Bean Fairy spawn egg.
- **Shaders behind `-Pshaders`.** `localRuntime "maven.modrinth:sodium:<26.3 release>"` and `maven.modrinth:iris:<26.3 release>`, added only when the flag is set, pinned in `gradle.properties`. `run/shaderpacks/` is git-ignored already (under `run/`). If Iris fails in the dev environment, the fallback is the user's real client.
- **Real client.** Copying the 2.0.0 jar and Fabric API into `~/Library/Application Support/minecraft/mods/` changes the user's own install, so Claude asks before doing it.
- **Listing pages** reuse the release skills. The description gets a short "Versions" line: 2.0 for 26.1 to 26.3, and 1.2.0 for older versions. Each new file lists only its own Minecraft version.

## Look and sound

None: no player-facing moment beyond what earlier changes defined. The demo world shows them off.

## Risks / Trade-offs

- [1.21.11 players, 21% of current Fabric players, don't get 2.0] → the pages say so plainly, and 1.2.0 stays available.
- [Iris may not run in a dev environment with other dev mods] → the real client is the fallback, and the demo save works in both.
- [A seed's terrain varies with world generation changes] → the build script flattens its own plot before building, so only the backdrop depends on the seed.
