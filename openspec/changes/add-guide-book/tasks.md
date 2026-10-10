# Tasks

## 1. Book

- [ ] 1.1 Write the page text as lang keys with the user, covering every coverage item; verify the asset consistency test sees every key
- [ ] 1.2 Add `GuideBook.create()`; verify a GameTest gives a mock player the book and `data get` shows translatable pages

## 2. Delivery

- [ ] 2.1 Add the player attachment, join handler, and `give_guide_book` gamerule; verify a `GuideBookTests` GameTest gives the book once across a rejoin and not when the gamerule is false
- [ ] 2.2 Add the Book + Bone Meal recipe; verify the recipe loads with no parse error and add "craft a new guide" to the in-game checklist
- [ ] 2.3 Play `item.book.page_turn` when the join gift arrives and check it in `runClient`
- [ ] 2.4 Add "read every page" to the in-game checklist and mention the guide in `README.md`
