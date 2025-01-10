package liedge.limatech.lib.upgradesystem.calculation;

import com.mojang.serialization.MapCodec;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.LevelBasedDoubleValue;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class OverrideBaseCalculation extends CompoundCalculation.FlatOperation
{
    public static final MapCodec<OverrideBaseCalculation> CODEC = simpleCodec(OverrideBaseCalculation::new);

    public OverrideBaseCalculation(LevelBasedDoubleValue value)
    {
        super(value);
    }

    @Override
    public Component getTooltip(int level)
    {
        return LimaTechLang.OVERRIDE_BASE_CALCULATION.translateArgs(formatStandard(getValue().calculate(level))).withStyle(ChatFormatting.GOLD);
    }

    @Override
    public CalculationType getType()
    {
        return CalculationType.OVERRIDE_BASE;
    }
}