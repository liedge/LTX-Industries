package liedge.ltxindustries.blockentity;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.ltxindustries.recipe.GrindingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIMenus;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static liedge.ltxindustries.util.config.LTXIMachinesConfig.*;

public class GrinderBlockEntity extends SimpleRecipeMachineBlockEntity<LimaRecipeInput, GrindingRecipe>
{
    public GrinderBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.GRINDER.get(), LTXIRecipeTypes.GRINDING.get(), pos, state, 3);
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

    @Override
    protected LimaRecipeInput getRecipeInput(Level level)
    {
        return LimaRecipeInput.createSingleSlot(getItemHandler(), 1);
    }

    @Override
    protected void consumeIngredients(LimaRecipeInput recipeInput, GrindingRecipe recipe, Level level)
    {
        recipe.consumeIngredientsStrictSlots(recipeInput, false, false);
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LTXIMenus.GRINDER.get();
    }

    @Override
    public boolean isInputSlot(int index)
    {
        return index == 1;
    }

    @Override
    public int getOutputSlot()
    {
        return 2;
    }
}