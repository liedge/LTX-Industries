package liedge.ltxindustries.item;

import liedge.limacore.capability.energy.ItemEnergyProperties;
import liedge.limacore.capability.energy.LimaComponentEnergyStorage;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.util.LimaCoreUtil;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import static liedge.limacore.registry.game.LimaCoreDataComponents.ENERGY;
import static liedge.limacore.registry.game.LimaCoreDataComponents.ENERGY_PROPERTIES;

public interface EnergyHolderItem
{
    static @Nullable IEnergyStorage createEnergyAccess(ItemStack stack)
    {
        EnergyHolderItem item = LimaCoreUtil.castOrThrow(EnergyHolderItem.class, stack.getItem(), "Not an energy holder item.");
        return item.supportsEnergyStorage(stack) ? item.getOrCreateEnergyStorage(stack) : null;
    }

    int getBaseEnergyCapacity(ItemStack stack);

    int getBaseEnergyTransferRate(ItemStack stack);

    default int getBaseEnergyUsage(ItemStack stack)
    {
        return 0;
    }

    default int getEnergyStored(ItemStack stack)
    {
        return stack.getOrDefault(ENERGY, 0);
    }

    default ItemEnergyProperties getEnergyProperties(ItemStack stack)
    {
        return stack.getOrDefault(ENERGY_PROPERTIES, ItemEnergyProperties.EMPTY);
    }

    default int getEnergyCapacity(ItemStack stack)
    {
        ItemEnergyProperties properties = getEnergyProperties(stack);
        return properties == ItemEnergyProperties.EMPTY ? getBaseEnergyCapacity(stack) : properties.capacity();
    }

    default int getEnergyTransferRate(ItemStack stack)
    {
        ItemEnergyProperties properties = getEnergyProperties(stack);
        return properties == ItemEnergyProperties.EMPTY ? getBaseEnergyTransferRate(stack) : properties.transferRate();
    }

    default int getEnergyUsage(ItemStack stack)
    {
        ItemEnergyProperties properties = getEnergyProperties(stack);
        return properties == ItemEnergyProperties.EMPTY ? getBaseEnergyUsage(stack) : properties.energyUsage();
    }

    default boolean supportsEnergyStorage(ItemStack stack)
    {
        return true;
    }

    default IEnergyStorage getOrCreateEnergyStorage(ItemStack stack)
    {
        ItemEnergyProperties properties = getEnergyProperties(stack);
        if (properties == ItemEnergyProperties.EMPTY) properties = new ItemEnergyProperties(getBaseEnergyCapacity(stack), getBaseEnergyTransferRate(stack), getBaseEnergyUsage(stack));
        return new LimaComponentEnergyStorage(stack, properties);
    }

    default void appendEquipmentEnergyTooltip(TooltipLineConsumer consumer, ItemStack stack)
    {
        LTXITooltipUtil.appendEnergyWithCapacityTooltip(consumer, getEnergyStored(stack), getEnergyCapacity(stack));
        LTXITooltipUtil.appendEnergyUsageTooltip(consumer, getEnergyUsage(stack));
    }

    default void appendStorageEnergyTooltip(TooltipLineConsumer consumer, ItemStack stack)
    {
        LTXITooltipUtil.appendStorageEnergyTooltip(consumer, getEnergyStored(stack), getEnergyCapacity(stack), getEnergyTransferRate(stack));
    }
}