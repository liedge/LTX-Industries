package liedge.ltxindustries.item;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.item.EnergyHolderItem;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.InfiniteEnergyHandler;
import org.jspecify.annotations.Nullable;

public class InfiniteECABlockItem extends BlockItem implements EnergyHolderItem, TooltipShiftHintItem
{
    public InfiniteECABlockItem(Block block, Properties properties)
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
    public EnergyHandler getEnergy(ItemStack stack, ItemAccess access)
    {
        return InfiniteEnergyHandler.INSTANCE;
    }

    @Override
    public @Nullable EnergyHandler getNoTransferLimitEnergy(ItemStack stack, ItemAccess access)
    {
        return InfiniteEnergyHandler.INSTANCE;
    }

    @Override
    public void appendTooltipHintComponents(Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        LTXITooltipUtil.appendItemStorageEnergyTooltip(consumer, stack, this);
    }
}