package liedge.limatech.lib.upgradesystem.calculation;

import com.mojang.serialization.MapCodec;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.LevelBasedDoubleValue;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class MultiplyBaseCalculation extends CompoundCalculation.MultiplyOperation
{
    public static final MapCodec<MultiplyBaseCalculation> CODEC = simpleCodec(MultiplyBaseCalculation::new);

    public MultiplyBaseCalculation(LevelBasedDoubleValue value)
    {
        super(value);
    }

    @Override
    public Component getTooltip(int level)
    {
        return LimaTechLang.MULTIPLY_BASE_CALCULATION.translateArgs(formatPercentageWithSign(getValue().calculate(level))).withStyle(ChatFormatting.AQUA);
    }

    @Override
    public CalculationType getType()
    {
        return CalculationType.ADD_MULTIPLIED_BASE;
    }
}