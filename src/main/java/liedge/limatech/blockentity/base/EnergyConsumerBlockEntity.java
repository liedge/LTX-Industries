package liedge.limatech.blockentity.base;

import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.lib.upgrades.machine.MachineUpgrades;
import liedge.limatech.registry.game.LimaTechUpgradeEffectComponents;
import net.minecraft.world.level.storage.loot.LootContext;

public interface EnergyConsumerBlockEntity
{
    static void applyUpgrades(EnergyConsumerBlockEntity blockEntity, LootContext context, MachineUpgrades upgrades)
    {
        double newEnergyUsage = upgrades.applyValue(LimaTechUpgradeEffectComponents.ENERGY_USAGE, context, blockEntity.getBaseEnergyUsage());
        blockEntity.setEnergyUsage(Math.max(0, LimaMathUtil.round(newEnergyUsage)));
    }

    int getBaseEnergyUsage();

    int getEnergyUsage();

    void setEnergyUsage(int energyUsage);

    default void keepEnergyConsumerPropertiesSynced(DataWatcherHolder.DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getEnergyUsage, this::setEnergyUsage));
    }
}