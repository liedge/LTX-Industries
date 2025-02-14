package liedge.limatech.item;

import liedge.limacore.capability.energy.LimaComponentEnergyStorage;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.blockentity.UpgradableMachineBlockEntity;
import liedge.limatech.registry.LimaTechUpgradeEffectComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import static liedge.limacore.capability.energy.InfiniteEnergyStorage.INFINITE_ENERGY_STORAGE;
import static liedge.limatech.util.config.LimaTechMachinesConfig.ESA_BASE_ENERGY_CAPACITY;
import static liedge.limatech.util.config.LimaTechMachinesConfig.ESA_BASE_TRANSFER_RATE;

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
    public int getEnergyCapacity(ItemStack stack)
    {
        if (infinite)
        {
            return INFINITE_ENERGY_STORAGE.getMaxEnergyStored();
        }
        else
        {
            double newCapacity = UpgradableMachineBlockEntity.getMachineUpgradesFromItem(stack).runCompoundOps(LimaTechUpgradeEffectComponents.ENERGY_CAPACITY, null, null, ESA_BASE_ENERGY_CAPACITY.getAsInt());
            return LimaMathUtil.round(newCapacity);
        }
    }

    @Override
    public int getEnergyTransferRate(ItemStack stack)
    {
        if (infinite)
        {
            return INFINITE_ENERGY_STORAGE.getTransferRate();
        }
        else
        {
            double newTransferRate = UpgradableMachineBlockEntity.getMachineUpgradesFromItem(stack).runCompoundOps(LimaTechUpgradeEffectComponents.ENERGY_TRANSFER_RATE, null, null, ESA_BASE_TRANSFER_RATE.getAsInt());
            return LimaMathUtil.round(newTransferRate);
        }
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
            return new LimaComponentEnergyStorage(stack, getEnergyCapacity(stack), getEnergyTransferRate(stack));
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