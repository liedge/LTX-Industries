package liedge.limatech.blockentity;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.IOAccessSets;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.capability.energy.InfiniteEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.lib.LimaColor;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.blockentity.io.MachineIOControl;
import liedge.limatech.blockentity.io.MachineInputType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class InfiniteESABlockEntity extends BaseESABlockEntity
{
    public InfiniteESABlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
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
    protected MachineIOControl initEnergyIOControl(Direction front)
    {
        return new MachineIOControl(this, MachineInputType.ENERGY, IOAccessSets.OUTPUT_ONLY_OR_DISABLED, IOAccess.OUTPUT_ONLY, front, false, true);
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