package liedge.limatech.blockentity;

import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.lib.upgrades.machine.MachineUpgrades;
import liedge.limatech.registry.LimaTechDataComponents;
import liedge.limatech.registry.LimaTechUpgradeEffectComponents;
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
            double newCapacity = getUpgrades().runCompoundOps(LimaTechUpgradeEffectComponents.ENERGY_CAPACITY, null, null, energyHolder.getBaseEnergyCapacity());
            double newTransferRate = getUpgrades().runCompoundOps(LimaTechUpgradeEffectComponents.ENERGY_TRANSFER_RATE, null, null, energyHolder.getBaseEnergyTransferRate());

            energyHolder.getEnergyStorage().setMaxEnergyStored(LimaMathUtil.round(newCapacity));
            energyHolder.getEnergyStorage().setTransferRate(LimaMathUtil.round(newTransferRate));
        }

        // Apply to timed process machines
        if (this instanceof TimedProcessMachineBlockEntity processMachine)
        {
            double newEnergyUsage = getUpgrades().runCompoundOps(LimaTechUpgradeEffectComponents.MACHINE_ENERGY_USAGE, null, null, processMachine.getBaseEnergyUsage());
            double newProcessingTime = getUpgrades().runCompoundOps(LimaTechUpgradeEffectComponents.TICKS_PER_OPERATION, null, null, processMachine.getBaseTicksPerOperation());

            processMachine.setEnergyUsage(LimaMathUtil.round(newEnergyUsage));
            processMachine.setTicksPerOperation(Math.max(0, LimaMathUtil.round(newProcessingTime, LimaMathUtil.RoundingStrategy.FLOOR)));
        }
    }
}