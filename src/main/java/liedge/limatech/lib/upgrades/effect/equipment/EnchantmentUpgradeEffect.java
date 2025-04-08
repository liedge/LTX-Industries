package liedge.limatech.lib.upgrades.effect.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.upgrades.effect.EffectTooltipProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record EnchantmentUpgradeEffect(Holder<Enchantment> enchantment, LevelBasedValue amount) implements EffectTooltipProvider.SingleLine
{
    public static final Codec<EnchantmentUpgradeEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Enchantment.CODEC.fieldOf("enchantment").forGetter(EnchantmentUpgradeEffect::enchantment),
            LevelBasedValue.CODEC.optionalFieldOf("amount", LevelBasedValue.perLevel(1)).forGetter(EnchantmentUpgradeEffect::amount))
            .apply(instance, EnchantmentUpgradeEffect::new));

    public static EnchantmentUpgradeEffect constantLevel(Holder<Enchantment> enchantment, int level)
    {
        return new EnchantmentUpgradeEffect(enchantment, LevelBasedValue.constant(level));
    }

    public static EnchantmentUpgradeEffect oneLevelPerRank(Holder<Enchantment> enchantment)
    {
        return new EnchantmentUpgradeEffect(enchantment, LevelBasedValue.perLevel(1));
    }

    private int getValue(int upgradeRank)
    {
        return Mth.floor(amount.calculate(upgradeRank));
    }

    public void applyEnchantment(ItemEnchantments.Mutable builder, int upgradeRank)
    {
        builder.upgrade(enchantment, getValue(upgradeRank));
    }

    @Override
    public Component getEffectTooltip(int upgradeRank)
    {
        return LimaTechLang.ENCHANTMENT_UPGRADE_EFFECT.translateArgs(enchantment.value().description(), Component.translatable("enchantment.level." + getValue(upgradeRank))).withStyle(ChatFormatting.DARK_PURPLE);
    }
}