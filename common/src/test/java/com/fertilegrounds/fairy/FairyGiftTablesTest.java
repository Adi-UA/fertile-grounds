package com.fertilegrounds.fairy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Checks the default gift loot tables against the spec's "Default gift tables" requirement. */
class FairyGiftTablesTest {

  /** One row of the spec's table: an item (or "#tag" for a random tag item), count, and weight. */
  private record Row(int min, int max, int weight) {}

  private static final Map<String, Map<String, Row>> SPEC =
      Map.of(
          "common",
          Map.of(
              "minecraft:bone_meal", new Row(3, 6, 25),
              "minecraft:wheat_seeds", new Row(4, 8, 20),
              "#fertilegrounds:fairy_flowers", new Row(1, 3, 20),
              "minecraft:beetroot_seeds", new Row(4, 8, 15),
              "minecraft:pumpkin_seeds", new Row(2, 4, 10),
              "minecraft:melon_seeds", new Row(2, 4, 10)),
          "uncommon",
          Map.of(
              "minecraft:sweet_berries", new Row(4, 8, 30),
              "minecraft:glow_berries", new Row(3, 6, 25),
              "minecraft:cocoa_beans", new Row(3, 6, 25),
              "fertilegrounds:fairy_dust", new Row(1, 1, 20)),
          "rare",
          Map.of(
              "fertilegrounds:fertilizer", new Row(2, 4, 30),
              "minecraft:golden_carrot", new Row(2, 4, 30),
              "minecraft:torchflower_seeds", new Row(1, 2, 20),
              "minecraft:pitcher_pod", new Row(1, 1, 20)));

  @ParameterizedTest
  @ValueSource(strings = {"common", "uncommon", "rare"})
  void tableMatchesTheSpec(final String tier) throws IOException {
    final Path file =
        Path.of(
            "common/src/main/resources/data/fertilegrounds/loot_table/gameplay/fairy_gift/"
                + tier
                + ".json");
    final JsonObject table = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
    final Map<String, Row> found = new HashMap<>();
    for (final JsonElement entry : onlyPoolEntries(table)) {
      final JsonObject e = entry.getAsJsonObject();
      final int weight = e.get("weight").getAsInt();
      if (e.get("type").getAsString().equals("minecraft:loot_table")) {
        // The flower entry: a nested table holding one tag entry, whose "name" omits the "#".
        final JsonObject tag = onlyPoolEntries(e.getAsJsonObject("value")).get(0).getAsJsonObject();
        found.put("#" + tag.get("name").getAsString(), row(tag, weight));
      } else {
        found.put(e.get("name").getAsString(), row(e, weight));
      }
    }
    assertEquals(SPEC.get(tier), found);
  }

  private static JsonArray onlyPoolEntries(final JsonObject table) {
    final var pools = table.getAsJsonArray("pools");
    assertEquals(1, pools.size(), "each gift rolls once on a single pool");
    final JsonObject pool = pools.get(0).getAsJsonObject();
    assertEquals(1, pool.get("rolls").getAsInt());
    return pool.getAsJsonArray("entries");
  }

  private static Row row(final JsonObject entry, final int weight) {
    if (!entry.has("functions")) {
      return new Row(1, 1, weight);
    }
    final JsonObject count =
        entry.getAsJsonArray("functions").get(0).getAsJsonObject().getAsJsonObject("count");
    return new Row(count.get("min").getAsInt(), count.get("max").getAsInt(), weight);
  }
}
