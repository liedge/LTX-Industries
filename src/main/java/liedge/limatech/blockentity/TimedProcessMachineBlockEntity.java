package liedge.limatech.blockentity;

import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaMathUtil;

public interface TimedProcessMachineBlockEntity
{
    int getCurrentProcessTime();

    void setCurrentProcessTime(int currentProcessTime);

    int getBaseTicksPerOperation();

    int getTicksPerOperation();

    void setTicksPerOperation(int ticksPerOperation);

    int getBaseEnergyUsage();

    int getEnergyUsage();

    void setEnergyUsage(int energyUsage);

    default float getProcessTimePercent()
    {
        return LimaMathUtil.divideFloat(getCurrentProcessTime(), getTicksPerOperation());
    }

    default LimaDataWatcher<Integer> keepProcessSynced()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getCurrentProcessTime, this::setCurrentProcessTime);
    }

    default LimaDataWatcher<Integer> keepProcessDurationSynced()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getTicksPerOperation, this::setTicksPerOperation);
    }

    default void keepProcessAndDurationSynced(DataWatcherHolder.DataWatcherCollector collector)
    {
        collector.register(keepProcessSynced());
        collector.register(keepProcessDurationSynced());
    }
}