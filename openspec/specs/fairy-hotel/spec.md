# fairy-hotel Specification

## Purpose
Defines how players catch a Bean Fairy, how long a caught fairy waits in its bottle, and how a Fairy Hotel gives it a home for good.

## Requirements

### Requirement: Catching a fairy
Using a Glass Bottle on a Bean Fairy that has finished growing its patch SHALL remove the fairy, use one bottle, and give the player a Jarred Fairy. Using one on a fairy that has not finished SHALL do nothing and show a translated "busy" message.

#### Scenario: Catch while lingering
- **WHEN** a sneaking player uses a Glass Bottle on a fairy lingering over its grown patch
- **THEN** the fairy disappears and the player gets a Jarred Fairy

#### Scenario: Fairy still working
- **WHEN** a player uses a Glass Bottle on a fairy still flying to its crop
- **THEN** nothing is caught, the bottle stays, and the busy message shows

### Requirement: Jarred Fairy slips away
A Jarred Fairy SHALL last `fertilegrounds:jarred_fairy_days` in-game days from capture (default 3), counting in-game time whether carried, stored, or dropped. When the time runs out, it SHALL become one random item from `#fertilegrounds:fairy_flowers`. A Jarred Fairy SHALL have no other effect.

#### Scenario: Carried too long
- **WHEN** a player carries a Jarred Fairy for 3 in-game days
- **THEN** it becomes a random flower in the same slot, and the player sees "Your Bean Fairy slipped away and left a <flower> behind." in the action bar

#### Scenario: Forgotten in a chest
- **WHEN** a player stores a Jarred Fairy in a chest and opens the chest 4 in-game days later
- **THEN** the slot holds a random flower, and the player sees "A Bean Fairy slipped away while you were gone and left a <flower>." in the action bar

#### Scenario: Dropped on the ground
- **WHEN** a Jarred Fairy lies on the ground as its time runs out
- **THEN** the item entity becomes a random flower, and no message is shown

### Requirement: Building a Fairy Hotel
The game SHALL craft one Empty Fairy Hotel from a shaped recipe: a Firefly Bush in the top middle, a Lantern in the center, and a `#fertilegrounds:fairy_flowers` item in each of the other 7 slots. Any mix of fairy flowers SHALL work.

#### Scenario: Mixed flowers
- **WHEN** a player fills the 7 flower slots with poppies and dandelions around a lantern and firefly bush
- **THEN** the crafting output is an Empty Fairy Hotel

### Requirement: Moving in
Using a Jarred Fairy on a placed Empty Fairy Hotel SHALL turn it into an Occupied Fairy Hotel, use the Jarred Fairy, and give back a Glass Bottle. An Occupied Fairy Hotel SHALL never lose its fairy.

#### Scenario: New home
- **WHEN** a player uses a Jarred Fairy with 1 day left on an Empty Fairy Hotel
- **THEN** the hotel lights up with its fairy, the player gets a Glass Bottle back, and 100 in-game days later the fairy is still there

### Requirement: Light
An Empty Fairy Hotel SHALL emit light level 5. An Occupied Fairy Hotel SHALL emit light level 12.

#### Scenario: Night on the farm
- **WHEN** a player places one hotel of each kind in the dark
- **THEN** the empty one gives light level 5 and the occupied one gives 12

### Requirement: Hotels are portable
Breaking an Empty Fairy Hotel SHALL drop an Empty Fairy Hotel. Breaking an Occupied Fairy Hotel SHALL drop an Occupied Fairy Hotel item, which places back as an occupied hotel with its fairy.

#### Scenario: Moving the farm
- **WHEN** a player breaks an occupied hotel, stores it in a chest for 10 in-game days, and places it elsewhere
- **THEN** it is still an occupied hotel with its fairy
