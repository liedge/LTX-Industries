package liedge.limatech.lib.upgradesystem.calculation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

public enum CalculationType implements StringRepresentable
{
    EMPTY("empty", 99, EmptyCalculation.CODEC),
    OVERRIDE_BASE("override_base", 0, OverrideBaseCalculation.CODEC),
    FLAT_ADDITION("flat_addition", 1, AdditionCalculation.CODEC),
    ADD_MULTIPLIED_ATTRIBUTE("add_multiplied_attribute", 1, LivingAttributeCalculation.CODEC),
    MULTIPLY_BASE_VALUE("multiply_base", 1, MultiplyBaseCalculation.CODEC),
    MULTIPLY_TOTAL_VALUE("multiply_total", 2, MultiplyTotalCalculation.CODEC);

    public static final Codec<CalculationType> CODEC = LimaEnumCodec.createStrict(CalculationType.class);

    private final String name;
    private final int priority;
    private final MapCodec<? extends CompoundCalculation> codec;

    CalculationType(String name, int priority, MapCodec<? extends CompoundCalculation> codec)
    {
        this.name = name;
        this.priority = priority;
        this.codec = codec;
    }

    public MapCodec<? extends CompoundCalculation> getCodec()
    {
        return codec;
    }

    public int getPriority()
    {
        return priority;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}