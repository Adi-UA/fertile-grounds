# Tasks

## 1. Trades

- [x] 1.1 Find how 26.3 adds Wandering Trader trades (Fabric `TradeOfferHelper` or data-driven trade tables) by reading the Fabric API jar and vanilla sources; note the method in this file and the porting divergence table
  - Found: since 26.1 trades are data. Each trade is a `villager_trade` file (`data/fertilegrounds/villager_trade/wandering_trader/`), and the trader rolls 2 rare trades from the `minecraft:wandering_trader/uncommon` villager trade tag, which the mod appends to. 1.21.11 uses Fabric's `TradeOfferHelper.registerWanderingTraderOffers` with `SELL_SPECIAL_ITEMS_POOL` instead.
- [x] 1.2 Register both trades; verify a `TraderTests` GameTest summons Wandering Traders until one offers a mod trade (or inspects the trade pool through the registration), and add "buy Fairy Dust from a trader" to the in-game checklist
- [x] 1.3 Mention the trades in `README.md`; verify prices match the spec
