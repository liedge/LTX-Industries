package liedge.ltxindustries.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface UpgradableEquipmentItem extends ItemLike
{
    static EquipmentUpgrades getEquipmentUpgradesFromStack(ItemStack stack)
    {
        return stack.getOrDefault(LTXIDataComponents.EQUIPMENT_UPGRADES, EquipmentUpgrades.EMPTY);
    }

    default @Nullable ResourceKey<EquipmentUpgrade> getDefaultUpgradeKey()
    {
        return null;
    }

    default ItemStack createStackWithDefaultUpgrades(HolderLookup.Provider registries)
    {
        ItemStack stack = new ItemStack(this);
        Optional.ofNullable(getDefaultUpgradeKey()).flatMap(registries::holder).ifPresent(holder -> setUpgrades(stack, EquipmentUpgrades.builder().set(holder).toImmutable()));
        return stack;
    }

    default void onUpgradeRefresh(LootContext context, ItemStack stack, EquipmentUpgrades upgrades)
    {
        // Refresh enchantments
        upgrades.applyEnchantments(stack);

        // Refresh attribute modifiers
        List<ItemAttributeModifiers.Entry> modifierEntries = new ObjectArrayList<>();
        modifierEntries.addAll(this.asItem().getDefaultAttributeModifiers(stack).modifiers());
        upgrades.forEachEffect(LTXIUpgradeEffectComponents.ADD_ITEM_ATTRIBUTES, (effect, rank) -> modifierEntries.add(effect.createModifierEntry(rank)));
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(modifierEntries, true));

        // Apply to energy capability holder items only
        if (this instanceof EnergyHolderItem holderItem && holderItem.supportsEnergyStorage(stack))
        {
            int capacity = LimaCoreMath.round(upgrades.runValueOps(LTXIUpgradeEffectComponents.ENERGY_CAPACITY, context, holderItem.getBaseEnergyCapacity(stack)));
            stack.set(LimaCoreDataComponents.ENERGY_CAPACITY, capacity);

            int transferRate = LimaCoreMath.round(upgrades.runValueOps(LTXIUpgradeEffectComponents.ENERGY_TRANSFER_RATE, context, holderItem.getBaseEnergyTransferRate(stack)));
            int energyUsage = LimaCoreMath.round(upgrades.runValueOps(LTXIUpgradeEffectComponents.ENERGY_USAGE, context, holderItem.getBaseEnergyUsage(stack)));
            stack.set(LimaCoreDataComponents.ENERGY_TRANSFER_RATE, transferRate);
            stack.set(LimaCoreDataComponents.ENERGY_USAGE, energyUsage);
        }
    }

    default EquipmentUpgrades getUpgrades(ItemStack stack)
    {
        return getEquipmentUpgradesFromStack(stack);
    }

    default void setUpgrades(ItemStack stack, EquipmentUpgrades upgrades)
    {
        stack.set(LTXIDataComponents.EQUIPMENT_UPGRADES, upgrades);
    }

    default double getUpgradedDamage(EquipmentUpgrades upgrades, LootContext context, double baseDamage)
    {
        return upgrades.runConditionalValueOps(LTXIUpgradeEffectComponents.EQUIPMENT_DAMAGE, context, baseDamage);
    }
}