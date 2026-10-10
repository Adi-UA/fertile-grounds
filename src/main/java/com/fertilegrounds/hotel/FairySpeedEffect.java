package com.fertilegrounds.hotel;

import com.fertilegrounds.util.ModIdsUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Fairy Speed: 10% faster walking near an occupied hotel. Built like vanilla's Speed, which adds
 * 20% per level with the same operation; this one has a single level, so it never shows a numeral.
 */
public class FairySpeedEffect extends MobEffect {

  /** The movement speed bonus, as a fraction of the total. */
  public static final double SPEED_BONUS = 0.1;

  /** A soft leaf green for the effect's particles. */
  private static final int COLOR = 0x8FD67A;

  public FairySpeedEffect() {
    super(MobEffectCategory.BENEFICIAL, COLOR);
    this.addAttributeModifier(
        Attributes.MOVEMENT_SPEED,
        ModIdsUtil.id("effect.fairy_speed"),
        SPEED_BONUS,
        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
  }
}
