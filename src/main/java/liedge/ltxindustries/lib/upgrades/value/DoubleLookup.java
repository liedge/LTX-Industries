package liedge.ltxindustries.lib.upgrades.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.doubles.DoubleList;

import java.util.List;

public record DoubleLookup(List<Double> values) implements ContextlessValue
{
    public static final MapCodec<DoubleLookup> CODEC = Codec.DOUBLE.listOf().fieldOf("values").xmap(DoubleLookup::new, DoubleLookup::values);

    public static DoubleLookup lookup(double... values)
    {
        return new DoubleLookup(DoubleList.of(values));
    }

    @Override
    public double calculate(int upgradeRank)
    {
        int index = upgradeRank - 1;

        if (index < 0)
            return values.getFirst();
        else if (index >= values.size())
            return values.getLast();
        else
            return values.get(index);
    }

    @Override
    public MapCodec<? extends ContextlessValue> codec()
    {
        return CODEC;
    }
}