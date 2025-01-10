package liedge.limatech.blockentity;

import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import liedge.limatech.lib.upgradesystem.calculation.CompoundCalculation;
import liedge.limatech.lib.upgradesystem.machine.MachineUpgrades;
import liedge.limatech.lib.upgradesystem.machine.effect.ModifyEnergyStorageUpgradeEffect;
import liedge.limatech.registry.LimaTechDataComponents;
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
            int newCapacity = CompoundCalculation.runStepsAsInt(energyHolder.getBaseEnergyCapacity(), getUpgrades().flatMapToSortedCalculations(ModifyEnergyStorageUpgradeEffect.class, ModifyEnergyStorageUpgradeEffect::capacityModifier));
            int newTransferRate = CompoundCalculation.runStepsAsInt(energyHolder.getBaseEnergyTransferRate(), getUpgrades().flatMapToSortedCalculations(ModifyEnergyStorageUpgradeEffect.class, ModifyEnergyStorageUpgradeEffect::transferRateModifier));

            energyHolder.getEnergyStorage().setMaxEnergyStored(newCapacity);
            energyHolder.getEnergyStorage().setTransferRate(newTransferRate);
        }

        getUpgrades().forEachEffect((effect, upgradeRank) -> effect.onUpgradeReload(this, upgradeRank));
    }
}