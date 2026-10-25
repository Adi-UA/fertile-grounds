package com.fertilegrounds.gametest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

/** The test-instance files every loader runs, built from the {@link FertileTest} annotations. */
class TestCatalogTest {

  private static final Map<ResourceLocation, JsonObject> INSTANCES = TestCatalog.instances();

  /** Same ids Fabric API's {@code @GameTest} gave, so filters and old reports still match. */
  @Test
  void idsMatchFabricApiNaming() {
    assertEquals(
        "fertilegrounds-gametest:fertilizer_tests_upgrades_sand",
        TestCatalog.id(FertilizerTests.class, method(FertilizerTests.class, "upgradesSand"))
            .toString());
    assertEquals(
        "fertilegrounds-gametest:fairy_speed_tests_player_near_ahotel_gets_ten_percent_then_loses_it_away",
        TestCatalog.id(
                FairySpeedTests.class,
                method(FairySpeedTests.class, "playerNearAHotelGetsTenPercentThenLosesItAway"))
            .toString());
  }

  /** A bare {@code @FertileTest} gets Fabric API's defaults. */
  @Test
  void bareTestGetsDefaults() {
    final JsonObject json =
        INSTANCES.get(
            ResourceLocation.fromNamespaceAndPath(
                TestCatalog.NAMESPACE, "fertilizer_tests_keeps_fertilizer_on_stone"));
    assertEquals("minecraft:function", json.get("type").getAsString());
    assertEquals(
        "fertilegrounds-gametest:fertilizer_tests_keeps_fertilizer_on_stone",
        json.get("function").getAsString());
    assertEquals("minecraft:default", json.get("environment").getAsString());
    assertEquals(TestCatalog.EMPTY_STRUCTURE, json.get("structure").getAsString());
    assertEquals(20, json.get("max_ticks").getAsInt());
    assertEquals(false, json.get("sky_access").getAsBoolean());
  }

  /** Every test class's tests made it into the catalog, so no file is silently overwritten. */
  @Test
  void catalogHasEveryTest() {
    long annotated = 0;
    for (final Class<?> type : TestCatalog.classes()) {
      annotated +=
          java.util.Arrays.stream(type.getDeclaredMethods())
              .filter(method -> method.isAnnotationPresent(FertileTest.class))
              .count();
    }
    assertEquals(annotated, INSTANCES.size());
  }

  private static java.lang.reflect.Method method(final Class<?> type, final String name) {
    try {
      return type.getMethod(name, net.minecraft.gametest.framework.GameTestHelper.class);
    } catch (final NoSuchMethodException e) {
      throw new AssertionError(e);
    }
  }
}
