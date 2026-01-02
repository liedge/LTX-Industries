package liedge.ltxindustries.lib.upgrades.value;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.MathOperation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

import java.util.Set;

public record MathOperationValue(UpgradeValueProvider left, UpgradeValueProvider right, MathOperation operation) implements UpgradeValueProvider
{
    public static final MapCodec<MathOperationValue> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UpgradeValueProvider.DIRECT_CODEC.fieldOf("left").forGetter(MathOperationValue::left),
            UpgradeValueProvider.DIRECT_CODEC.fieldOf("right").forGetter(MathOperationValue::right),
            MathOperation.SINGLE_OP_CODEC.fieldOf("op").forGetter(MathOperationValue::operation))
            .apply(instance, MathOperationValue::new));

    public static MathOperationValue of(UpgradeValueProvider left, UpgradeValueProvider right, MathOperation operation)
    {
        return new MathOperationValue(left, right, operation);
    }

    @Override
    public double get(LootContext context, int upgradeRank)
    {
        return operation.applyAsDouble(left.get(context, upgradeRank), right.get(context, upgradeRank));
    }

    @Override
    public MapCodec<? extends UpgradeValueProvider> codec()
    {
        return CODEC;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return Sets.union(left.getReferencedContextParams(), right.getReferencedContextParams());
    }
}