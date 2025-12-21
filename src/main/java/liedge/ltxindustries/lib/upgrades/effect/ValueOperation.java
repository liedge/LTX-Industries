package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.MathOperation;
import liedge.ltxindustries.lib.upgrades.value.UpgradeDoubleValue;
import liedge.ltxindustries.lib.upgrades.value.UpgradeValueProvider;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public record ValueOperation(UpgradeValueProvider value, MathOperation operation)
{
    public static final Codec<ValueOperation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UpgradeValueProvider.CODEC.fieldOf("value").forGetter(ValueOperation::value),
            MathOperation.COMPOUND_OP_CODEC.fieldOf("op").forGetter(ValueOperation::operation))
            .apply(instance, ValueOperation::new));

    public static ValueOperation of(UpgradeDoubleValue value, MathOperation operation)
    {
        return new ValueOperation(value, operation);
    }

    public static ValueOperation of(NumberProvider provider, MathOperation operation)
    {
        return new ValueOperation(UpgradeValueProvider.of(provider), operation);
    }

    public double apply(LootContext context, int rank, double base, double total)
    {
        return operation.applyCompoundingDouble(total, base, value.get(context, rank));
    }
}