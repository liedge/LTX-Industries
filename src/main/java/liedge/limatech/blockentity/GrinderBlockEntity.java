package liedge.limatech.blockentity;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limatech.recipe.GrindingRecipe;
import liedge.limatech.registry.game.LimaTechBlockEntities;
import liedge.limatech.registry.game.LimaTechMenus;
import liedge.limatech.registry.game.LimaTechRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static liedge.limatech.util.config.LimaTechMachinesConfig.*;

public class GrinderBlockEntity extends SimpleRecipeMachineBlockEntity<LimaRecipeInput, GrindingRecipe>
{
    public GrinderBlockEntity(BlockPos pos, BlockState state)
    {
        super(LimaTechBlockEntities.GRINDER.get(), LimaTechRecipeTypes.GRINDING.get(), pos, state, GRINDER_ENERGY_CAPACITY.getAsInt(), 3);
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
        return LimaTechMenus.GRINDER.get();
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