# Tasks

## 1. Book

- [x] 1.1 Write the page text as lang keys covering every coverage item (the user reviews the text in the end-of-run read-through); verify the asset consistency test sees every key
- [x] 1.2 Add `GuideBook.create()`; verify a GameTest gives a mock player the book and checks its pages are translatable

## 2. Delivery

- [x] 2.1 Add the player attachment, join handler, and `give_guide_book` gamerule; verify a `GuideBookTests` GameTest gives the book once across a rejoin and not when the gamerule is false
- [x] 2.2 Add the Book + Bone Meal recipe; verify a GameTest crafts it and gets the same book the join gift gives
- [x] 2.3 Play `item.book.page_turn` when the join gift arrives and add it to the in-game checklist
- [x] 2.4 Add "read every page" to the in-game checklist and mention the guide in `README.md`
