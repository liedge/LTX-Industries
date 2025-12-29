package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import liedge.limacore.util.LimaLootUtil;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;
import java.util.function.Predicate;

public interface EffectConditionHolder<T> extends Predicate<LootContext>
{
    static Codec<LootItemCondition> conditionCodec(LootContextParamSet params)
    {
        return LimaLootUtil.contextUserCodec(LootItemCondition.DIRECT_CODEC, params, "upgrade condition");
    }

    T effect();

    Optional<LootItemCondition> condition();

    @Override
    default boolean test(LootContext context)
    {
        return condition().isEmpty() || condition().get().test(context);
    }
}