# Tasks

## 1. Lookup

- [ ] 1.1 Register the filled-jar POI type and add `FairyJarLookup`; verify the smoke test places a jar and a test command or debug log finds it within 6 blocks, and not at 10
- [ ] 1.2 Add the cluster leader rule as a pure function with JUnit tests (single jar, pair, three in a row)

## 2. Growth

- [ ] 2.1 Add the jar random tick boost with the `fairy_jar_boost_chance` gamerule; verify a `jar_effects` smoke suite with `random_tick_speed 100` sees wheat near a jar reach age 7 while distant wheat doesn't
- [ ] 2.2 Add the bee extra attempt; verify with a JUnit test of the attempt count, and add "bees near a jar" to the in-game checklist

## 3. Protection

- [ ] 3.1 Add the creeper mixin; verify the smoke suite summons a creeper 5 blocks from a jar and checks it is farther away after 5 seconds
- [ ] 3.2 Add the trample mixin; verify the smoke suite drops a mob 10 blocks onto farmland near a jar (stays farmland) and far from it (becomes dirt)
- [ ] 3.3 Document jar effects in `README.md` and record mixin target signatures in the porting divergence table
