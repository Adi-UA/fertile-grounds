# Tasks

## 1. Lookup

- [x] 1.1 Register the occupied-hotel POI type and add `FairyHotelLookup`; verify a GameTest places a hotel and a test command or debug log finds it within 6 blocks, and not at 10
- [x] 1.2 Add the cluster leader rule as a pure function with JUnit tests (single hotel, pair, three in a row)

## 2. Growth and honey

- [ ] 2.1 Add the hotel random tick boost with the `fairy_hotel_boost_chance` gamerule; verify a `HotelEffectTests` GameTest with `random_tick_speed 100` sees wheat near a hotel reach age 7 while distant wheat doesn't
- [ ] 2.2 Add the bee extra attempt; verify with a JUnit test of the attempt count, and add "bees near a hotel" to the in-game checklist
- [ ] 2.3 Add the honey boost with `fairy_hotel_honey_chance`; verify a GameTest sees a hive next to a hotel gain honey levels and stop at 5

## 3. Petting

- [ ] 3.1 Add the `PETTED` state, brush use, dawn tick, and `fairy_hotel_petted_boost_chance`; verify a GameTest brushes a hotel once (state set, durability used), twice (no change), and after `/time set` past dawn (state cleared)
- [ ] 3.2 Add "brush your hotel fairy each morning" to the in-game checklist

## 4. Fairy Speed

- [ ] 4.1 Register the Fairy Speed effect, the gui atlas alias, the per-player check, and the `fairy_hotel_speed` gamerule; verify a GameTest places a mock player 6 blocks from a hotel and reads the effect, then 20 blocks away and sees it gone within 3 seconds, and a JUnit test of the modifier amount
- [ ] 4.2 Check in `runClient` that the inventory shows vanilla's Speed icon named "Fairy Speed" with no missing-sprite warning; add "walk onto the farm and feel the lift" to the in-game checklist

## 5. Protection

- [ ] 5.1 Add the creeper mixin; verify a GameTest summons a creeper 5 blocks from a hotel and checks it is farther away after 5 seconds
- [ ] 5.2 Add the trample mixin; verify a GameTest drops a mob 10 blocks onto farmland near a hotel (stays farmland) and far from it (becomes dirt)

## 6. Finish

- [ ] 6.1 Check every row of the Look and sound table in `runClient` and add it to the in-game checklist
- [ ] 6.2 Document hotel effects in `README.md` and record mixin target signatures in the porting divergence table
