package liedge.limatech.item;

import liedge.limatech.lib.upgradesystem.equipment.EquipmentUpgrades;
import liedge.limatech.registry.LimaTechDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public interface UpgradableEquipmentItem
{
    static EquipmentUpgrades getEquipmentUpgradesFromStack(ItemStack stack)
    {
        return stack.getOrDefault(LimaTechDataComponents.EQUIPMENT_UPGRADES, EquipmentUpgrades.EMPTY);
    }

    default EquipmentUpgrades getUpgrades(ItemStack stack)
    {
        return getEquipmentUpgradesFromStack(stack);
    }

    default int getUpgradeEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment)
    {
        return getUpgrades(stack).flatMapEffectsToInt(((effect, upgradeRank) -> effect.addToEnchantmentLevel(enchantment, upgradeRank))).sum();
    }
}