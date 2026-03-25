package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.blockentity.template.LTXIRecipeMachineBlockEntity;
import liedge.ltxindustries.client.model.custom.EnergyBoltData;
import liedge.ltxindustries.recipe.EnergizingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class VoltaicInjectorBlockEntity extends LTXIRecipeMachineBlockEntity.StateMachine<EnergizingRecipe>
{
    public @Nullable EnergyBoltData platformBolt;

    public VoltaicInjectorBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.VOLTAIC_INJECTOR.get(), LTXIRecipeTypes.ENERGIZING.get(), pos, state, 1, 1, 0, 0);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.VOLTAIC_INJECTOR_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.VOLTAIC_INJECTOR_ENERGY_USAGE.getAsInt();
    }

    @Override
    protected void tickClient(Level level, BlockPos pos, BlockState state)
    {
        if (state.getValue(LTXIBlockProperties.BINARY_MACHINE_STATE).isActive())
        {
            platformBolt = EnergyBoltData.create(0, 0, 0, 0, 0.5625d, 0, 0.0104167f, 0.05f, level.getRandom());
        }
        else
        {
            platformBolt = null;
        }
    }
}