package liedge.limatech.lib.upgradesystem.calculation;

import com.mojang.serialization.MapCodec;
import liedge.limatech.lib.LevelBasedDoubleValue;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class AdditionCalculation extends CompoundCalculation.FlatOperation
{
    public static final MapCodec<AdditionCalculation> CODEC = simpleCodec(AdditionCalculation::new);

    public AdditionCalculation(LevelBasedDoubleValue value)
    {
        super(value);
    }

    @Override
    public Component getTooltip(int level)
    {
        return Component.literal(formatStandardWithSign(getValue().calculate(level))).withStyle(ChatFormatting.GREEN);
    }

    @Override
    public CalculationType getType()
    {
        return CalculationType.FLAT_ADDITION;
    }
}