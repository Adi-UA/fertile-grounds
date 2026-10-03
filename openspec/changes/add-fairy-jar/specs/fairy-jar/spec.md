# Spec Delta

## Purpose

Lets players keep a Bean Fairy in a jar as a glowing companion, at the cost of daily care, with permanence earned through friendship rather than trapping.

## ADDED Requirements

### Requirement: Catching a fairy
Using a Glass Bottle or an Empty Fairy Jar on a Bean Fairy that has finished growing its patch SHALL remove the fairy, use one bottle or empty jar, and give the player a Fairy Jar with `fertilegrounds:fairy_jar_days` days of life (default 7). Using one on a fairy that has not finished SHALL do nothing and show a translated "busy" message.

#### Scenario: Catch after a visit
- **WHEN** a player uses a Glass Bottle on a fairy flying away from a grown patch
- **THEN** the fairy disappears and the player gets a Fairy Jar with 7 days left

#### Scenario: Fairy still working
- **WHEN** a player uses a Glass Bottle on a fairy still flying to its crop
- **THEN** nothing is caught, the bottle stays, and the busy message shows

### Requirement: Lifespan counts down everywhere
A Fairy Jar's remaining life SHALL count down in in-game time whether the jar is placed, stored in a container, or carried. Breaking a placed jar SHALL drop it with its remaining life and friendship progress intact.

#### Scenario: Stored in a chest
- **WHEN** a player stores a Fairy Jar with 3 days left in a chest and returns 2 in-game days later
- **THEN** the jar has 1 day left

#### Scenario: Pick up and replace
- **WHEN** a player breaks a placed jar with 5 days left and 3 counted flowers and places it again
- **THEN** it still has 5 days left and 3 counted flowers

### Requirement: Light
A placed Fairy Jar SHALL emit light level 10 while more than 1 day is left and light level 5 during its last day. A permanent jar SHALL always emit 10. An Empty Fairy Jar SHALL emit no light.

#### Scenario: Last day
- **WHEN** a placed jar drops below 1 day left
- **THEN** its light level becomes 5

### Requirement: Feeding adds days
Using a `#fertilegrounds:fairy_flowers` item on a Fairy Jar that isn't permanent SHALL add 1 day, capped at `fertilegrounds:fairy_jar_days`, and SHALL use the flower.

#### Scenario: Top up
- **WHEN** a player feeds a poppy to a jar with 2.5 days left
- **THEN** the jar has 3.5 days left and the poppy is used

### Requirement: Friendship makes a jar permanent
Each fed flower SHALL count toward friendship, with at most 2 counted per in-game day. When 8 flowers have counted, the jar SHALL become permanent: it never expires, keeps full light, and the player sees particles and a translated message. Feeding SHALL be refused, keeping the flower, when the jar is at full days and today's friendship count is used up.

#### Scenario: Two days of feeding is not enough
- **WHEN** a player feeds 8 flowers across 2 in-game days
- **THEN** 4 flowers count and the jar is not permanent

#### Scenario: Earned permanence
- **WHEN** a player feeds 2 flowers on each of 4 different in-game days
- **THEN** the jar becomes permanent on the 8th counted flower

#### Scenario: Any flowers count
- **WHEN** all 8 counted flowers are poppies
- **THEN** the jar still becomes permanent

#### Scenario: Refused feeding
- **WHEN** a jar has 7 days left and 2 flowers already counted today
- **THEN** feeding a third flower that day does nothing and the flower stays in the player's hand

### Requirement: Expiry
When a jar's life reaches zero, the fairy SHALL leave with no gift. A placed jar SHALL become an Empty Fairy Jar block, showing the fairy flying up and away if a player is within 32 blocks. A carried or stored jar SHALL become an Empty Fairy Jar item. A jar in an unloaded chunk SHALL become empty when the chunk next loads.

#### Scenario: Forgotten placed jar
- **WHEN** a placed jar's life runs out while a player stands nearby
- **THEN** the player sees the fairy fly away, and the block becomes an Empty Fairy Jar with no light

#### Scenario: Permanent jar never expires
- **WHEN** 100 in-game days pass for a permanent jar
- **THEN** it still holds its fairy

### Requirement: Empty Fairy Jar
An Empty Fairy Jar SHALL be placeable as a decorative block, SHALL drop itself when broken, and SHALL catch a fairy the same way a Glass Bottle does.

#### Scenario: Reuse
- **WHEN** a player uses an Empty Fairy Jar on a fairy that finished its patch
- **THEN** the player gets a Fairy Jar with 7 days left and the empty jar is used
