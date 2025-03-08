package liedge.limatech.lib.upgrades.effect.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limatech.lib.CompoundValueOperation;
import net.minecraft.network.chat.Component;

public record SimpleValueUpgradeEffect(DoubleLevelBasedValue value, CompoundValueOperation operation)
{
    public static final Codec<SimpleValueUpgradeEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DoubleLevelBasedValue.CODEC.fieldOf("value").forGetter(SimpleValueUpgradeEffect::value),
            CompoundValueOperation.CODEC.fieldOf("op").forGetter(SimpleValueUpgradeEffect::operation))
            .apply(instance, SimpleValueUpgradeEffect::new));

    public static SimpleValueUpgradeEffect of(DoubleLevelBasedValue value, CompoundValueOperation operation)
    {
        return new SimpleValueUpgradeEffect(value, operation);
    }

    public Component getValueTooltip(int upgradeRank, boolean beneficial)
    {
        return operation.toValueComponent(value.calculate(upgradeRank), beneficial);
    }
}