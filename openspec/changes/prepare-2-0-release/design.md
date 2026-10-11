# Design

## Context

Branches `1.20.1`, `1.21.1`, `1.21.10`, `1.21.11`, `26.1`, `26.2`, `26.3`, and `main` exist. The roadmap's release steps still list a 1.21.11 backport. The release skills loop over all seven version branches. Sodium (`mc26.3-0.9.2-fabric`) and Iris (`1.11.7+26.3-fabric`) have Fabric releases for 26.3 on Modrinth. Complementary Reimagined supports 26.3 but uses a custom license, so the user downloads it. The real launcher keeps saves in `~/Library/Application Support/minecraft/saves/`.

## Goals / Non-Goals

**Goals:** one version story across every doc and page; a demo world the user can open and record in, with no setup.

**Non-Goals:** shipping shader support in the mod; scripted camera paths or automated recording.

## Decisions

- **Freeze, don't delete, old branches.** They keep their 1.2.0 code and get one README commit each. Modrinth and CurseForge keep their 1.2.0 files; the release skills already forbid deleting versions.
- **Backport order** stays newest first: `main` (26.3) to `26.2`, then `26.1`, following the porting skill. The skill's target list loses 1.21.11; its divergence table keeps the 1.21.11 rows for history.
- **Demo world built by a script, not by hand.** Claude never drives the game UI, so `scripts/demo/build_demo_world.py` writes a dev-only datapack of `.mcfunction` files that place the whole showcase with `fill`, `setblock`, and `item replace`. It runs a headless dev server (a `runDemoServer` Loom run in `run/demo-server/`) on a fresh world, sends commands over RCON bound to 127.0.0.1 with a random password, stops the server, and copies the world folder to the saves directory and to the dev client's `run/saves/`. The dev server runs with `-PnoJade`, since Loom's split classpath leaves the mod's client Jade plugin off a dev server (the shipped jar has it). The world itself is never committed; the script is, so the demo can be rebuilt after changes.
- **Same save for testing.** The datapack stays in the save's `datapacks/` folder as `fgdemo`. The script flattens the whole plot and builds every station, the grove's hillside included, from `fill` and `setblock` commands only, never from seed terrain or random features, so `reset/<name>` clears that station's box and reruns its build function and matches the first build exactly. That beats saving structure templates, which top out at 48 blocks a side and need a structure block per station. The `mode/*` functions only set gamerules and time, so switching between recording and testing takes one command.
- **Scenery.** A fixed seed, and whichever of flower forest, cherry grove, or meadow `/locate biome` finds nearest, gives the backdrop (a meadow for seed `fertilegrounds`). Inside the plot, hand-shaped cherry and birch trees line both edges and a meadow of petals, wildflowers, and firefly bushes fills the gaps between stations, all natural blocks so the grove can still clear them. Stations sit along a gravel and moss path, lit with lanterns, each with a sign. Time starts at late afternoon so the light is warm, and gamerules keep the scene still while recording (`advance_weather false`, `spawn_mobs false`, `fairy_visit_chance 0` so only Fairy Dust brings fairies, on cue).
- **Stations, in path order:** (1) soil rows of wheat on vanilla, Enriched, and Super Enriched farmland, plus bamboo on Enriched Sand; (2) the fairy farm: a 9x9 mixed crop field ringed by ten kinds of flowers, with a beehive; (3) the hotel garden: occupied hotels standing and hanging from a pergola, with a hive and farmland nearby; (4) the Chime Hotel with a nearly ripe field, a comparator, and a redstone lamp; (5) a Fairy Grove shrine on a wild hillside with a clear 35x35 area around it, ready for a diamond; (6) a lectern holding the guide book; (7) supply chests with diamonds, Fairy Dust, Fertilizer, glass bottles, brushes, every hotel, a Jarred Fairy, and Bean Fairy spawn eggs; (8) an empty test lot of flat grass with a pond, as big as a grove.
- **Shaders behind `-Pshaders`.** `localRuntime "maven.modrinth:sodium:<26.3 release>"` and `maven.modrinth:iris:<26.3 release>`, added only when the flag is set, pinned in `gradle.properties`. `run/shaderpacks/` is git-ignored already (under `run/`). If Iris fails in the dev environment, the fallback is the user's real client.
- **Real client.** Copying the 2.0.0 jar and Fabric API into `~/Library/Application Support/minecraft/mods/` changes the user's own install, so Claude asks before doing it.
- **Listing pages** reuse the release skills. The description gets a short "Versions" line: 2.0 for 26.1 to 26.3, and 1.2.0 for older versions. Each new file lists only its own Minecraft version.

## Look and sound

None: no player-facing moment beyond what earlier changes defined. The demo world shows them off.

## Risks / Trade-offs

- [1.21.11 players, 21% of current Fabric players, don't get 2.0] → the pages say so plainly, and 1.2.0 stays available.
- [Iris may not run in a dev environment with other dev mods] → the real client is the fallback, and the demo save works in both.
- [A seed's terrain varies with world generation changes] → the build script flattens its own plot before building, so only the backdrop depends on the seed.
