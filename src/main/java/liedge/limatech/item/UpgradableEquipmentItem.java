package liedge.limatech.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.capability.energy.ItemEnergyProperties;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.util.LimaMathUtil;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.limatech.registry.LimaTechRegistries;
import liedge.limatech.registry.game.LimaTechDataComponents;
import liedge.limatech.registry.game.LimaTechUpgradeEffectComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.List;

public interface UpgradableEquipmentItem extends ItemLike
{
    static EquipmentUpgrades getEquipmentUpgradesFromStack(ItemStack stack)
    {
        return stack.getOrDefault(LimaTechDataComponents.EQUIPMENT_UPGRADES, EquipmentUpgrades.EMPTY);
    }

    default ItemStack createStackWithDefaultUpgrades(HolderLookup.Provider registries)
    {
        ItemStack stack = new ItemStack(this);

        ResourceLocation id = LimaRegistryUtil.getItemId(this.asItem());
        ResourceKey<EquipmentUpgrade> defaultKey = ResourceKey.create(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES, id.withSuffix("_default"));
        registries.holder(defaultKey).ifPresent(holder -> setUpgrades(stack, EquipmentUpgrades.builder().set(holder).toImmutable()));

        return stack;
    }

    default void onUpgradeRefresh(LootContext context, ItemStack stack, EquipmentUpgrades upgrades)
    {
        // Refresh enchantments
        upgrades.applyEnchantments(stack);

        // Refresh attribute modifiers
        List<ItemAttributeModifiers.Entry> modifierEntries = new ObjectArrayList<>();
        modifierEntries.addAll(this.asItem().getDefaultAttributeModifiers(stack).modifiers());
        upgrades.forEachEffect(LimaTechUpgradeEffectComponents.ITEM_ATTRIBUTE_MODIFIERS, (effect, rank) -> modifierEntries.add(effect.makeModifierEntry(rank)));
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(modifierEntries, true));

        // Apply to energy capability holder items only
        if (this instanceof EnergyHolderItem holderItem && holderItem.supportsEnergyStorage(stack))
        {
            int capacity = LimaMathUtil.round(upgrades.applyValue(LimaTechUpgradeEffectComponents.ENERGY_CAPACITY, context, holderItem.getBaseEnergyCapacity(stack)));
            int transferRate = LimaMathUtil.round(upgrades.applyValue(LimaTechUpgradeEffectComponents.ENERGY_TRANSFER_RATE, context, holderItem.getBaseEnergyTransferRate(stack)));
            int energyUsage = LimaMathUtil.round(upgrades.applyValue(LimaTechUpgradeEffectComponents.ENERGY_USAGE, context, holderItem.getBaseEnergyUsage(stack)));

            stack.set(LimaCoreDataComponents.ENERGY_PROPERTIES, new ItemEnergyProperties(capacity, transferRate, energyUsage));
        }
        else
        {
            stack.remove(LimaCoreDataComponents.ENERGY_PROPERTIES);
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

    default float getUpgradedDamage(ServerLevel level, EquipmentUpgrades upgrades, Entity targetEntity, DamageSource source, double baseDamage)
    {
        return (float) upgrades.applyConditionalValue(LimaTechUpgradeEffectComponents.EQUIPMENT_DAMAGE.get(), rank -> Enchantment.damageContext(level, rank, targetEntity, source), baseDamage);
    }
}