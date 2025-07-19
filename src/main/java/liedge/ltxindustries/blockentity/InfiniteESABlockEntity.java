package liedge.ltxindustries.blockentity;

import liedge.limacore.capability.energy.InfiniteEnergyStorage;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InfiniteESABlockEntity extends BaseESABlockEntity
{
    public InfiniteESABlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.INFINITE_ENERGY_STORAGE_ARRAY.get(), pos, state, InfiniteEnergyStorage.INFINITE_ENERGY_STORAGE);
    }

    @Override
    public LimaColor getRemoteEnergyFillColor()
    {
        return LTXIConstants.CREATIVE_PINK;
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
}