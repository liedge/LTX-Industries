package liedge.ltxindustries.blockentity.base;

import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.world.level.storage.loot.LootContext;

public interface TimedProcessBlockEntity
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

    interface FixedBaseDuration extends TimedProcessBlockEntity
    {
        static void applyUpgrades(FixedBaseDuration blockEntity, LootContext context, Upgrades upgrades)
        {
            double newTicksPerOp = upgrades.runValueOps(LTXIUpgradeEffectComponents.TICKS_PER_OPERATION, context, blockEntity.getBaseTicksPerOperation());
            blockEntity.setTicksPerOperation(Math.max(0, LimaCoreMath.round(newTicksPerOp)));
        }

        int getBaseTicksPerOperation();
    }
}