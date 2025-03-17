package liedge.limatech.blockentity;

import liedge.limacore.capability.energy.InfiniteEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.lib.LimaColor;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.registry.LimaTechBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InfiniteESABlockEntity extends BaseESABlockEntity
{
    public InfiniteESABlockEntity(BlockPos pos, BlockState state)
    {
        super(LimaTechBlockEntities.INFINITE_ENERGY_STORAGE_ARRAY.get(), pos, state);
    }

    @Override
    public LimaColor getRemoteEnergyFillColor()
    {
        return LimaTechConstants.CREATIVE_PINK;
    }

    @Override
    public float getRemoteEnergyFill()
    {
        return 1f;
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getBaseEnergyTransferRate()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public LimaEnergyStorage getEnergyStorage()
    {
        return InfiniteEnergyStorage.INFINITE_ENERGY_STORAGE;
    }
}