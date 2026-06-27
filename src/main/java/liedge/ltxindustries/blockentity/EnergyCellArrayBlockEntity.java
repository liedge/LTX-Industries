package liedge.ltxindustries.blockentity;

import liedge.limacore.network.sync.SimpleValueTracker;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.transfer.LimaEnergyUtil;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

import static liedge.ltxindustries.util.config.LTXIMachinesConfig.ECA_BASE_ENERGY_CAPACITY;
import static liedge.ltxindustries.util.config.LTXIMachinesConfig.ECA_BASE_TRANSFER_RATE;

public class EnergyCellArrayBlockEntity extends BaseECABlockEntity
{
    private int remoteEnergyFill;

    public EnergyCellArrayBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.ENERGY_CELL_ARRAY.get(), pos, state, null);
    }

    @Override
    public float getRemoteEnergyFill()
    {
        return remoteEnergyFill / 20f;
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return ECA_BASE_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyTransferRate()
    {
        return ECA_BASE_TRANSFER_RATE.getAsInt();
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(SimpleValueTracker.create(LimaCoreNetworkSerializers.VAR_INT, () -> Mth.floor(LimaEnergyUtil.getFillPercentage(getEnergy()) * 20f), i -> this.remoteEnergyFill = i).setAutomatic());
    }
}