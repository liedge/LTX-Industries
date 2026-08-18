package liedge.ltxindustries.blockentity.base;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.network.sync.SimpleValueTracker;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.util.LTXIUpgradeUtil;
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
        collector.register(SimpleValueTracker.create(LimaCoreNetworkSerializers.VAR_INT, this::getCurrentProcessTime, this::setCurrentProcessTime).setAutomatic());
        collector.register(SimpleValueTracker.create(LimaCoreNetworkSerializers.VAR_INT, this::getTicksPerOperation, this::setTicksPerOperation).setAutomatic());
    }

    interface FixedBaseDuration extends TimedProcessBlockEntity
    {
        static void applyUpgrades(FixedBaseDuration blockEntity, LootContext context, Upgrades upgrades)
        {
            int newTicksPerOp = LTXIUpgradeUtil.calculateMachineSpeed(upgrades, context, blockEntity.getBaseTicksPerOperation());
            blockEntity.setTicksPerOperation(newTicksPerOp);
        }

        int getBaseTicksPerOperation();

        default void appendOperationTicksTooltip(TooltipLineConsumer consumer)
        {
            consumer.accept(LTXILangKeys.MACHINE_TICKS_PER_OP_TOOLTIP.translateArgs(getTicksPerOperation()));
        }
    }
}