package liedge.limatech.lib.upgradesystem.equipment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.List;

public class EnchantmentLevelUpgradeEffect implements EquipmentUpgradeEffect
{
    public static final MapCodec<EnchantmentLevelUpgradeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryFixedCodec.create(Registries.ENCHANTMENT).fieldOf("enchantment").forGetter(o -> o.enchantment),
            LevelBasedValue.CODEC.fieldOf("levels_per_rank").forGetter(o -> o.levelsPerRank))
            .apply(instance, EnchantmentLevelUpgradeEffect::new));

    private final Holder<Enchantment> enchantment;
    private final LevelBasedValue levelsPerRank;

    public EnchantmentLevelUpgradeEffect(Holder<Enchantment> enchantment, LevelBasedValue levelsPerRank)
    {
        this.enchantment = enchantment;
        this.levelsPerRank = levelsPerRank;
    }

    public EnchantmentLevelUpgradeEffect(Holder<Enchantment> enchantment)
    {
        this(enchantment, LevelBasedValue.perLevel(1));
    }

    @Override
    public int addToEnchantmentLevel(Holder<Enchantment> enchantment, int upgradeRank)
    {
        if (enchantment.value().equals(this.enchantment.value()))
        {
            return Math.round(levelsPerRank.calculate(upgradeRank));
        }
        else
        {
            return 0;
        }
    }

    @Override
    public EquipmentUpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.ENCHANTMENT_LEVEL.get();
    }

    @Override
    public void appendEffectTooltip(int upgradeRank, List<Component> lines)
    {
        lines.add(LimaTechLang.ENCHANTMENT_UPGRADE_EFFECT.translateArgs(enchantment.value().description(), Component.translatable("enchantment.level." + upgradeRank)).withStyle(ChatFormatting.DARK_PURPLE));
    }
}