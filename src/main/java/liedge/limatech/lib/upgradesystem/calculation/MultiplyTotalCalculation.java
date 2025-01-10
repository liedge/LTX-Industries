package liedge.limatech.lib.upgradesystem.calculation;

import com.mojang.serialization.MapCodec;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.LevelBasedDoubleValue;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class MultiplyTotalCalculation extends CompoundCalculation.MultiplyOperation
{
    public static final MapCodec<MultiplyTotalCalculation> CODEC = simpleCodec(MultiplyTotalCalculation::new);

    public MultiplyTotalCalculation(LevelBasedDoubleValue value)
    {
        super(value);
    }

    @Override
    public Component getTooltip(int level)
    {
        return LimaTechLang.MULTIPLY_TOTAL_CALCULATION.translateArgs(formatPercentageWithSign(getValue().calculate(level))).withStyle(ChatFormatting.LIGHT_PURPLE);
    }

    @Override
    public CalculationType getType()
    {
        return CalculationType.ADD_MULTIPLIED_TOTAL;
    }
}