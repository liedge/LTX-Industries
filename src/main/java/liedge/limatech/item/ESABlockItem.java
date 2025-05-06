package liedge.limatech.item;

import liedge.limacore.capability.energy.ItemEnergyProperties;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.item.LimaCreativeTabFillerItem;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.util.config.LimaTechMachinesConfig;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import static liedge.limacore.capability.energy.InfiniteEnergyStorage.INFINITE_ENERGY_STORAGE;

public class ESABlockItem extends BlockItem implements EnergyHolderItem, TooltipShiftHintItem, LimaCreativeTabFillerItem
{
    private final boolean infinite;

    public ESABlockItem(Block block, Properties properties, boolean infinite)
    {
        super(block, properties);
        this.infinite = infinite;
    }

    @Override
    public int getBaseEnergyCapacity(ItemStack stack)
    {
        return LimaTechMachinesConfig.ESA_BASE_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyTransferRate(ItemStack stack)
    {
        return LimaTechMachinesConfig.ESA_BASE_TRANSFER_RATE.getAsInt();
    }

    @Override
    public int getEnergyStored(ItemStack stack)
    {
        return infinite ? Integer.MAX_VALUE : EnergyHolderItem.super.getEnergyStored(stack);
    }

    @Override
    public ItemEnergyProperties getEnergyProperties(ItemStack stack)
    {
        return infinite ? ItemEnergyProperties.INFINITE : EnergyHolderItem.super.getEnergyProperties(stack);
    }

    @Override
    public IEnergyStorage getOrCreateEnergyStorage(ItemStack stack)
    {
        return infinite ? INFINITE_ENERGY_STORAGE : EnergyHolderItem.super.getOrCreateEnergyStorage(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack)
    {
        return !infinite;
    }

    @Override
    public int getBarColor(ItemStack stack)
    {
        return LimaTechConstants.REM_BLUE.argb32();
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
        appendStorageEnergyTooltip(consumer, stack);
    }
}