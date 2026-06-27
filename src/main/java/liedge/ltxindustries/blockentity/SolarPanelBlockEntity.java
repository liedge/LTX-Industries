package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.blockentity.template.BaseGeneratorBlockEntity;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class SolarPanelBlockEntity extends BaseGeneratorBlockEntity
{
    public SolarPanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.SOLAR_PANEL.get(), pos, state);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.SOLAR_PANEL_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyGeneration()
    {
        return LTXIMachinesConfig.SOLAR_PANEL_GENERATION.getAsInt();
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        if (level.getGameTime() % 20L == 0)
        {
            boolean active = level.dimensionType().hasSkyLight() && level.getSkyDarken() < 4 && level.canSeeSky(pos);
            setActive(level, pos, state, active);
        }

        EnergyHandler energy = getEnergy();
        if (isActive() && !EnergyHandlerUtil.isFull(energy))
        {
            try (Transaction tx = Transaction.openRoot())
            {
                energy.insert(getEnergyGeneration(), tx);
                tx.commit();
            }
        }

        pushEnergyToSides();
    }
}