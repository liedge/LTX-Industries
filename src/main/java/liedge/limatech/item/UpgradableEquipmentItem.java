package liedge.limatech.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.limatech.registry.game.LimaTechDataComponents;
import liedge.limatech.registry.game.LimaTechUpgradeEffectComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

public interface UpgradableEquipmentItem
{
    static EquipmentUpgrades getEquipmentUpgradesFromStack(ItemStack stack)
    {
        return stack.getOrDefault(LimaTechDataComponents.EQUIPMENT_UPGRADES, EquipmentUpgrades.EMPTY);
    }

    default void refreshEquipmentUpgrades(ItemStack stack, EquipmentUpgrades upgrades)
    {
        // Refresh enchantments
        upgrades.applyEnchantments(stack);

        // Refresh attribute modifiers
        List<ItemAttributeModifiers.Entry> modifierEntries = new ObjectArrayList<>();
        upgrades.forEachEffect(LimaTechUpgradeEffectComponents.ITEM_ATTRIBUTE_MODIFIERS, (effect, rank) -> modifierEntries.add(effect.makeModifierEntry(rank)));
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(modifierEntries, false));
    }

    default EquipmentUpgrades getUpgrades(ItemStack stack)
    {
        return getEquipmentUpgradesFromStack(stack);
    }

    default void setUpgrades(ItemStack stack, EquipmentUpgrades upgrades)
    {
        stack.set(LimaTechDataComponents.EQUIPMENT_UPGRADES, upgrades);
    }
}