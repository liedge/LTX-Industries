package liedge.limatech.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.limatech.registry.LimaTechDataComponents;
import liedge.limatech.registry.LimaTechUpgradeEffectComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.ItemEnchantments;

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
        ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY.withTooltip(false));
        getUpgrades(stack).forEachEffect(LimaTechUpgradeEffectComponents.ITEM_ENCHANTMENTS, (effect, rank) -> effect.applyEnchantment(builder, rank));
        ItemEnchantments enchantments = builder.keySet().isEmpty() ? null : builder.toImmutable();
        stack.set(DataComponents.ENCHANTMENTS, enchantments);

        // Refresh attribute modifiers
        List<ItemAttributeModifiers.Entry> modifierEntries = new ObjectArrayList<>();
        getUpgrades(stack).forEachEffect(LimaTechUpgradeEffectComponents.ITEM_ATTRIBUTE_MODIFIERS, (effect, rank) -> modifierEntries.add(effect.makeModifierEntry(rank)));
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