package liedge.limatech.item;

import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.util.config.LimaTechMachinesConfig;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class EnergyStorageArrayItem extends BlockItem implements LimaContainerItem
{
    public EnergyStorageArrayItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    public boolean supportsEnergy(ItemStack stack)
    {
        return true;
    }

    @Override
    public boolean extendedEnergyTooltip(ItemStack stack)
    {
        return true;
    }

    @Override
    public int getEnergyCapacity(ItemStack stack)
    {
        return LimaTechMachinesConfig.ESA_BASE_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getEnergyTransferRate(ItemStack stack)
    {
        return LimaTechMachinesConfig.ESA_BASE_TRANSFER_RATE.getAsInt();
    }

    @Override
    public boolean isBarVisible(ItemStack stack)
    {
        return true;
    }

    @Override
    public int getBarColor(ItemStack stack)
    {
        return LimaTechConstants.REM_BLUE.packedRGB();
    }

    @Override
    public int getBarWidth(ItemStack stack)
    {
        return Math.round(13f * LimaMathUtil.divideFloat(getEnergyStored(stack), getEnergyCapacity(stack)));
    }
}