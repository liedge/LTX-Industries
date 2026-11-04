package liedge.ltxindustries.blockentity.base;

import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.network.sync.SimpleValueTracker;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.world.level.storage.loot.LootContext;

public interface EnergyGeneratorBlockEntity
{
    static void onUpgradeRefresh(EnergyGeneratorBlockEntity blockEntity, LootContext context, Upgrades upgrades)
    {
        int newGeneration = upgrades.runRoundedIntValueOps(LTXIUpgradeEffectComponents.ENERGY_GENERATION, context, 0, blockEntity.getBaseEnergyGeneration());
        blockEntity.setEnergyGeneration(newGeneration);
    }

    int getBaseEnergyGeneration();

    int getEnergyGeneration();

    void setEnergyGeneration(int energyGeneration);

    default void syncEnergyGeneration(DataWatcherHolder holder)
    {
        holder.addDataWatcher(SimpleValueTracker.create(LimaCoreNetworkSerializers.VAR_INT, this::getEnergyGeneration, this::setEnergyGeneration).setAutomatic());
    }
}