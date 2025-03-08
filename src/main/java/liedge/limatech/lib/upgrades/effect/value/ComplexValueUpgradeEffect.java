package liedge.limatech.lib.upgrades.effect.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limatech.lib.CompoundValueOperation;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record ComplexValueUpgradeEffect(NumberProvider value, CompoundValueOperation operation, ComplexValueTooltip tooltip)
{
    public static final Codec<ComplexValueUpgradeEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            NumberProviders.CODEC.fieldOf("value").forGetter(ComplexValueUpgradeEffect::value),
            CompoundValueOperation.CODEC.fieldOf("op").forGetter(ComplexValueUpgradeEffect::operation),
            ComplexValueTooltip.CODEC.fieldOf("tooltip").forGetter(ComplexValueUpgradeEffect::tooltip))
            .apply(instance, ComplexValueUpgradeEffect::new));

    public static ComplexValueUpgradeEffect simpleConstant(float flatValue, CompoundValueOperation operation)
    {
        return new ComplexValueUpgradeEffect(ConstantValue.exactly(flatValue), operation, new ComplexValueTooltip.SimpleTooltip(LevelBasedValue.constant(flatValue)));
    }

    public static ComplexValueUpgradeEffect simpleRankBased(LevelBasedValue value, CompoundValueOperation operation)
    {
        return new ComplexValueUpgradeEffect(EnchantmentLevelProvider.forEnchantmentLevel(value), operation, new ComplexValueTooltip.SimpleTooltip(value));
    }

    public static ComplexValueUpgradeEffect of(NumberProvider value, CompoundValueOperation operation, ComplexValueTooltip tooltip)
    {
        return new ComplexValueUpgradeEffect(value, operation, tooltip);
    }
}