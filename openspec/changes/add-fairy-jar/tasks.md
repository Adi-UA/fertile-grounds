# Tasks

## 1. Rules and state

- [ ] 1.1 Add `FairyJarRules` and `FairyJarState` with JUnit tests for every spec scenario (feeding cap, daily friendship cap, permanence after 4 days, refused feeding, light level, expiry)
- [ ] 1.2 Add the `fairy_jar_days` gamerule; verify its default in the smoke test server

## 2. Blocks and items

- [ ] 2.1 Ask the user for jar textures (filled green lantern, empty Bottle o' Enchanting look); verify no missing-texture warnings in the `runClient` log
- [ ] 2.2 Register Fairy Jar and Empty Fairy Jar blocks, block entity, items, data component, loot tables, and lang keys; verify the smoke test places both and breaking a filled jar drops it with its state
- [ ] 2.3 Add the `GLOW` property, scheduled tick, and chunk-load expiry; verify the smoke test advances time with `/time add` and sees light 10, then 5, then an empty jar

## 3. Catching, feeding, expiry

- [ ] 3.1 Add the `finishedVisit` flag and capture in `mobInteract`; verify the smoke test uses a fake player with a bottle on a finished fairy and gets a jar
- [ ] 3.2 Add flower feeding and permanence with particles and message; verify the smoke test feeds over 4 simulated days and the jar becomes permanent
- [ ] 3.3 Add inventory expiry and the fly-away effect; add "watch a fairy leave its jar" and "carry a jar until it empties" to the in-game checklist
- [ ] 3.4 Add the first jar and permanent jar advancements; verify the smoke test earns both through real actions
- [ ] 3.5 Document jars in `README.md` and add any 1.21.11 data component difference to the porting divergence table
