package liedge.limatech.blockentity;

import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.lib.upgrades.machine.MachineUpgrades;
import liedge.limatech.registry.game.LimaTechDataComponents;
import liedge.limatech.registry.game.LimaTechUpgradeEffectComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public interface UpgradableMachineBlockEntity extends SubMenuProviderBlockEntity, ItemHolderBlockEntity
{
    static MachineUpgrades getMachineUpgradesFromItem(ItemStack stack)
    {
        return stack.getOrDefault(LimaTechDataComponents.MACHINE_UPGRADES, MachineUpgrades.EMPTY);
    }

    MachineUpgrades getUpgrades();

    void setUpgrades(MachineUpgrades upgrades);

    default void onUpgradeRefresh(ServerLevel level)
    {
        LimaBlockEntity thisBE = getAsLimaBlockEntity();
        MachineUpgrades upgrades = getUpgrades();

        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.BLOCK_STATE, thisBE.getBlockState())
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(thisBE.getBlockPos()))
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, thisBE)
                .create(LootContextParamSets.BLOCK);
        LootContext context = new LootContext.Builder(params).create(Optional.empty());

        // Apply to energy holders, must run here since it is a compounding calculation
        if (this instanceof EnergyHolderBlockEntity energyHolder)
        {
            double newCapacity = upgrades.applyValue(LimaTechUpgradeEffectComponents.ENERGY_CAPACITY, context, energyHolder.getBaseEnergyCapacity());
            double newTransferRate = upgrades.applyValue(LimaTechUpgradeEffectComponents.ENERGY_TRANSFER_RATE, context, energyHolder.getBaseEnergyTransferRate());

            energyHolder.getEnergyStorage().setMaxEnergyStored(LimaMathUtil.round(newCapacity));
            energyHolder.getEnergyStorage().setTransferRate(LimaMathUtil.round(newTransferRate));
        }

        // Apply to timed process machines
        if (this instanceof TimedProcessMachineBlockEntity processMachine)
        {
            double newEnergyUsage = upgrades.applyValue(LimaTechUpgradeEffectComponents.MACHINE_ENERGY_USAGE, context, processMachine.getBaseEnergyUsage());
            double newProcessingTime = upgrades.applyValue(LimaTechUpgradeEffectComponents.TICKS_PER_OPERATION, context, processMachine.getBaseTicksPerOperation());

            processMachine.setEnergyUsage(LimaMathUtil.round(newEnergyUsage));
            processMachine.setTicksPerOperation(Math.max(0, Mth.floor(newProcessingTime)));
        }
    }
}