package liedge.ltxindustries.lib.upgrades.effect.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public record ConstantDouble(double value) implements UpgradeDoubleValue
{
    static final Codec<ConstantDouble> INLINE_CODEC = Codec.DOUBLE.xmap(ConstantDouble::new, ConstantDouble::value);
    static final MapCodec<ConstantDouble> CODEC = INLINE_CODEC.fieldOf("value");

    public static ConstantDouble of(double value)
    {
        return new ConstantDouble(value);
    }

    @Override
    public double calculate(int upgradeRank)
    {
        return value;
    }

    @Override
    public Type getType()
    {
        return Type.CONSTANT;
    }
}