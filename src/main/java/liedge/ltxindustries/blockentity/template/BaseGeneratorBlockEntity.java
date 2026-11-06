package liedge.ltxindustries.blockentity.template;

import com.google.common.primitives.Ints;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.transfer.energy.LimaEnergyHandler;
import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.block.MachineState;
import liedge.ltxindustries.blockentity.base.EnergyGeneratorBlockEntity;
import liedge.ltxindustries.blockentity.base.ResourceType;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.util.LTXITooltipUtil;
import liedge.ltxindustries.util.SidedResourceUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;

public abstract class BaseGeneratorBlockEntity extends LTXIMachineBlockEntity implements EnergyGeneratorBlockEntity
{
    private boolean active;
    private int energyGeneration;

    protected BaseGeneratorBlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, 1, null);
    }

    @Override
    public int getEnergyGeneration()
    {
        return energyGeneration;
    }

    @Override
    public void setEnergyGeneration(int energyGeneration)
    {
        this.energyGeneration = energyGeneration;
    }

    protected boolean isActive()
    {
        return active;
    }

    protected void setActive(Level level, BlockPos pos, BlockState state, boolean active)
    {
        MachineState machineState = state.getValue(LTXIBlockProperties.BINARY_MACHINE_STATE);

        if (this.active != active || machineState.isActive() != active)
        {
            this.active = active;
            level.setBlockAndUpdate(pos, state.setValue(LTXIBlockProperties.BINARY_MACHINE_STATE, MachineState.of(active)));
        }
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        LimaEnergyHandler energy = getEnergy();
        SidedResourceUtil.pushEnergy(getIOConfiguration(ResourceType.ENERGY), getFacing(), energy, energy.getTransferRate(), this::getNeighborEnergyHandler);
    }

    @Override
    public final int getBaseEnergyTransferRate()
    {
        return Ints.saturatedCast(energyGeneration * 4L);
    }

    @Override
    public void appendStatsTooltips(TooltipLineConsumer consumer)
    {
        LTXITooltipUtil.appendEnergyGenerationTooltip(consumer, energyGeneration);
    }

    @Override
    public void onUpgradeRefresh(LootContext context, Upgrades upgrades)
    {
        EnergyGeneratorBlockEntity.onUpgradeRefresh(this, context, upgrades);

        super.onUpgradeRefresh(context, upgrades);
    }
}