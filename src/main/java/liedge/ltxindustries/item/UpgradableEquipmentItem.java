package liedge.ltxindustries.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.transfer.LimaEnergyUtil;
import liedge.ltxindustries.lib.upgrades.MutableUpgrades;
import liedge.ltxindustries.lib.upgrades.Upgrade;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface UpgradableEquipmentItem extends ItemLike, EnergyHolderItem
{
    static Upgrades getUpgradesFrom(DataComponentHolder getter)
    {
        return getter.getOrDefault(LTXIDataComponents.UPGRADES, Upgrades.EMPTY);
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
    default int getBaseEnergyUsage(ItemInstance stack)
    {
        return 0;
    }

    default int getEnergyUsage(ItemInstance stack)
    {
        return stack.getOrDefault(LimaCoreDataComponents.ENERGY_USAGE, getBaseEnergyUsage(stack));
    }

    default boolean hasEnergyForAction(ItemInstance stack)
    {
        return getEnergyStored(stack) >= getEnergyUsage(stack);
    }

    default boolean consumeEnergy(LivingEntity entity, ItemStack stack, int amount)
    {
        if (entity instanceof Player player && player.isCreative())
        {
            return true;
        }
        else
        {
            ItemAccess access = ItemAccess.forStack(stack);
            EnergyHandler energy = getNoLimitEnergy(stack, access);
            return LimaEnergyUtil.useExact(energy, amount, null);
        }
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

    default @Nullable ResourceKey<Upgrade> getDefaultUpgradeKey()
    {
        return null;
    }

    default ItemStack createStackWithDefaultUpgrades(HolderLookup.Provider registries)
    {
        ItemStack stack = new ItemStack(this);
        Optional.ofNullable(getDefaultUpgradeKey()).flatMap(registries::holder).ifPresent(holder -> setUpgrades(stack, MutableUpgrades.create().set(holder).build()));
        return stack;
    }

    default void onUpgradeRefresh(LootContext context, ItemStack stack, Upgrades upgrades)
    {
        // Refresh enchantments
        upgrades.applyEnchantments(stack);

        // Refresh attribute modifiers
        List<ItemAttributeModifiers.Entry> modifierEntries = new ObjectArrayList<>();
        modifierEntries.addAll(this.asItem().getDefaultAttributeModifiers(stack).modifiers());
        upgrades.forEachEffect(LTXIUpgradeEffectComponents.ADD_ITEM_ATTRIBUTES, (effect, rank) -> modifierEntries.add(effect.createItemModifier(rank, getEquipmentSlot())));
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(modifierEntries));

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

    default Upgrades getUpgrades(ItemStack stack)
    {
        return getUpgradesFrom(stack);
    }

    default void setUpgrades(ItemStack stack, Upgrades upgrades)
    {
        stack.set(LTXIDataComponents.UPGRADES, upgrades);
    }

    default double getUpgradedDamage(Upgrades upgrades, LootContext context, double baseDamage)
    {
        return upgrades.runConditionalValueOps(LTXIUpgradeEffectComponents.EQUIPMENT_DAMAGE, context, baseDamage);
    }
}