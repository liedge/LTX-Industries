package liedge.ltxindustries.item;

import liedge.limacore.capability.energy.LimaComponentEnergyStorage;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.util.LimaCoreUtil;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import static liedge.limacore.registry.game.LimaCoreDataComponents.*;

public interface EnergyHolderItem extends ItemLike
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

    default int getEnergyCapacity(ItemStack stack)
    {
        return stack.getOrDefault(ENERGY_CAPACITY, getBaseEnergyCapacity(stack));
    }

    default int getEnergyTransferRate(ItemStack stack)
    {
        return stack.getOrDefault(ENERGY_TRANSFER_RATE, getBaseEnergyTransferRate(stack));
    }

    default int getEnergyUsage(ItemStack stack)
    {
        return stack.getOrDefault(ENERGY_USAGE, getBaseEnergyUsage(stack));
    }

    default float getChargePercentage(ItemStack stack)
    {
        float fill = LimaCoreMath.divideFloat(getEnergyStored(stack), getEnergyCapacity(stack));
        return Mth.clamp(fill, 0f, 1f);
    }

    default boolean supportsEnergyStorage(ItemStack stack)
    {
        return true;
    }

    default IEnergyStorage getOrCreateEnergyStorage(ItemStack stack)
    {
        return LimaComponentEnergyStorage.createFromItem(stack, getBaseEnergyCapacity(stack), getBaseEnergyTransferRate(stack));
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