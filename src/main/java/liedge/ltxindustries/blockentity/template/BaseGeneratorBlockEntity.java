package liedge.ltxindustries.blockentity.template;

import com.google.common.primitives.Ints;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.network.sync.SimpleValueTracker;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.block.MachineState;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;

public abstract class BaseGeneratorBlockEntity extends LTXIMachineBlockEntity
{
    private boolean active;
    private int energyGeneration;

    protected BaseGeneratorBlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, 1, null);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector) { }

    public abstract int getBaseEnergyGeneration();

    public int getEnergyGeneration()
    {
        return energyGeneration;
    }

    public void setEnergyGeneration(int energyGeneration)
    {
        this.energyGeneration = energyGeneration;
    }

    public LimaDataWatcher<Integer> syncEnergyGeneration()
    {
        return SimpleValueTracker.create(LimaCoreNetworkSerializers.VAR_INT, this::getEnergyGeneration, this::setEnergyGeneration).setAutomatic();
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
    public final int getBaseEnergyTransferRate()
    {
        return Ints.saturatedCast(getBaseEnergyGeneration() * 4L);
    }

    @Override
    public void appendStatsTooltips(TooltipLineConsumer consumer)
    {
        LTXITooltipUtil.appendEnergyGenerationTooltip(consumer, energyGeneration);
    }

    @Override
    public void onUpgradeRefresh(LootContext context, Upgrades upgrades)
    {
        double newGeneration = upgrades.runValueOps(LTXIUpgradeEffectComponents.ENERGY_GENERATION, context, getBaseEnergyGeneration());
        setEnergyGeneration(LimaCoreMath.roundInt(newGeneration));

        super.onUpgradeRefresh(context, upgrades);
    }
}