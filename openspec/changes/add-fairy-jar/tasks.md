# Tasks

## 1. Rules

- [x] 1.1 Add `JarredFairyRules` with JUnit tests for every expiry scenario (before, at, and after 3 days; flower pick; empty tag falls back to Poppy)
- [x] 1.2 Add the `jarred_fairy_days` gamerule; verify its default in a GameTest

## 2. Items and blocks

- [ ] 2.1 Ask the user for the Jarred Fairy, Empty Fairy Hotel, and Occupied Fairy Hotel textures and emissive layers (offer UV guides); verify no missing-texture warnings in the `runClient` log
- [x] 2.2 Register the Jarred Fairy item and data component, both hotel blocks and items, loot tables, and lang keys; verify a GameTest places both hotels, reads light levels 5 and 12, and breaking an occupied hotel drops an occupied hotel
- [x] 2.3 Add the hotel recipe; verify a GameTest crafts it with mixed flowers and the recipe loads with no parse error

## 3. Catching and moving in

- [x] 3.1 Add capture in `mobInteract`; verify a GameTest uses a mock player with a bottle on a lingering fairy and gets a Jarred Fairy, and on a flying-in fairy gets the busy message
- [x] 3.2 Add moving in; verify a GameTest uses a Jarred Fairy on an empty hotel, sees an occupied hotel, and gets a bottle back

## 4. Slipping away

- [x] 4.1 Add the inventory, container-open, and item-entity expiry checks with both messages; verify a GameTest advances time with `/time add` and sees a flower in a mock player's inventory, in a chest after opening, and on the ground
- [x] 4.2 `JarredFairyExpiryTests` covers carried, chest, and dropped fairies slipping away with their messages; add the slipping-away look and sound (cherry leaves and a soft Allay throw around the player) to the in-game checklist

## 5. Finish

- [x] 5.1 Add the first catch and first hotel advancements; verify a GameTest earns both through real actions
- [ ] 5.2 Check every row of the Look and sound table in `runClient` and add "catch a fairy and move it into a hotel" to the in-game checklist
- [ ] 5.3 Document hotels in `README.md` and add any 1.21.11 data component, recipe, or container hook difference to the porting divergence table
