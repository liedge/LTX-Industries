package liedge.ltxindustries.blockentity;

import liedge.limacore.transfer.energy.LimaInfiniteEnergyHandler;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InfiniteECABlockEntity extends BaseECABlockEntity
{
    public InfiniteECABlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.INFINITE_ENERGY_CELL_ARRAY.get(), pos, state, LimaInfiniteEnergyHandler.INSTANCE);
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