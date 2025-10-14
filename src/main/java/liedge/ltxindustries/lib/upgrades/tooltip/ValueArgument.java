package liedge.ltxindustries.lib.upgrades.tooltip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.lib.upgrades.effect.value.DoubleLevelBasedValue;
import net.minecraft.network.chat.Component;

public record ValueArgument(DoubleLevelBasedValue value, ValueFormat format, ValueSentiment sentiment, int zero) implements TooltipArgument
{
    static final Codec<ValueArgument> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DoubleLevelBasedValue.CODEC.fieldOf("value").forGetter(ValueArgument::value),
            ValueFormat.CODEC.fieldOf("format").forGetter(ValueArgument::format),
            ValueSentiment.CODEC.optionalFieldOf("sentiment", ValueSentiment.NEUTRAL).forGetter(ValueArgument::sentiment),
            Codec.INT.optionalFieldOf("zero", 0).forGetter(ValueArgument::zero))
            .apply(instance, ValueArgument::new));

    public static ValueArgument of(DoubleLevelBasedValue value, ValueFormat format, ValueSentiment sentiment, int zero)
    {
        return new ValueArgument(value, format, sentiment, zero);
    }

    public static ValueArgument of(DoubleLevelBasedValue value, ValueFormat format, ValueSentiment sentiment)
    {
        return of(value, format, sentiment, 0);
    }

    @Override
    public Component get(int upgradeRank)
    {
        return format.apply(value.calculate(upgradeRank), sentiment, zero);
    }
}