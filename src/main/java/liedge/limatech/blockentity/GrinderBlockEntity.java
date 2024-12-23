package liedge.limatech.blockentity;

import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limatech.recipe.GrindingRecipe;
import liedge.limatech.registry.LimaTechMenus;
import liedge.limatech.registry.LimaTechRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static liedge.limatech.util.config.LimaTechMachinesConfig.*;

public class GrinderBlockEntity extends SimpleRecipeMachineBlockEntity<LimaRecipeInput, GrindingRecipe>
{
    public GrinderBlockEntity(LimaBlockEntityType type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, GRINDER_ENERGY_CAPACITY.getAsInt(), 3);
    }

    @Override
    public RecipeType<GrindingRecipe> machineRecipeType()
    {
        return LimaTechRecipeTypes.GRINDING.get();
    }

    @Override
    public int baseEnergyUsage()
    {
        return GRINDER_ENERGY_USAGE.getAsInt();
    }

    @Override
    public int baseCraftingTime()
    {
        return GRINDER_CRAFTING_TIME.getAsInt();
    }

    @Override
    protected LimaRecipeInput getRecipeInput(Level level)
    {
        return new LimaRecipeInput(getItemHandler(), 1, 1);
    }

    @Override
    protected boolean isInputSlot(int slot)
    {
        return slot == 1;
    }

    @Override
    protected int outputSlotIndex()
    {
        return 2;
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
}