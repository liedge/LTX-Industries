package liedge.ltxindustries.blockentity.base;

import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;

public interface VariableTimedProcessBlockEntity
{
    String TAG_KEY_PROGRESS = "progress";

    int getCurrentProcessTime();

    void setCurrentProcessTime(int currentProcessTime);

    int getTicksPerOperation();

    void setTicksPerOperation(int ticksPerOperation);

    default float getProcessTimePercent()
    {
        return LimaCoreMath.divideFloat(getCurrentProcessTime(), getTicksPerOperation());
    }

    default void keepTimedProcessSynced(DataWatcherHolder.DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getCurrentProcessTime, this::setCurrentProcessTime));
        collector.register(AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getTicksPerOperation, this::setTicksPerOperation));
    }
}