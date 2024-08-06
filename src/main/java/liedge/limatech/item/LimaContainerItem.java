package liedge.limatech.item;

import liedge.limacore.capability.energy.LimaComponentEnergyStorage;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limatech.menu.tooltip.ItemGridTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static liedge.limacore.capability.energy.LimaEnergyUtil.formatEnergyWithSuffix;
import static liedge.limacore.registry.LimaCoreDataComponents.ENERGY;
import static liedge.limacore.registry.LimaCoreDataComponents.ITEM_CONTAINER;
import static liedge.limatech.LimaTechConstants.REM_BLUE;
import static liedge.limatech.client.LimaTechLang.*;

public interface LimaContainerItem extends TooltipShiftHintItem
{
    static @Nullable IEnergyStorage createEnergyAccess(ItemStack stack)
    {
        LimaContainerItem item = LimaCoreUtil.castOrNull(LimaContainerItem.class, stack.getItem());
        return item != null ? item.createEnergyStorage(stack) : null;
    }

    // Energy capability properties
    default boolean supportsEnergy(ItemStack stack)
    {
        return false;
    }

    default boolean extendedEnergyTooltip(ItemStack stack)
    {
        return false;
    }

    default int getEnergyStored(ItemStack stack)
    {
        return stack.getOrDefault(ENERGY, 0);
    }

    default int getEnergyCapacity(ItemStack stack)
    {
        return 1;
    }

    default int getEnergyTransferRate(ItemStack stack)
    {
        return 1;
    }

    default @Nullable IEnergyStorage createEnergyStorage(ItemStack stack)
    {
        if (supportsEnergy(stack))
        {
            return new LimaComponentEnergyStorage(stack, getEnergyCapacity(stack), getEnergyTransferRate(stack));
        }

        return null;
    }

    // Item capability properties
    default boolean supportsItemStorage(ItemStack stack)
    {
        return false;
    }

    @Override
    default void appendTooltipHintComponents(@Nullable Level level, ItemStack stack, TooltipCollector collector)
    {
        if (supportsEnergy(stack))
        {
            int energy = getEnergyStored(stack);
            collector.with(INLINE_ENERGY_STORED.translateArgs(formatEnergyWithSuffix(energy)).withStyle(REM_BLUE.chatStyle()));

            if (extendedEnergyTooltip(stack))
            {
                collector.with(INLINE_ENERGY_TRANSFER_RATE.translateArgs(formatEnergyWithSuffix(getEnergyTransferRate(stack))).withStyle(REM_BLUE.chatStyle()));
                collector.with(INLINE_ENERGY_CAPACITY.translateArgs(formatEnergyWithSuffix(getEnergyCapacity(stack))).withStyle(REM_BLUE.chatStyle()));
            }
        }

        if (supportsItemStorage(stack))
        {
            List<ItemStack> inventory = stack.getOrDefault(ITEM_CONTAINER, ItemContainerContents.EMPTY).nonEmptyStream().toList();
            if (!inventory.isEmpty())
            {
                collector.with(ITEM_INVENTORY_TOOLTIP.translate().withStyle(ChatFormatting.GRAY));
                collector.with(new ItemGridTooltip(inventory, 6));
            }
            else
            {
                collector.with(EMPTY_ITEM_INVENTORY_TOOLTIP.translate().withStyle(ChatFormatting.GRAY));
            }
        }
    }
}