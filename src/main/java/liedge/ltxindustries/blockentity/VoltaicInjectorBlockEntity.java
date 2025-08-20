package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.recipe.EnergizingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class VoltaicInjectorBlockEntity extends LTXIRecipeMachineBlockEntity<EnergizingRecipe>
{
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
}