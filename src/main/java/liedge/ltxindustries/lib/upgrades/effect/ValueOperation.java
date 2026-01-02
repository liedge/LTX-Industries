package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.MathOperation;
import liedge.ltxindustries.lib.upgrades.value.UpgradeValueProvider;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public record ValueOperation(UpgradeValueProvider value, MathOperation operation)
{
    public static Codec<ValueOperation> codec(LootContextParamSet params)
    {
        return RecordCodecBuilder.create(instance -> instance.group(
                UpgradeValueProvider.codec(params).fieldOf("value").forGetter(ValueOperation::value),
                MathOperation.COMPOUND_OP_CODEC.fieldOf("op").forGetter(ValueOperation::operation))
                .apply(instance, ValueOperation::new));
    }

    public static final Codec<ValueOperation> CONTEXTLESS_CODEC = codec(LootContextParamSets.EMPTY);

    public static ValueOperation of(UpgradeValueProvider value, MathOperation operation)
    {
        return new ValueOperation(value, operation);
    }

    public static ValueOperation of(NumberProvider provider, MathOperation operation)
    {
        return new ValueOperation(UpgradeValueProvider.wrap(provider), operation);
    }

    public double apply(LootContext context, int upgradeRank, double base, double total)
    {
        return operation.applyCompoundingDouble(total, base, value.get(context, upgradeRank));
    }
}