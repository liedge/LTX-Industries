package liedge.ltxindustries.lib.upgrades.tooltip;

import com.mojang.serialization.Codec;
import liedge.limacore.data.LimaEnumCodec;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;

import java.util.function.DoubleFunction;

public enum ValueFormat implements StringRepresentable
{
    FLAT_NUMBER("flat", LTXITooltipUtil::flatNumberWithoutSign),
    SIGNED_FLAT_NUMBER("signed_flat", LTXITooltipUtil::flatNumberWithSign),
    PERCENTAGE("percentage", LTXITooltipUtil::percentageWithoutSign),
    SIGNED_PERCENTAGE("signed_percentage", LTXITooltipUtil::percentageWithSign),
    MULTIPLICATIVE("multiplicative", val -> Component.literal(LTXITooltipUtil.formatFlatNumber(val) + "x"));

    public static final Codec<ValueFormat> CODEC = LimaEnumCodec.create(ValueFormat.class);

    private final String name;
    private final DoubleFunction<MutableComponent> function;

    ValueFormat(String name, DoubleFunction<MutableComponent> function)
    {
        this.name = name;
        this.function = function;
    }

    public MutableComponent apply(double value, ValueSentiment sentiment, int zero)
    {
        return function.apply(value).withStyle(sentiment.apply(zero, value));
    }

    public MutableComponent apply(double value, ValueSentiment sentiment)
    {
        return apply(value, sentiment, 0);
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}