package liedge.ltxindustries.lib.upgrades.value;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.MathOperation;

public record MathOperationDouble(ContextlessValue left, ContextlessValue right, MathOperation operation) implements ContextlessValue
{
    public static final MapCodec<MathOperationDouble> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ContextlessValue.CODEC.fieldOf("left").forGetter(MathOperationDouble::left),
            ContextlessValue.CODEC.fieldOf("right").forGetter(MathOperationDouble::right),
            MathOperation.SINGLE_OP_CODEC.fieldOf("op").forGetter(MathOperationDouble::operation))
            .apply(instance, MathOperationDouble::new));

    public static MathOperationDouble of(ContextlessValue left, ContextlessValue right, MathOperation operation)
    {
        return new MathOperationDouble(left, right, operation);
    }

    @Override
    public double calculate(int upgradeRank)
    {
        return operation.applyAsDouble(left.calculate(upgradeRank), right.calculate(upgradeRank));
    }

    @Override
    public MapCodec<? extends ContextlessValue> codec()
    {
        return CODEC;
    }
}