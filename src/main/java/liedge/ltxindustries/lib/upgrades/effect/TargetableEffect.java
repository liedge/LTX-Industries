package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;

public record TargetableEffect<T>(EffectTarget source, EffectTarget affected, T effect, Optional<LootItemCondition> condition) implements EffectConditionHolder<T>
{
    public static <T> Codec<TargetableEffect<T>> codec(Codec<T> effectCodec, LootContextParamSet params)
    {
        return RecordCodecBuilder.create(instance -> instance.group(
                EffectTarget.SOURCE_CODEC.fieldOf("source").forGetter(TargetableEffect::source),
                EffectTarget.CODEC.fieldOf("affected").forGetter(TargetableEffect::affected),
                effectCodec.fieldOf("effect").forGetter(TargetableEffect::effect),
                EffectConditionHolder.conditionCodec(params).optionalFieldOf("requirements").forGetter(TargetableEffect::condition))
                .apply(instance, TargetableEffect::new));
    }
}