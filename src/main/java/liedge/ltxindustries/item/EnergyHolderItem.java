package liedge.ltxindustries.item;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.transfer.LimaEnergyUtil;
import liedge.limacore.util.LimaCoreObjects;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.jetbrains.annotations.Nullable;

import static liedge.limacore.registry.game.LimaCoreDataComponents.*;

public interface EnergyHolderItem extends ItemLike
{
    static @Nullable EnergyHandler createItemEnergy(ItemStack stack, ItemAccess context)
    {
        EnergyHolderItem item = LimaCoreObjects.cast(EnergyHolderItem.class, stack.getItem(), "Not an energy holder item.");
        return item.supportsEnergyStorage(stack) ? item.getEnergy(stack, context) : null;

    }

    int getBaseEnergyCapacity(ItemInstance stack);

    int getBaseEnergyTransferRate(ItemStack stack);

    default int getEnergyStored(ItemInstance stack)
    {
        return stack.getOrDefault(ENERGY, 0);
    }

    default int getEnergyCapacity(ItemInstance stack)
    {
        return stack.getOrDefault(ENERGY_CAPACITY, getBaseEnergyCapacity(stack));
    }

    default int getEnergyTransferRate(ItemStack stack)
    {
        return stack.getOrDefault(ENERGY_TRANSFER_RATE, getBaseEnergyTransferRate(stack));
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

    default EnergyHandler getEnergy(ItemStack stack, ItemAccess access)
    {
        return LimaEnergyUtil.createStandardTransferItemEnergy(stack, access, getBaseEnergyCapacity(stack), getBaseEnergyTransferRate(stack));
    }

    default EnergyHandler getNoLimitEnergy(ItemStack stack, ItemAccess access)
    {
        return LimaEnergyUtil.createUnlimitedTransferItemEnergy(stack, access, getBaseEnergyCapacity(stack));
    }

    default void appendStorageEnergyTooltip(TooltipLineConsumer consumer, ItemStack stack)
    {
        LTXITooltipUtil.appendStorageEnergyTooltip(consumer, getEnergyStored(stack), getEnergyCapacity(stack), getEnergyTransferRate(stack));
    }
}