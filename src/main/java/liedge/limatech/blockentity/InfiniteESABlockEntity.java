package liedge.limatech.blockentity;

import liedge.limacore.blockentity.IOAccess;
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

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class InfiniteESABlockEntity extends EnergyStorageArrayBlockEntity
{
    private final MachineIOControl energyControl;

    public InfiniteESABlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        Direction front = state.getValue(HORIZONTAL_FACING);
        this.energyControl = new MachineIOControl(this, MachineInputType.ENERGY, IOAccess.ONLY_OUTPUT_AND_DISABLED, IOAccess.OUTPUT_ONLY, front, false, true);
    }

    @Override
    public LimaColor getRemoteEnergyFillColor()
    {
        return LimaTechConstants.NIOBIUM_PURPLE;
    }

    @Override
    public float getRemoteEnergyFill()
    {
        return 1f;
    }

    @Override
    protected MachineIOControl energyIOControl()
    {
        return energyControl;
    }

    @Override
    public LimaEnergyStorage getEnergyStorage()
    {
        return InfiniteEnergyStorage.INFINITE_ENERGY_STORAGE;
    }
}