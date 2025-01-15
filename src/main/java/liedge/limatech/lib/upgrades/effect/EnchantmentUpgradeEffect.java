package liedge.limatech.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.client.LimaTechLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record EnchantmentUpgradeEffect(Holder<Enchantment> enchantment, LevelBasedValue amount) implements UpgradeEffect
{
    public static final Codec<EnchantmentUpgradeEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Enchantment.CODEC.fieldOf("enchantment").forGetter(EnchantmentUpgradeEffect::enchantment),
            LevelBasedValue.CODEC.optionalFieldOf("amount", LevelBasedValue.perLevel(1)).forGetter(EnchantmentUpgradeEffect::amount))
            .apply(instance, EnchantmentUpgradeEffect::new));

    public static EnchantmentUpgradeEffect oneLevelPerRank(Holder<Enchantment> enchantment)
    {
        return new EnchantmentUpgradeEffect(enchantment, LevelBasedValue.perLevel(1));
    }

    @SuppressWarnings("deprecation")
    public int calculate(Holder<Enchantment> enchantment, int upgradeRank)
    {
        return this.enchantment.is(enchantment) ? getValue(upgradeRank) : 0;
    }

    private int getValue(int upgradeRank)
    {
        return LimaMathUtil.round(amount.calculate(upgradeRank), LimaMathUtil.RoundingStrategy.FLOOR);
    }

    @Override
    public Component defaultEffectTooltip(int upgradeRank)
    {
        return LimaTechLang.ENCHANTMENT_UPGRADE_EFFECT.translateArgs(enchantment.value().description(), Component.translatable("enchantment.level." + getValue(upgradeRank))).withStyle(ChatFormatting.DARK_PURPLE);
    }
}