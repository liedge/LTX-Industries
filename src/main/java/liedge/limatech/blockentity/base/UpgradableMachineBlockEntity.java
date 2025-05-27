package liedge.limatech.blockentity.base;

import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.lib.upgrades.machine.MachineUpgrades;
import liedge.limatech.registry.game.LimaTechUpgradeEffectComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public interface UpgradableMachineBlockEntity extends SubMenuProviderBlockEntity, ItemHolderBlockEntity
{
    String TAG_KEY_UPGRADES = "upgrades";
    String TAG_KEY_UPGRADE_SLOT_INVENTORY = "upgrade_slot";

    MachineUpgrades getUpgrades();

    void setUpgrades(MachineUpgrades upgrades);

    default boolean hasStatsTooltips()
    {
        return true;
    }

    default void appendStatsTooltips(TooltipLineConsumer consumer) { }

    default LootContext createUpgradeContext(ServerLevel level)
    {
        LimaBlockEntity self = getAsLimaBlockEntity();
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.BLOCK_STATE, self.getBlockState())
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(self.getBlockPos()))
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, self)
                .create(LootContextParamSets.BLOCK);

        return new LootContext.Builder(params).create(Optional.empty());
    }

    default void onUpgradeRefresh(LootContext context, MachineUpgrades upgrades)
    {
        // Apply to energy holders, must run here since it is a compounding calculation
        if (this instanceof EnergyHolderBlockEntity energyHolder)
        {
            double newCapacity = upgrades.applyValue(LimaTechUpgradeEffectComponents.ENERGY_CAPACITY, context, energyHolder.getBaseEnergyCapacity());
            double newTransferRate = upgrades.applyValue(LimaTechUpgradeEffectComponents.ENERGY_TRANSFER_RATE, context, energyHolder.getBaseEnergyTransferRate());

            energyHolder.getEnergyStorage().setMaxEnergyStored(LimaMathUtil.round(newCapacity));
            energyHolder.getEnergyStorage().setTransferRate(LimaMathUtil.round(newTransferRate));
        }
    }
}