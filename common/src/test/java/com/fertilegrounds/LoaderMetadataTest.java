package com.fertilegrounds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 * Each loader's jar describes the mod in its own metadata file. Players see these in their mod
 * list, so they have to agree, and each has to accept only the branch's Minecraft version.
 */
class LoaderMetadataTest {

  private static final Path FABRIC = Path.of("fabric/src/main/resources/fabric.mod.json");

  /** Each mods.toml file and the key it names the mod's icon with. */
  private static final Map<Path, String> TOML =
      Map.of(
          Path.of("neoforge/src/main/resources/META-INF/neoforge.mods.toml"), "iconFile",
          Path.of("forge/src/main/resources/META-INF/mods.toml"), "logoFile");

  @Test
  void everyLoaderDescribesTheModTheSameWay() throws IOException {
    final JsonObject fabric = JsonParser.parseString(Files.readString(FABRIC)).getAsJsonObject();
    for (final Map.Entry<Path, String> entry : TOML.entrySet()) {
      final Path file = entry.getKey();
      final String toml = Files.readString(file);
      final String where = file + " vs " + FABRIC;
      assertEquals(fabric.get("id").getAsString(), value(toml, "modId"), "mod id, " + where);
      assertEquals(fabric.get("name").getAsString(), value(toml, "displayName"), "name, " + where);
      assertEquals(
          fabric.get("description").getAsString(),
          value(toml, "description"),
          "description, " + where);
      assertEquals(
          fabric.get("license").getAsString(), value(toml, "license"), "license, " + where);
      assertEquals(
          fabric.get("icon").getAsString(), value(toml, entry.getValue()), "icon, " + where);
    }
  }

  /**
   * Fabric's {@code ~26.3} and the range the other loaders read both start at the branch's version.
   */
  @Test
  void everyLoaderTargetsTheBranchVersion() throws IOException {
    final Properties gradle = new Properties();
    gradle.load(new StringReader(Files.readString(Path.of("gradle.properties"))));
    final String minecraft = gradle.getProperty("minecraft_version");
    final JsonObject fabric = JsonParser.parseString(Files.readString(FABRIC)).getAsJsonObject();
    assertEquals(
        "~" + minecraft,
        fabric.getAsJsonObject("depends").get("minecraft").getAsString(),
        "Minecraft version in " + FABRIC);
    assertTrue(
        gradle.getProperty("minecraft_version_range").startsWith("[" + minecraft + ","),
        "minecraft_version_range in gradle.properties starts at " + minecraft);
    for (final Path file : TOML.keySet()) {
      assertTrue(
          Files.readString(file).contains("versionRange = \"${minecraft_version_range}\""),
          file + " reads its Minecraft range from gradle.properties");
    }
  }

  /** A top-level {@code key = "value"} or {@code key = '''value'''} from a mods.toml file. */
  private static String value(final String toml, final String key) {
    final Matcher matcher =
        Pattern.compile("(?m)^" + key + "\\s*=\\s*(?:'''(.*?)'''|\"(.*?)\")", Pattern.DOTALL)
            .matcher(toml);
    assertTrue(matcher.find(), "no " + key + " in mods.toml");
    return matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
  }
}
