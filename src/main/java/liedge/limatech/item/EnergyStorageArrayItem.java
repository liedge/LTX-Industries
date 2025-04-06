package liedge.limatech.item;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.LimaTechConstants;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import static liedge.limacore.capability.energy.InfiniteEnergyStorage.INFINITE_ENERGY_STORAGE;

public class EnergyStorageArrayItem extends BlockItem implements EnergyHolderItem, TooltipShiftHintItem
{
    private final boolean infinite;

    public EnergyStorageArrayItem(Block block, Properties properties, boolean infinite)
    {
        super(block, properties);
        this.infinite = infinite;
    }

    @Override
    public int getEnergyStored(ItemStack stack)
    {
        return infinite ? INFINITE_ENERGY_STORAGE.getEnergyStored() : EnergyHolderItem.super.getEnergyStored(stack);
    }

    @Override
    public IEnergyStorage getOrCreateEnergyStorage(ItemStack stack)
    {
        if (infinite)
        {
            return INFINITE_ENERGY_STORAGE;
        }
        else
        {
            return EnergyHolderItem.super.getOrCreateEnergyStorage(stack);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack)
    {
        return !infinite;
    }

    @Override
    public int getBarColor(ItemStack stack)
    {
        return LimaTechConstants.REM_BLUE.packedRGB();
    }

    @Override
    public int getBarWidth(ItemStack stack)
    {
        float fill = Math.min(LimaMathUtil.divideFloat(getEnergyStored(stack), getEnergyCapacity(stack)), 1f);
        return Math.round(13f * fill);
    }

    @Override
    public void appendTooltipHintComponents(@Nullable Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        appendExtendedEnergyTooltip(consumer, stack);
    }
}