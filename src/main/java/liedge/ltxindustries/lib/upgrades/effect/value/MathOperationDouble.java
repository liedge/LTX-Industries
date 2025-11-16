package liedge.ltxindustries.lib.upgrades.effect.value;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.MathOperation;

public record MathOperationDouble(UpgradeDoubleValue left, UpgradeDoubleValue right, MathOperation operation) implements UpgradeDoubleValue
{
    static final MapCodec<MathOperationDouble> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UpgradeDoubleValue.CODEC.fieldOf("left").forGetter(MathOperationDouble::left),
            UpgradeDoubleValue.CODEC.fieldOf("right").forGetter(MathOperationDouble::right),
            MathOperation.SINGLE_OP_CODEC.fieldOf("op").forGetter(MathOperationDouble::operation))
            .apply(instance, MathOperationDouble::new));

    public static MathOperationDouble of(UpgradeDoubleValue left, UpgradeDoubleValue right, MathOperation operation)
    {
        return new MathOperationDouble(left, right, operation);
    }

    @Override
    public double calculate(int upgradeRank)
    {
        return operation.applyAsDouble(left.calculate(upgradeRank), right.calculate(upgradeRank));
    }

    @Override
    public Type getType()
    {
        return Type.MATH_OPERATION;
    }
}