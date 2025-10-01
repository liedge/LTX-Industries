package liedge.ltxindustries.blockentity;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.capability.fluid.LimaFluidHandler;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import liedge.ltxindustries.recipe.LTXIRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public abstract class LTXIRecipeMachineBlockEntity<R extends LTXIRecipe> extends StateBlockRecipeMachineBlockEntity<LimaRecipeInput, R>
{
    protected LTXIRecipeMachineBlockEntity(ConfigurableIOBlockEntityType<?> type, RecipeType<R> recipeType, BlockPos pos, BlockState state, int inputSlots, int outputSlots, int inputTanks, int outputTanks)
    {
        super(type, recipeType, pos, state, inputSlots, outputSlots, inputTanks, outputTanks);
    }

    @Override
    protected LimaRecipeInput getRecipeInput(Level level)
    {
        return LimaRecipeInput.create(getItemHandler(BlockContentsType.INPUT), getFluidHandler(BlockContentsType.INPUT));
    }

    @Override
    protected int getBaseRecipeCraftingTime(R recipe)
    {
        return recipe.getCraftTime();
    }

    @Override
    protected void consumeIngredients(LimaRecipeInput recipeInput, R recipe, Level level)
    {
        recipe.consumeItemIngredients(recipeInput);
        recipe.consumeFluidIngredients(recipeInput);
    }

    @Override
    public boolean canInsertRecipeResults(Level level, R recipe)
    {
        // Check item results
        List<ItemStack> results = recipe.getPossibleItemResults();
        boolean itemCheck = switch (results.size())
        {
            case 0 -> true;
            case 1 -> ItemHandlerHelper.insertItem(getOutputInventory(), results.getFirst(), true).isEmpty();
            default ->
            {
                ItemStackHandler interim = getOutputInventory().copyHandler();
                for (ItemStack stack : results)
                {
                    if (!ItemHandlerHelper.insertItem(interim, stack, false).isEmpty()) yield false;
                }
                yield true;
            }
        };

        // Check fluid results
        List<FluidStack> fluidResults = recipe.getFluidResults();
        boolean fluidCheck = switch (fluidResults.size())
        {
            case 0 -> true;
            case 1 ->
            {
                FluidStack first = fluidResults.getFirst();
                yield getFluidHandlerOrThrow(BlockContentsType.OUTPUT).fillAny(first, IFluidHandler.FluidAction.SIMULATE, true) == first.getAmount();
            }
            default ->
            {
                LimaFluidHandler interim = getFluidHandlerOrThrow(BlockContentsType.OUTPUT).copyHandler();
                for (FluidStack stack : fluidResults)
                {
                    if (interim.fillAny(stack, IFluidHandler.FluidAction.EXECUTE, true) != stack.getAmount())
                        yield false;
                }
                yield true;
            }
        };

        return itemCheck && fluidCheck;
    }

    @Override
    protected void insertRecipeResults(Level level, R recipe, LimaRecipeInput recipeInput)
    {
        // Insert item results
        List<ItemStack> results = recipe.generateItemResults(recipeInput, level.registryAccess(), level.random);
        for (ItemStack stack : results)
        {
            ItemHandlerHelper.insertItem(getOutputInventory(), stack, false);
        }

        // Insert fluid results
        LimaFluidHandler outputFluids = getFluidHandler(BlockContentsType.OUTPUT);
        if (outputFluids != null)
        {
            List<FluidStack> fluidResults = recipe.generateFluidResults(recipeInput, level.registryAccess());
            for (FluidStack stack : fluidResults)
            {
                outputFluids.fillAny(stack, IFluidHandler.FluidAction.EXECUTE, true);
            }
        }
    }
}