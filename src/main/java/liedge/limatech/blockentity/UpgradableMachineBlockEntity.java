package liedge.limatech.blockentity;

import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.lib.upgrades.machine.MachineUpgrades;
import liedge.limatech.registry.LimaTechDataComponents;
import liedge.limatech.registry.LimaTechUpgradeEffectComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public interface UpgradableMachineBlockEntity extends SubMenuProviderBlockEntity
{
    static MachineUpgrades getMachineUpgradesFromItem(ItemStack stack)
    {
        return stack.getOrDefault(LimaTechDataComponents.MACHINE_UPGRADES, MachineUpgrades.EMPTY);
    }

    LimaItemHandlerBase getUpgradeModuleInventory();

    MachineUpgrades getUpgrades();

    void setUpgrades(MachineUpgrades upgrades);

    default void reloadUpgrades()
    {
        // Apply to energy holders, must run here since it is a compounding calculation
        if (this instanceof EnergyHolderBlockEntity energyHolder)
        {
            double newCapacity = getUpgrades().applySimpleValue(LimaTechUpgradeEffectComponents.ENERGY_CAPACITY, energyHolder.getBaseEnergyCapacity());
            double newTransferRate = getUpgrades().applySimpleValue(LimaTechUpgradeEffectComponents.ENERGY_TRANSFER_RATE, energyHolder.getBaseEnergyTransferRate());

            energyHolder.getEnergyStorage().setMaxEnergyStored(LimaMathUtil.round(newCapacity));
            energyHolder.getEnergyStorage().setTransferRate(LimaMathUtil.round(newTransferRate));
        }

        // Apply to timed process machines
        if (this instanceof TimedProcessMachineBlockEntity processMachine)
        {
            double newEnergyUsage = getUpgrades().applySimpleValue(LimaTechUpgradeEffectComponents.MACHINE_ENERGY_USAGE, processMachine.getBaseEnergyUsage());
            double newProcessingTime = getUpgrades().applySimpleValue(LimaTechUpgradeEffectComponents.TICKS_PER_OPERATION, processMachine.getBaseTicksPerOperation());

            processMachine.setEnergyUsage(LimaMathUtil.round(newEnergyUsage));
            processMachine.setTicksPerOperation(Math.max(0, Mth.floor(newProcessingTime)));
        }
    }
}