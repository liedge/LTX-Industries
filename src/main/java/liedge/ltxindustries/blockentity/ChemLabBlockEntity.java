package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.recipe.ChemicalReactingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ChemLabBlockEntity extends LimaRecipeMachineBlockEntity<ChemicalReactingRecipe>
{
    public ChemLabBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.CHEM_LAB.get(), LTXIRecipeTypes.CHEMICAL_REACTING.get(), pos, state, 3, 2, 3, 2);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.CHEM_LAB_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.CHEM_LAB_ENERGY_USAGE.getAsInt();
    }

    @Override
    public int getBaseTicksPerOperation()
    {
        return LTXIMachinesConfig.CHEM_LAB_CRAFTING_TIME.getAsInt();
    }
}