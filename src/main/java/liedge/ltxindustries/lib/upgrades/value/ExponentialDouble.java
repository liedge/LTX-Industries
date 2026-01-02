package liedge.ltxindustries.lib.upgrades.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ExponentialDouble(double base, ContextlessValue power) implements ContextlessValue
{
    public static final MapCodec<ExponentialDouble> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("base").forGetter(ExponentialDouble::base),
            ContextlessValue.CODEC.fieldOf("power").forGetter(ExponentialDouble::power))
            .apply(instance, ExponentialDouble::new));

    public static ExponentialDouble of(double base, ContextlessValue power)
    {
        return new ExponentialDouble(base, power);
    }

    public static ExponentialDouble linearExponent(double base)
    {
        return of(base, LinearDouble.linearIncrement(1));
    }

    public static ExponentialDouble negativeLinearExponent(double base)
    {
        return of(base, LinearDouble.linearIncrement(-1));
    }

    @Override
    public double calculate(int upgradeRank)
    {
        return Math.pow(base, power.calculate(upgradeRank));
    }

    @Override
    public MapCodec<? extends ContextlessValue> codec()
    {
        return CODEC;
    }
}