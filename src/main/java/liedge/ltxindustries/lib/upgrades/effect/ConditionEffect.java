package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.util.LimaLootUtil;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;

public record ConditionEffect<T>(T effect, Optional<LootItemCondition> condition) implements EffectConditionHolder<T>
{
    public static <T> Codec<ConditionEffect<T>> codec(Codec<T> effectCodec, LootContextParamSet params)
    {
        Codec<ConditionEffect<T>> direct = RecordCodecBuilder.create(instance -> EffectConditionHolder.codecFields(instance, effectCodec).apply(instance, ConditionEffect::new));
        return LimaLootUtil.contextUserCodec(direct, params, "conditional upgrade effect");
    }
}