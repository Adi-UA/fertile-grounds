# Tasks

## 1. Lookup

- [x] 1.1 Register the occupied-hotel POI type and add `FairyHotelLookup`; verify a GameTest places a hotel and a test command or debug log finds it within 6 blocks, and not at 10
- [x] 1.2 Add the cluster leader rule as a pure function with JUnit tests (single hotel, pair, three in a row)

## 2. Growth and honey

- [x] 2.1 Add the hotel random tick boost with the `fairy_hotel_boost_chance` gamerule; verify a `HotelEffectTests` GameTest with `random_tick_speed 100` sees wheat near a hotel reach age 7 while distant wheat doesn't
- [x] 2.2 Add the bee extra attempt; verify with a JUnit test of the attempt count, and add "bees near a hotel" to the in-game checklist
- [x] 2.3 Add the honey boost with `fairy_hotel_honey_chance`; verify a GameTest sees a hive next to a hotel gain honey levels and stop at 5

## 3. Petting

- [x] 3.1 Add the `PETTED` state, brush use, dawn tick, and `fairy_hotel_petted_boost_chance`; verify a GameTest brushes a hotel once (state set, durability used), twice (no change), and after dawn (state cleared; the test starts the clock just before dawn, since the dawn tick counts real ticks and a later `/time set` doesn't move it)
- [x] 3.2 `HotelEffectTests` covers the once-a-day rule; add the petting look and sound (hearts, a glow burst, brushing then an amethyst chime) to the in-game checklist

## 4. Fairy Speed

- [x] 4.1 Register the Fairy Speed effect, the gui atlas alias, the per-player check, and the `fairy_hotel_speed` gamerule; verify `FairySpeedTests` places a mock player 6 blocks from a hotel and reads the effect and exactly 10% more movement speed, then 20 blocks away and sees it gone within 3 seconds, that three hotels still give 10%, and that the gamerule turns it off (the speed check replaces a JUnit test, since a `MobEffect` can't be built outside the game)
- [x] 4.2 Verify `client_log_check.py` shows no warnings with the gui atlas alias; add to the in-game checklist: the inventory and HUD show vanilla's Speed icon named "Fairy Speed" with a beacon-style border, and "walk onto the farm and feel the lift" (the HUD only asks for the icon in game, so the log check can't prove it draws)

## 5. Protection

- [x] 5.1 Add the creeper mixin; verify a GameTest summons a creeper 5 blocks from a hotel and checks it is farther away after 5 seconds
- [x] 5.2 Add the trample mixin; verify a GameTest drops a mob 10 blocks onto farmland near a hotel (stays farmland) and far from it (becomes dirt)

## 6. Finish

- [ ] 6.1 Check every row of the Look and sound table in `runClient` and add it to the in-game checklist
- [ ] 6.2 Document hotel effects in `README.md` and record mixin target signatures in the porting divergence table
