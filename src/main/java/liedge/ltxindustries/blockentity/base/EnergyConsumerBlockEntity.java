package liedge.ltxindustries.blockentity.base;

import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
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
        return getEnergyStorage().getEnergyStored() >= getEnergyUsage();
    }

    default boolean consumeUsageEnergy(boolean ignoreLimit)
    {
        return LimaEnergyUtil.consumeEnergy(getEnergyStorage(), getEnergyUsage(), ignoreLimit);
    }

    default void keepEnergyConsumerPropertiesSynced(DataWatcherHolder.DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getEnergyUsage, this::setEnergyUsage));
    }
}