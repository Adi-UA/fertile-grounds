package com.fertilegrounds.gametest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.gametest.framework.BeforeBatch;
import net.minecraft.gametest.framework.GameTestHelper;
import org.junit.jupiter.api.Test;

/** The tests every loader runs, built from the {@link FertileTest} annotations. */
class TestCatalogTest {

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

  /** No two tests share an id, so none is silently dropped. */
  @Test
  void everyTestHasItsOwnId() {
    final Set<String> ids = new HashSet<>();
    for (final Class<?> type : TestCatalog.classes()) {
      for (final Method method : TestCatalog.testMethods(type)) {
        assertTrue(ids.add(TestCatalog.id(type, method).toString()), "duplicate id " + method);
      }
    }
  }

  /** A batch without a setup would run with whatever the batch before it left behind. */
  @Test
  void everyEnvironmentHasABatchSetup() {
    final Set<String> setups =
        Arrays.stream(TestEnvironments.class.getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(BeforeBatch.class))
            .map(method -> method.getAnnotation(BeforeBatch.class).batch())
            .collect(Collectors.toSet());
    for (final String environment : TestCatalog.environments()) {
      assertTrue(setups.contains(environment), "no @BeforeBatch for " + environment);
    }
  }

  @Test
  void filterMatchesLikeTheBuildSays() {
    final String id = "fertilegrounds-gametest:fertilizer_tests_upgrades_sand";
    assertTrue(TestCatalog.matches("fertilegrounds-gametest:*fertilizer*", id));
    assertTrue(TestCatalog.matches(id, id));
    assertFalse(TestCatalog.matches("fertilegrounds-gametest:fairy*", id));
    assertFalse(TestCatalog.matches("*", id), "* doesn't cross the colon");
  }

  private static Method method(final Class<?> type, final String name) {
    try {
      return type.getMethod(name, GameTestHelper.class);
    } catch (final NoSuchMethodException e) {
      throw new AssertionError(e);
    }
  }
}
