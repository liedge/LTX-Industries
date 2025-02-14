package liedge.limatech.item;

import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.limatech.registry.LimaTechDataComponents;
import liedge.limatech.registry.LimaTechUpgradeEffectComponents;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public interface UpgradableEquipmentItem
{
    static EquipmentUpgrades getEquipmentUpgradesFromStack(ItemStack stack)
    {
        return stack.getOrDefault(LimaTechDataComponents.EQUIPMENT_UPGRADES, EquipmentUpgrades.EMPTY);
    }

    void refreshEquipmentUpgrades(ItemStack stack, EquipmentUpgrades upgrades, Player player);

    default EquipmentUpgrades getUpgrades(ItemStack stack)
    {
        return getEquipmentUpgradesFromStack(stack);
    }

    default void setUpgrades(ItemStack stack, EquipmentUpgrades upgrades)
    {
        stack.set(LimaTechDataComponents.EQUIPMENT_UPGRADES, upgrades);
    }

    default int getUpgradeEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment)
    {
        return getUpgrades(stack).flatMapToInt(LimaTechUpgradeEffectComponents.ITEM_ENCHANTMENTS.get(), (effect, rank) -> effect.getEnchantmentLevels(enchantment, rank)).sum();
    }
}