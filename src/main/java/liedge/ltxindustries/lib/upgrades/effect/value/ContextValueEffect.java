package liedge.ltxindustries.lib.upgrades.effect.value;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.MathOperation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record ContextValueEffect(NumberProvider value, MathOperation operation) implements ValueUpgradeEffect
{
    static final MapCodec<ContextValueEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.fieldOf("value").forGetter(ContextValueEffect::value),
            MathOperation.COMPOUND_OP_CODEC.fieldOf("op").forGetter(ContextValueEffect::operation))
            .apply(instance, ContextValueEffect::new));

    public static ContextValueEffect of(NumberProvider value, MathOperation operation)
    {
        return new ContextValueEffect(value, operation);
    }

    @Override
    public double get(LootContext context, int upgradeRank)
    {
        return value.getFloat(context);
    }

    @Override
    public Type getType()
    {
        return Type.LOOT_CONTEXT;
    }
}