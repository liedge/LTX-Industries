package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.recipe.MaterialFusingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import static liedge.ltxindustries.util.config.LTXIMachinesConfig.*;

public class MaterialFusingChamberBlockEntity extends LimaRecipeMachineBlockEntity<MaterialFusingRecipe>
{
    public MaterialFusingChamberBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.MATERIAL_FUSING_CHAMBER.get(), LTXIRecipeTypes.MATERIAL_FUSING.get(), pos, state, 3, 1, 1, 0);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return MFC_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return MFC_ENERGY_USAGE.getAsInt();
    }

    @Override
    public int getBaseTicksPerOperation()
    {
        return MFC_CRAFTING_TIME.getAsInt();
    }
}