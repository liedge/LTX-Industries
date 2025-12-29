package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;

public record ConditionEffect<T>(T effect, Optional<LootItemCondition> condition) implements EffectConditionHolder<T>
{
    public static <T> Codec<ConditionEffect<T>> codec(Codec<T> effectCodec, LootContextParamSet params)
    {
        return RecordCodecBuilder.create(instance -> instance.group(
                effectCodec.fieldOf("effect").forGetter(ConditionEffect::effect),
                EffectConditionHolder.conditionCodec(params).optionalFieldOf("requirements").forGetter(ConditionEffect::condition))
                .apply(instance, ConditionEffect::new));
    }
}