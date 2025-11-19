package liedge.ltxindustries.lib.upgrades.effect.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record LinearDouble(double base, double increment) implements UpgradeDoubleValue
{
    static final MapCodec<LinearDouble> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("base").forGetter(LinearDouble::base),
            Codec.DOUBLE.fieldOf("increment").forGetter(LinearDouble::increment))
            .apply(instance, LinearDouble::new));

    public static LinearDouble of(double base, double increment)
    {
        return new LinearDouble(base, increment);
    }

    public static LinearDouble linearIncrement(double baseAndIncrement)
    {
        return of(baseAndIncrement, baseAndIncrement);
    }

    public static LinearDouble oneIncrement(double base)
    {
        return of(base, 1);
    }

    @Override
    public double calculate(int upgradeRank)
    {
        return base + increment * (upgradeRank - 1);
    }

    @Override
    public Type getType()
    {
        return Type.LINEAR;
    }
}