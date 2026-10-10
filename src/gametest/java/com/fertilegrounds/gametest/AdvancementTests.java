package com.fertilegrounds.gametest;

import com.fertilegrounds.util.ModIdsUtil;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerAdvancementManager;

/**
 * The mod's advancements, and the test data's advancements on each custom trigger, all load. An
 * unknown trigger or a broken file makes the advancement loader skip that file with only a log
 * line.
 */
public class AdvancementTests {

  @GameTest
  public void everyAdvancementLoads(final GameTestHelper helper) {
    final ServerAdvancementManager advancements = helper.getLevel().getServer().getAdvancements();
    Stream.concat(
            Stream.of(
                    "root",
                    "first_fairy_visit",
                    "use_fertilizer",
                    "first_fairy_gift",
                    "harvest_moon_visit")
                .map(ModIdsUtil::id),
            Stream.of("fairy_visit", "fairy_gift", "harvest_moon_visit")
                .map(path -> Identifier.fromNamespaceAndPath("fertilegrounds-gametest", path)))
        .forEach(id -> helper.assertTrue(advancements.get(id) != null, "advancement " + id));
    helper.succeed();
  }
}
