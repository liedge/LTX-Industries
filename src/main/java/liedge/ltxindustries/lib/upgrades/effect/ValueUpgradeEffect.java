package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.MathOperation;
import liedge.ltxindustries.lib.upgrades.value.UpgradeDoubleValue;
import liedge.ltxindustries.lib.upgrades.value.UpgradeValueProvider;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public record ValueUpgradeEffect(UpgradeValueProvider value, MathOperation operation)
{
    public static final Codec<ValueUpgradeEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UpgradeValueProvider.CODEC.fieldOf("value").forGetter(ValueUpgradeEffect::value),
            MathOperation.COMPOUND_OP_CODEC.fieldOf("op").forGetter(ValueUpgradeEffect::operation))
            .apply(instance, ValueUpgradeEffect::new));

    public static ValueUpgradeEffect of(UpgradeDoubleValue value, MathOperation operation)
    {
        return new ValueUpgradeEffect(value, operation);
    }

    public static ValueUpgradeEffect of(NumberProvider provider, MathOperation operation)
    {
        return new ValueUpgradeEffect(UpgradeValueProvider.of(provider), operation);
    }

    public double apply(LootContext context, int rank, double base, double total)
    {
        return operation.applyCompoundingDouble(total, base, value.get(context, rank));
    }
}