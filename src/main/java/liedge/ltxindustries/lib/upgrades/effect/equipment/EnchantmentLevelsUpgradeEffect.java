package liedge.ltxindustries.lib.upgrades.effect.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.effect.UpgradeTooltipsProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.Comparator;
import java.util.function.Consumer;

public record EnchantmentLevelsUpgradeEffect(Holder<Enchantment> enchantment, LevelBasedValue levels, int maxLevel) implements UpgradeTooltipsProvider
{
    public static final Codec<EnchantmentLevelsUpgradeEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Enchantment.CODEC.fieldOf("enchantment").forGetter(EnchantmentLevelsUpgradeEffect::enchantment),
            LevelBasedValue.CODEC.optionalFieldOf("levels", LevelBasedValue.perLevel(1)).forGetter(EnchantmentLevelsUpgradeEffect::levels),
            Codec.intRange(1, Enchantment.MAX_LEVEL).optionalFieldOf("max_level", Enchantment.MAX_LEVEL).forGetter(EnchantmentLevelsUpgradeEffect::maxLevel))
            .apply(instance, EnchantmentLevelsUpgradeEffect::new));

    public static final Comparator<EnchantmentLevelsUpgradeEffect> DESCENDING_MAX_LEVELS_COMPARATOR = Comparator.comparingInt(EnchantmentLevelsUpgradeEffect::maxLevel).reversed();

    public static EnchantmentLevelsUpgradeEffect fixed(Holder<Enchantment> enchantment, int level, int maxLevel)
    {
        return new EnchantmentLevelsUpgradeEffect(enchantment, LevelBasedValue.constant(level), maxLevel);
    }

    public static EnchantmentLevelsUpgradeEffect fixed(Holder<Enchantment> enchantment, int level)
    {
        return fixed(enchantment, level, Enchantment.MAX_LEVEL);
    }

    public static EnchantmentLevelsUpgradeEffect rankLinear(Holder<Enchantment> enchantment, int maxLevel)
    {
        return new EnchantmentLevelsUpgradeEffect(enchantment, LevelBasedValue.perLevel(1), maxLevel);
    }

    public static EnchantmentLevelsUpgradeEffect rankLinear(Holder<Enchantment> enchantment)
    {
        return rankLinear(enchantment, Enchantment.MAX_LEVEL);
    }

    private int getValue(int upgradeRank)
    {
        return Mth.floor(levels.calculate(upgradeRank));
    }

    public void applyEnchantment(ItemEnchantments.Mutable builder, int upgradeRank)
    {
        int max = maxLevel - builder.getLevel(enchantment);
        int levelsToAdd = Math.min(getValue(upgradeRank), max);
        if (levelsToAdd > 0) builder.upgrade(enchantment, levelsToAdd);
    }

    @Override
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        Component enchantmentName = enchantment.value().description().copy().withStyle(ChatFormatting.DARK_PURPLE);
        Component tooltip = LTXILangKeys.ENCHANTMENT_UPGRADE_EFFECT.translateArgs(getValue(upgradeRank), enchantmentName, Component.literal(Integer.toString(maxLevel)).withStyle(ChatFormatting.YELLOW));
        lines.accept(tooltip);
    }
}