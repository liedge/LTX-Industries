package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.tooltip.UpgradeTooltipsProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.Comparator;
import java.util.function.Consumer;

public record AddEnchantmentLevels(Holder<Enchantment> enchantment, LevelBasedValue levels, int maxLevel) implements UpgradeTooltipsProvider
{
    public static final Codec<AddEnchantmentLevels> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Enchantment.CODEC.fieldOf("enchantment").forGetter(AddEnchantmentLevels::enchantment),
            LevelBasedValue.CODEC.optionalFieldOf("levels", LevelBasedValue.perLevel(1)).forGetter(AddEnchantmentLevels::levels),
            Codec.intRange(1, Enchantment.MAX_LEVEL).optionalFieldOf("max_level", Enchantment.MAX_LEVEL).forGetter(AddEnchantmentLevels::maxLevel))
            .apply(instance, AddEnchantmentLevels::new));

    public static final Comparator<AddEnchantmentLevels> DESCENDING_MAX_LEVELS_COMPARATOR = Comparator.comparingInt(AddEnchantmentLevels::maxLevel).reversed();

    public static AddEnchantmentLevels fixed(Holder<Enchantment> enchantment, int level, int maxLevel)
    {
        return new AddEnchantmentLevels(enchantment, LevelBasedValue.constant(level), maxLevel);
    }

    public static AddEnchantmentLevels fixed(Holder<Enchantment> enchantment, int level)
    {
        return fixed(enchantment, level, Enchantment.MAX_LEVEL);
    }

    public static AddEnchantmentLevels rankLinear(Holder<Enchantment> enchantment, int maxLevel)
    {
        return new AddEnchantmentLevels(enchantment, LevelBasedValue.perLevel(1), maxLevel);
    }

    public static AddEnchantmentLevels rankLinear(Holder<Enchantment> enchantment)
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
        int levels = getValue(upgradeRank);

        Component tooltip = maxLevel == Enchantment.MAX_LEVEL
                ? LTXILangKeys.ENCHANTMENT_UPGRADE_EFFECT.translateArgs(levels, enchantmentName)
                : LTXILangKeys.CAPPED_ENCHANTMENT_UPGRADE_EFFECT.translateArgs(levels, enchantmentName, Component.literal(Integer.toString(maxLevel)).withStyle(ChatFormatting.YELLOW));

        lines.accept(tooltip);
    }
}