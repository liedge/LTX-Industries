package liedge.ltxindustries.lib.upgrades.effect.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ExponentialDouble(double base, UpgradeDoubleValue power) implements UpgradeDoubleValue
{
    static final MapCodec<ExponentialDouble> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("base").forGetter(ExponentialDouble::base),
            UpgradeDoubleValue.CODEC.fieldOf("power").forGetter(ExponentialDouble::power))
            .apply(instance, ExponentialDouble::new));

    public static ExponentialDouble of(double base, UpgradeDoubleValue power)
    {
        return new ExponentialDouble(base, power);
    }

    public static ExponentialDouble linearExponent(double base)
    {
        return of(base, LinearDouble.of(1));
    }

    public static ExponentialDouble negativeLinearExponent(double base)
    {
        return of(base, LinearDouble.of(-1));
    }

    @Override
    public double calculate(int upgradeRank)
    {
        return Math.pow(base, power.calculate(upgradeRank));
    }

    @Override
    public Type getType()
    {
        return Type.EXPONENTIAL;
    }
}