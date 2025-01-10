package liedge.limatech.lib.upgradesystem.calculation;

import com.mojang.serialization.MapCodec;
import liedge.limatech.lib.LevelBasedDoubleValue;
import net.minecraft.network.chat.Component;

public final class EmptyCalculation extends CompoundCalculation.FlatOperation
{
    private static final EmptyCalculation INSTANCE = new EmptyCalculation();

    public static final MapCodec<EmptyCalculation> CODEC = MapCodec.unit(INSTANCE);

    public static EmptyCalculation empty()
    {
        return INSTANCE;
    }

    private EmptyCalculation()
    {
        super(LevelBasedDoubleValue.constant(0d));
    }

    @Override
    public Component getTooltip(int level)
    {
        return Component.empty();
    }

    @Override
    public CalculationType getType()
    {
        return CalculationType.EMPTY;
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }
}