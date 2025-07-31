package liedge.ltxindustries.item;

import liedge.limacore.capability.energy.InfiniteEnergyStorage;
import liedge.limacore.client.gui.TooltipLineConsumer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class InfiniteESABlockItem extends BlockItem implements EnergyHolderItem, TooltipShiftHintItem
{
    public InfiniteESABlockItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    public int getBaseEnergyCapacity(ItemStack stack)
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getBaseEnergyTransferRate(ItemStack stack)
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getEnergyStored(ItemStack stack)
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getEnergyCapacity(ItemStack stack)
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getEnergyTransferRate(ItemStack stack)
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public IEnergyStorage getOrCreateEnergyStorage(ItemStack stack)
    {
        return InfiniteEnergyStorage.INFINITE_ENERGY_STORAGE;
    }

    @Override
    public void appendTooltipHintComponents(Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        appendStorageEnergyTooltip(consumer, stack);
    }
}