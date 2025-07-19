package liedge.ltxindustries.blockentity;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.ltxindustries.recipe.RecomposingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIMenus;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static liedge.ltxindustries.util.config.LTXIMachinesConfig.*;

public class RecomposerBlockEntity extends SimpleRecipeMachineBlockEntity<LimaRecipeInput, RecomposingRecipe>
{
    public RecomposerBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.RECOMPOSER.get(), LTXIRecipeTypes.RECOMPOSING.get(), pos, state, 3);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return RECOMPOSER_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return RECOMPOSER_ENERGY_USAGE.getAsInt();
    }

    @Override
    public int getBaseTicksPerOperation()
    {
        return RECOMPOSER_CRAFTING_TIME.getAsInt();
    }

    @Override
    protected LimaRecipeInput getRecipeInput(Level level)
    {
        return LimaRecipeInput.createSingleSlot(getItemHandler(), 1);
    }

    @Override
    public boolean isInputSlot(int slot)
    {
        return slot == 1;
    }

    @Override
    public int getOutputSlot()
    {
        return 2;
    }

    @Override
    protected void consumeIngredients(LimaRecipeInput recipeInput, RecomposingRecipe recipe, Level level)
    {
        recipe.consumeIngredientsStrictSlots(recipeInput, false, false);
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LTXIMenus.RECOMPOSER.get();
    }
}
