package liedge.ltxindustries.lib;

import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.Translatable;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.upgrades.effect.value.ValueSentiment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import static liedge.ltxindustries.util.LTXITooltipUtil.*;

@Deprecated(forRemoval = true)
public enum CompoundValueOperation implements StringRepresentable, Translatable
{
    REPLACE_BASE("replace_base"),
    FLAT_ADDITION("flat_addition"),
    ADD_MULTIPLIED_BASE("add_multiplied_base"),
    ADD_MULTIPLIED_TOTAL("add_multiplied_total"),
    MULTIPLY("multiply");

    public static final LimaEnumCodec<CompoundValueOperation> CODEC = LimaEnumCodec.create(CompoundValueOperation.class);

    private final String name;
    private final String translationKey;

    CompoundValueOperation(String name)
    {
        this.name = name;
        this.translationKey = LTXIndustries.RESOURCES.translationKey("math_op", "{}", name);
    }

    public double computeDouble(double base, double total, double param)
    {
        return switch (this)
        {
            case REPLACE_BASE -> param;
            case FLAT_ADDITION -> total + param;
            case ADD_MULTIPLIED_BASE -> total + (base * param);
            case ADD_MULTIPLIED_TOTAL -> total + (total * param);
            case MULTIPLY -> total * param;
        };
    }

    public Component toValueComponent(double value, ValueSentiment sentiment)
    {
        Component valueComponent = switch (this)
        {
            case REPLACE_BASE -> flatNumberWithoutSign(value).withStyle(ChatFormatting.GOLD);
            case FLAT_ADDITION -> flatNumberWithSign(value).withStyle(sentiment.get(value));
            case ADD_MULTIPLIED_BASE, ADD_MULTIPLIED_TOTAL -> percentageWithSign(value).withStyle(sentiment.get(value));
            case MULTIPLY -> flatNumberWithoutSign(value).withStyle(sentiment.get(1, value));
        };

        return translateArgs(valueComponent);
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    @Override
    public String descriptionId()
    {
        return translationKey;
    }
}