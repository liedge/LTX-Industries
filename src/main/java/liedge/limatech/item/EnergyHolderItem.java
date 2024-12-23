package liedge.limatech.item;

import liedge.limacore.capability.energy.LimaComponentEnergyStorage;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limatech.util.LimaTechTooltipUtil;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import static liedge.limacore.registry.LimaCoreDataComponents.ENERGY;

public interface EnergyHolderItem
{
    static @Nullable IEnergyStorage createEnergyAccess(ItemStack stack)
    {
        return LimaCoreUtil.castOrThrow(EnergyHolderItem.class, stack.getItem(), "Not an energy holder item.").getOrCreateEnergyStorage(stack);
    }

    default int getEnergyStored(ItemStack stack)
    {
        return stack.getOrDefault(ENERGY, 0);
    }

    int getEnergyCapacity(ItemStack stack);

    int getEnergyTransferRate(ItemStack stack);

    default boolean supportsEnergyStorage(ItemStack stack)
    {
        return true;
    }

    default @Nullable IEnergyStorage getOrCreateEnergyStorage(ItemStack stack)
    {
        if (supportsEnergyStorage(stack))
        {
            return new LimaComponentEnergyStorage(stack, getEnergyCapacity(stack), getEnergyTransferRate(stack));
        }

        return null;
    }

    default void appendSimpleEnergyTooltip(TooltipLineConsumer consumer, ItemStack stack)
    {
        LimaTechTooltipUtil.appendSimpleEnergyTooltip(consumer, getEnergyStored(stack));
    }

    default void appendExtendedEnergyTooltip(TooltipLineConsumer consumer, ItemStack stack)
    {
        LimaTechTooltipUtil.appendExtendedEnergyTooltip(consumer, getEnergyStored(stack), getEnergyCapacity(stack), getEnergyTransferRate(stack));
    }
}