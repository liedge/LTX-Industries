package liedge.ltxindustries.lib.upgrades.effect.value;

import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.util.StringRepresentable;

public enum ValueSentiment implements StringRepresentable
{
    NEUTRAL("neutral", ChatFormatting.GRAY, ChatFormatting.GRAY),
    POSITIVE("positive", ChatFormatting.RED, ChatFormatting.GREEN),
    NEGATIVE("negative", ChatFormatting.GREEN, ChatFormatting.RED);

    public static final LimaEnumCodec<ValueSentiment> CODEC = LimaEnumCodec.create(ValueSentiment.class);

    private final String name;
    private final ChatFormatting negativeColor;
    private final ChatFormatting positiveColor;

    ValueSentiment(String name, ChatFormatting negativeColor, ChatFormatting positiveColor)
    {
        this.name = name;
        this.negativeColor = negativeColor;
        this.positiveColor = positiveColor;
    }

    public ChatFormatting get(double zero, double value)
    {
        return value < zero ? negativeColor : positiveColor;
    }

    public ChatFormatting get(double value)
    {
        return get(0, value);
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}