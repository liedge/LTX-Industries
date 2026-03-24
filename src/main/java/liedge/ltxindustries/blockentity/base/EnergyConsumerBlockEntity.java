package liedge.ltxindustries.blockentity.base;

import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.transfer.LimaEnergyUtil;
import liedge.limacore.transfer.energy.EnergyHolderBlockEntity;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.world.level.storage.loot.LootContext;

public interface EnergyConsumerBlockEntity extends EnergyHolderBlockEntity
{
    static void applyUpgrades(EnergyConsumerBlockEntity blockEntity, LootContext context, MachineUpgrades upgrades)
    {
        double newEnergyUsage = upgrades.runValueOps(LTXIUpgradeEffectComponents.ENERGY_USAGE, context, blockEntity.getBaseEnergyUsage());
        blockEntity.setEnergyUsage(Math.max(0, LimaCoreMath.round(newEnergyUsage)));
    }

    int getBaseEnergyUsage();

    int getEnergyUsage();

    void setEnergyUsage(int energyUsage);

    default boolean hasMinimumEnergy()
    {
        return getEnergy().getAmountAsInt() >= getEnergyUsage();
    }

    default boolean consumeUsageEnergy()
    {
        return LimaEnergyUtil.useExact(getEnergy(), getEnergyUsage(), null);
    }

    default void keepEnergyConsumerPropertiesSynced(DataWatcherHolder.DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getEnergyUsage, this::setEnergyUsage));
    }
}