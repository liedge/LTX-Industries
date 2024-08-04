package liedge.limatech.blockentity;

import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.network.sync.SimpleDataWatcher;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaMathUtil;

public interface ProgressMachine
{
    int getMachineProgress();

    void setMachineProgress(int machineProgress);

    int getMachineProcessTime();

    default float getProgressAsPercent()
    {
        return LimaMathUtil.divideFloat(getMachineProgress(), getMachineProcessTime());
    }

    default LimaDataWatcher<Integer> keepMachineProgressSynced()
    {
        return SimpleDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getMachineProgress, this::setMachineProgress);
    }
}