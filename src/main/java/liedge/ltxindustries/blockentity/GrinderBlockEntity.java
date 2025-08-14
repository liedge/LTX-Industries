package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.recipe.GrindingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import static liedge.ltxindustries.util.config.LTXIMachinesConfig.*;

public class GrinderBlockEntity extends LimaRecipeMachineBlockEntity<GrindingRecipe>
{
    public GrinderBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.GRINDER.get(), LTXIRecipeTypes.GRINDING.get(), pos, state, 1, 3, 0, 0);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return GRINDER_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return GRINDER_ENERGY_USAGE.getAsInt();
    }

    @Override
    public int getBaseTicksPerOperation()
    {
        return GRINDER_CRAFTING_TIME.getAsInt();
    }
}