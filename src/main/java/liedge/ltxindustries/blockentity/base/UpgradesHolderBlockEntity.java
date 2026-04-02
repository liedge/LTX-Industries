package liedge.ltxindustries.blockentity.base;

import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.transfer.energy.EnergyHolderBlockEntity;
import liedge.limacore.transfer.item.ItemHolderBlockEntity;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public interface UpgradesHolderBlockEntity extends SubMenuProviderBlockEntity, ItemHolderBlockEntity
{
    Upgrades getUpgrades();

    void setUpgrades(Upgrades upgrades);

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

    default void onUpgradeRefresh(LootContext context, Upgrades upgrades)
    {
        // Apply to energy holders, must run here since it is a compounding calculation
        if (this instanceof EnergyHolderBlockEntity energyHolder)
        {
            double newCapacity = upgrades.runValueOps(LTXIUpgradeEffectComponents.ENERGY_CAPACITY, context, energyHolder.getBaseEnergyCapacity());
            double newTransferRate = upgrades.runValueOps(LTXIUpgradeEffectComponents.ENERGY_TRANSFER_RATE, context, energyHolder.getBaseEnergyTransferRate());
            energyHolder.getEnergy().setCapacity(LimaCoreMath.round(newCapacity));
            energyHolder.getEnergy().setTransferRate(LimaCoreMath.round(newTransferRate));
        }
    }
}