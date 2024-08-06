package liedge.limatech.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import static liedge.limacore.capability.energy.InfiniteEnergyStorage.INFINITE_ENERGY_STORAGE;

public class InfiniteEnergyItem extends Item implements LimaContainerItem
{
    public InfiniteEnergyItem(Properties properties)
    {
        super(properties);
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
    public int getEnergyStored(ItemStack stack)
    {
        return INFINITE_ENERGY_STORAGE.getEnergyStored();
    }

    @Override
    public int getEnergyCapacity(ItemStack stack)
    {
        return INFINITE_ENERGY_STORAGE.getMaxEnergyStored();
    }

    @Override
    public int getEnergyTransferRate(ItemStack stack)
    {
        return INFINITE_ENERGY_STORAGE.getTransferRate();
    }

    @Override
    public @Nullable IEnergyStorage createEnergyStorage(ItemStack stack)
    {
        return INFINITE_ENERGY_STORAGE;
    }

    @Override
    public boolean isFoil(ItemStack stack)
    {
        return true;
    }
}