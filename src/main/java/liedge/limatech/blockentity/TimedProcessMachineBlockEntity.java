package liedge.limatech.blockentity;

import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaMathUtil;

public interface TimedProcessMachineBlockEntity
{
    int getCurrentProcessTime();

    void setCurrentProcessTime(int currentProcessTime);

    int getTotalProcessDuration();

    default void setTotalProcessDuration(int totalProcessDuration) {}

    default float getProcessTimePercent()
    {
        return LimaMathUtil.divideFloat(getCurrentProcessTime(), getTotalProcessDuration());
    }

    default LimaDataWatcher<Integer> keepProcessSynced()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getCurrentProcessTime, this::setCurrentProcessTime);
    }

    default LimaDataWatcher<Integer> keepProcessDurationSynced()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getTotalProcessDuration, this::setTotalProcessDuration);
    }
}