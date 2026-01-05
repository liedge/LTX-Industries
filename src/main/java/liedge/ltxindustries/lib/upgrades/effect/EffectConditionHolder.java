package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public interface EffectConditionHolder<T> extends Predicate<LootContext>, LootContextUser
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
    default Set<LootContextParam<?>> getReferencedContextParams()
    {
        if (effect() instanceof LootContextUser user)
        {
            return user.getReferencedContextParams();
        }
        else
        {
            return Set.of();
        }
    }

    @Override
    default void validate(ValidationContext context)
    {
        LootContextUser.super.validate(context);
        condition().ifPresent(c -> c.validate(context.forChild(".condition")));
    }
}