package liedge.ltxindustries.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.registry.game.LTXICriterionTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AcidArmorBrokenTrigger extends SimpleCriterionTrigger<AcidArmorBrokenTrigger.TriggerInstance>
{
    public static final Codec<AcidArmorBrokenTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
            MinMaxBounds.Ints.CODEC.optionalFieldOf("items_broken", MinMaxBounds.Ints.ANY).forGetter(TriggerInstance::itemsBroken))
            .apply(instance, AcidArmorBrokenTrigger.TriggerInstance::new));

    @Override
    public Codec<TriggerInstance> codec()
    {
        return CODEC;
    }

    public void trigger(ServerPlayer player, int itemsBroken)
    {
        this.trigger(player, instance -> instance.itemsBroken.matches(itemsBroken));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, MinMaxBounds.Ints itemsBroken) implements SimpleCriterionTrigger.SimpleInstance
    {
        public static Criterion<TriggerInstance> armorBrokenByAcid(@Nullable ContextAwarePredicate player, MinMaxBounds.Ints itemsBroken)
        {
            return LTXICriterionTriggers.ACID_ARMOR_BROKEN.get().createCriterion(new TriggerInstance(Optional.ofNullable(player), itemsBroken));
        }

        public static Criterion<TriggerInstance> anyArmorBrokenByAcid()
        {
            return armorBrokenByAcid(null, MinMaxBounds.Ints.ANY);
        }
    }
}