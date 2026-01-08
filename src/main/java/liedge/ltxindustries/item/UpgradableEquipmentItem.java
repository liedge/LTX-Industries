package liedge.ltxindustries.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.ltxindustries.lib.upgrades.UpgradesContainerBase;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface UpgradableEquipmentItem extends ItemLike, EnergyHolderItem
{
    static EquipmentUpgrades getEquipmentUpgradesFromStack(ItemStack stack)
    {
        return stack.getOrDefault(LTXIDataComponents.EQUIPMENT_UPGRADES, EquipmentUpgrades.EMPTY);
    }

    default @Nullable EquipmentSlot getEquipmentSlot()
    {
        return null;
    }

    default boolean isInCorrectSlot(EquipmentSlot slot)
    {
        return getEquipmentSlot() == null || slot == getEquipmentSlot();
    }

    //#region Energy functions
    default int getBaseEnergyUsage(ItemStack stack)
    {
        return 0;
    }

    default int getEnergyUsage(ItemStack stack)
    {
        return stack.getOrDefault(LimaCoreDataComponents.ENERGY_USAGE, getBaseEnergyUsage(stack));
    }

    default boolean hasEnergyForAction(ItemStack stack)
    {
        return getEnergyStored(stack) >= getEnergyUsage(stack);
    }

    default boolean consumeEnergy(LivingEntity entity, ItemStack stack, int toExtract)
    {
        if (entity instanceof Player player && player.isCreative()) return true;
        else return LimaEnergyUtil.consumeEnergy(getOrCreateEnergyStorage(stack), toExtract, true);
    }

    default boolean consumeEnergyActions(LivingEntity entity, ItemStack stack, int actions)
    {
        return actions <= 0 || consumeEnergy(entity, stack, getEnergyUsage(stack) * actions);
    }

    default boolean consumeEnergyAction(LivingEntity entity, ItemStack stack)
    {
        return consumeEnergyActions(entity, stack, 1);
    }

    default void appendEquipmentEnergyTooltip(TooltipLineConsumer consumer, ItemStack stack)
    {
        LTXITooltipUtil.appendEnergyWithCapacityTooltip(consumer, getEnergyStored(stack), getEnergyCapacity(stack));
        LTXITooltipUtil.appendEnergyUsageTooltip(consumer, getEnergyUsage(stack));
    }
    //#endregion

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
        upgrades.forEachEffect(LTXIUpgradeEffectComponents.ADD_ITEM_ATTRIBUTES, (effect, rank) -> modifierEntries.add(effect.createItemModifier(rank, getEquipmentSlot())));
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(modifierEntries, true));

        // Energy
        if (supportsEnergyStorage(stack))
        {
            int capacity = LimaCoreMath.round(upgrades.runValueOps(LTXIUpgradeEffectComponents.ENERGY_CAPACITY, context, getBaseEnergyCapacity(stack)));
            stack.set(LimaCoreDataComponents.ENERGY_CAPACITY, capacity);

            int transferRate = LimaCoreMath.round(upgrades.runValueOps(LTXIUpgradeEffectComponents.ENERGY_TRANSFER_RATE, context, getBaseEnergyTransferRate(stack)));
            int energyUsage = LimaCoreMath.round(upgrades.runValueOps(LTXIUpgradeEffectComponents.ENERGY_USAGE, context, getBaseEnergyUsage(stack)));
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

    default double getUpgradedDamage(UpgradesContainerBase<?, ?> upgrades, LootContext context, double baseDamage)
    {
        return upgrades.runConditionalValueOps(LTXIUpgradeEffectComponents.EQUIPMENT_DAMAGE, context, baseDamage);
    }
}