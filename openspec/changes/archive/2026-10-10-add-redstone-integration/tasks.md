# Tasks

## 1. Comparators

- [x] 1.1 Add a `RINGING` state to the Chime Hotel, set when it chimes and cleared by a scheduled tick 20 ticks later; verify a JUnit test of the soil tier to signal mapping and `RedstoneTests` GameTests that sees the state set and cleared
- [x] 1.2 Override `hasAnalogOutputSignal` and the output method on the Chime Hotel and soil blocks (check the 26.3 method names first); verify a GameTest checks a comparator's output signal next to each block, including a pulse after a chime

## 2. Finish

- [x] 2.1 Add "build a harvest alarm: the lamp blinks once for 1 second when the field is ready, not when the hotel is placed" to the in-game checklist
- [x] 2.2 Add a redstone section to `README.md`; verify signal numbers match the tests, and add any comparator method difference to the porting divergence table
