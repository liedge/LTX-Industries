package liedge.limatech.lib.upgrades.effect.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limatech.lib.CompoundValueOperation;
import net.minecraft.network.chat.Component;

public record SimpleValueTooltip(DoubleLevelBasedValue value, boolean invertColors) implements ValueEffectTooltip
{
    public static final MapCodec<SimpleValueTooltip> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DoubleLevelBasedValue.CODEC.fieldOf("value").forGetter(SimpleValueTooltip::value),
            Codec.BOOL.optionalFieldOf("invert_color", false).forGetter(SimpleValueTooltip::invertColors))
            .apply(instance, SimpleValueTooltip::new));
    static final Codec<SimpleValueTooltip> FLAT_CODEC = CODEC.codec();

    @Override
    public Component get(int upgradeRank, CompoundValueOperation operation)
    {
        return operation.toValueComponent(value.calculate(upgradeRank), false);
    }

    @Override
    public Type getType()
    {
        return Type.SIMPLE_VALUE;
    }
}