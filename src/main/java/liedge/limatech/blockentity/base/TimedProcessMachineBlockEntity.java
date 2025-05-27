package liedge.limatech.blockentity.base;

import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.lib.upgrades.machine.MachineUpgrades;
import liedge.limatech.registry.game.LimaTechUpgradeEffectComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;

public interface TimedProcessMachineBlockEntity
{
    String TAG_KEY_PROGRESS = "progress";

    static void applyUpgrades(TimedProcessMachineBlockEntity blockEntity, LootContext context, MachineUpgrades upgrades)
    {
        double newProcessingTime = upgrades.applyValue(LimaTechUpgradeEffectComponents.TICKS_PER_OPERATION, context, blockEntity.getBaseTicksPerOperation());
        blockEntity.setTicksPerOperation(Math.max(0, Mth.floor(newProcessingTime)));
    }

    int getCurrentProcessTime();

    void setCurrentProcessTime(int currentProcessTime);

    int getBaseTicksPerOperation();

    int getTicksPerOperation();

    void setTicksPerOperation(int ticksPerOperation);

    default float getProcessTimePercent()
    {
        return LimaMathUtil.divideFloat(getCurrentProcessTime(), getTicksPerOperation());
    }

    default void keepTimedProcessPropertiesSynced(DataWatcherHolder.DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getCurrentProcessTime, this::setCurrentProcessTime));
        collector.register(AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getTicksPerOperation, this::setTicksPerOperation));
    }
}