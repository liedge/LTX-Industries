package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;
import java.util.function.Predicate;

public interface EffectConditionHolder<T> extends Predicate<LootContext>, Validatable
{
    static <T, A extends EffectConditionHolder<T>> Products.P2<RecordCodecBuilder.Mu<A>, T, Optional<LootItemCondition>> codecFields(RecordCodecBuilder.Instance<A> instance, Codec<T> effectCodec)
    {
        return instance.group(
                effectCodec.fieldOf("effect").forGetter(A::effect),
                LootItemCondition.DIRECT_CODEC.optionalFieldOf("requirements").forGetter(A::condition));
    }

    T effect();

    Optional<LootItemCondition> condition();

    @Override
    default boolean test(LootContext context)
    {
        return condition().isEmpty() || condition().get().test(context);
    }

    @Override
    default void validate(ValidationContext context)
    {
        if (effect() instanceof Validatable validatable)
        {
            Validatable.validate(context, "effect", validatable);
        }

        condition().ifPresent(o -> Validatable.validate(context, "condition", o));
    }
}