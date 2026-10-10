# Tasks

## 1. Rules

- [x] 1.1 Add `GroveLayout` with JUnit tests for the cell counts, the center plot, and every farmland block sitting within 4 blocks of water
- [x] 1.2 Add the `fairy_grove` gamerule; verify its default in a GameTest

## 2. Shrine and safety

- [x] 2.1 Add the `grove_replaceable` tag and `GroveShrine.check`; verify `FairyGroveTests` GameTests where a mock player's diamond on an incomplete shrine (no soil, a missing hotel, too few flowers) keeps the diamond and shows the matching message
- [x] 2.2 Add the safety scan; verify a GameTest with a chest in the area keeps the diamond and names the chest, and one with only natural terrain uses the diamond up

## 3. Building

- [x] 3.1 Add `GroveJobs` and the column builder; verify a GameTest on a small hill builds the full layout (spot checks of farmland moisture, water, hives, hedge leaves persistent, lanterns) and leaves the center plot untouched
- [x] 3.2 Verify GameTests that a chest placed mid-build is left alone, Blighted Dirt in the area is gone, and nothing happens with the gamerule off
- [x] 3.3 Verify building resumes after a reload by saving and reloading the job attachment in a GameTest; add "stop the server mid-build and rejoin" to the in-game checklist
- [x] 3.4 Add the 4 builder fairies; verify a GameTest sees them spawn, never start a visit, and despawn when the job ends

## 4. Polish and docs

- [x] 4.1 Add every row of the Look and sound table; check them and the build's frame rate in `runClient`, and add them, plus "watch a grove build at night", to the in-game checklist
- [x] 4.2 Add a guide book page in the Lexica Botania voice, a Fairy Grove section in `docs/DETAILS.md`, and a README line; verify `AssetConsistencyTest` passes
