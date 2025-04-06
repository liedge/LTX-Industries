package liedge.limatech.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.capability.energy.EnergyContainerSpec;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.limatech.registry.game.LimaTechDataComponents;
import liedge.limatech.registry.game.LimaTechUpgradeEffectComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.List;

public interface UpgradableEquipmentItem
{
    static EquipmentUpgrades getEquipmentUpgradesFromStack(ItemStack stack)
    {
        return stack.getOrDefault(LimaTechDataComponents.EQUIPMENT_UPGRADES, EquipmentUpgrades.EMPTY);
    }

    default void onUpgradeRefresh(LootContext context, ItemStack stack, EquipmentUpgrades upgrades)
    {
        // Refresh enchantments
        upgrades.applyEnchantments(stack);

        // Refresh attribute modifiers
        List<ItemAttributeModifiers.Entry> modifierEntries = new ObjectArrayList<>();
        upgrades.forEachEffect(LimaTechUpgradeEffectComponents.ITEM_ATTRIBUTE_MODIFIERS, (effect, rank) -> modifierEntries.add(effect.makeModifierEntry(rank)));
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(modifierEntries, false));

        // Apply to energy capability holder items only
        if (this instanceof EnergyHolderItem)
        {
            EnergyContainerSpec spec = stack.getOrDefault(LimaCoreDataComponents.ENERGY_SPEC, EnergyContainerSpec.EMPTY);
            double newCapacity = upgrades.applyValue(LimaTechUpgradeEffectComponents.ENERGY_CAPACITY, context, spec.capacity());
            double newTransferRate = upgrades.applyValue(LimaTechUpgradeEffectComponents.ENERGY_TRANSFER_RATE, context, spec.transferRate());
            spec = new EnergyContainerSpec(LimaMathUtil.round(newCapacity), LimaMathUtil.round(newTransferRate));
            stack.set(LimaCoreDataComponents.ENERGY_SPEC, spec);
        }
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