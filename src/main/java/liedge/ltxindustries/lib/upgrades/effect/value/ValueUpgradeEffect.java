package liedge.ltxindustries.lib.upgrades.effect.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.MathOperation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public record ValueUpgradeEffect(UpgradeContextValue value, MathOperation operation)
{
    public static final Codec<ValueUpgradeEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UpgradeContextValue.CODEC.fieldOf("value").forGetter(ValueUpgradeEffect::value),
            MathOperation.COMPOUND_OP_CODEC.fieldOf("op").forGetter(ValueUpgradeEffect::operation))
            .apply(instance, ValueUpgradeEffect::new));

    public static ValueUpgradeEffect of(UpgradeDoubleValue value, MathOperation operation)
    {
        return new ValueUpgradeEffect(UpgradeContextValue.of(value), operation);
    }

    public static ValueUpgradeEffect of(NumberProvider value, MathOperation operation)
    {
        return new ValueUpgradeEffect(UpgradeContextValue.of(value), operation);
    }

    public double apply(LootContext context, int upgradeRank, double base, double total)
    {
        return operation.applyCompoundingDouble(total, base, value.get(context, upgradeRank));
    }
}