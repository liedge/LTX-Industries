package liedge.ltxindustries.lib.upgrades.tooltip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.lib.upgrades.effect.value.UpgradeDoubleValue;
import net.minecraft.network.chat.Component;

public record ValueComponent(UpgradeDoubleValue value, ValueFormat format, ValueSentiment sentiment, int zero) implements UpgradeComponentLike
{
    static final MapCodec<ValueComponent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UpgradeDoubleValue.CODEC.fieldOf("value").forGetter(ValueComponent::value),
            ValueFormat.CODEC.fieldOf("format").forGetter(ValueComponent::format),
            ValueSentiment.CODEC.optionalFieldOf("sentiment", ValueSentiment.NEUTRAL).forGetter(ValueComponent::sentiment),
            Codec.INT.optionalFieldOf("zero", 0).forGetter(ValueComponent::zero))
            .apply(instance, ValueComponent::new));

    public static ValueComponent of(UpgradeDoubleValue value, ValueFormat format, ValueSentiment sentiment, int zero)
    {
        return new ValueComponent(value, format, sentiment, zero);
    }

    public static ValueComponent of(UpgradeDoubleValue value, ValueFormat format, ValueSentiment sentiment)
    {
        return of(value, format, sentiment, 0);
    }

    @Override
    public Component get(int upgradeRank)
    {
        return format.apply(value.calculate(upgradeRank), sentiment, zero);
    }

    @Override
    public Type getType()
    {
        return Type.UPGRADE_VALUE;
    }
}