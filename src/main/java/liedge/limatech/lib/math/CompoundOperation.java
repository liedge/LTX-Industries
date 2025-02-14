package liedge.limatech.lib.math;

import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.Translatable;
import liedge.limatech.LimaTech;
import liedge.limatech.util.LimaTechTooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import static liedge.limatech.util.LimaTechTooltipUtil.*;

public enum CompoundOperation implements StringRepresentable, Translatable
{
    SET("set", (base, total, param) -> param),
    FLAT_ADDITION("flat_addition", (base, total, param) -> total + param),
    ADD_MULTIPLIED_BASE("add_multiplied_base", (base, total, param) -> total + (base * param)),
    ADD_MULTIPLIED_TOTAL("add_multiplied_total", (base, total, param) -> total + (total * param)),
    MULTIPLY("multiply", (base, total, param) -> total * param);

    public static final LimaEnumCodec<CompoundOperation> CODEC = LimaEnumCodec.create(CompoundOperation.class);

    private final String name;
    private final String translationKey;
    private final Operator operator;

    CompoundOperation(String name, Operator operator)
    {
        this.name = name;
        this.translationKey = LimaTech.RESOURCES.translationKey("math_op", "{}", name);
        this.operator = operator;
    }

    public double apply(double base, double total, double param)
    {
        return operator.apply(base, total, param);
    }

    public Component toComponent(double value, boolean invertColor)
    {
        Component valueComponent = switch (this)
        {
            case SET -> flatNumberWithoutSign(value).withStyle(ChatFormatting.GOLD);
            case FLAT_ADDITION -> flatNumberWithSign(value).withStyle(numSignColor(value, invertColor));
            case ADD_MULTIPLIED_BASE, ADD_MULTIPLIED_TOTAL -> LimaTechTooltipUtil.percentageWithSign(value, invertColor);
            case MULTIPLY -> LimaTechTooltipUtil.flatNumberWithoutSign(value).withStyle(numSignColor(value, invertColor));
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

    @FunctionalInterface
    private interface Operator
    {
        double apply(double base, double total, double param);
    }
}