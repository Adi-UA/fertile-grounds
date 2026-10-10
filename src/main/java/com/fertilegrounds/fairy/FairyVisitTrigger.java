package com.fertilegrounds.fairy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * The {@code fertilegrounds:fairy_visit} advancement trigger: fires for each player near a visit
 * that grows crops. Built like vanilla's simplest triggers (e.g. {@code StartRidingTrigger}), with
 * only the optional {@code player} condition, so datapacks and quest mods can use it.
 */
public class FairyVisitTrigger extends SimpleCriterionTrigger<FairyVisitTrigger.TriggerInstance> {

  @Override
  public Codec<TriggerInstance> codec() {
    return TriggerInstance.CODEC;
  }

  public void trigger(final ServerPlayer player) {
    this.trigger(player, instance -> true);
  }

  public record TriggerInstance(Optional<Holder<LootItemCondition>> player)
      implements SimpleCriterionTrigger.SimpleInstance {

    public static final Codec<TriggerInstance> CODEC =
        RecordCodecBuilder.create(
            i ->
                i.group(
                        LootItemCondition.CODEC
                            .optionalFieldOf("player")
                            .forGetter(TriggerInstance::player))
                    .apply(i, TriggerInstance::new));
  }
}
