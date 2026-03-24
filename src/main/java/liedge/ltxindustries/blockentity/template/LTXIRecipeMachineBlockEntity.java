package liedge.ltxindustries.blockentity.template;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.block.MachineState;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.recipe.LTXIRecipe;
import liedge.ltxindustries.recipe.LTXIRecipeInput;
import liedge.ltxindustries.recipe.RecipeMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class LTXIRecipeMachineBlockEntity<R extends LTXIRecipe> extends BaseRecipeMachineBlockEntity<LTXIRecipeInput, R> implements RecipeModeHolderBlockEntity
{
    @Nullable
    private Holder<RecipeMode> mode;

    protected LTXIRecipeMachineBlockEntity(ConfigurableIOBlockEntityType<?> type, RecipeType<R> recipeType, BlockPos pos, BlockState state, int inputSlots, int outputSlots, int inputTanks, int outputTanks)
    {
        super(type, recipeType, pos, state, inputSlots, outputSlots, inputTanks, outputTanks);
    }

    @Override
    public @Nullable Holder<RecipeMode> getMode()
    {
        return mode;
    }

    @Override
    public void setMode(@Nullable Holder<RecipeMode> mode)
    {
        this.mode = mode;

        if (checkServerSide())
        {
            setChanged();
            reCheckRecipe();
        }
    }

    @Override
    public Holder<RecipeType<?>> getRecipeTypeHolder()
    {
        return BuiltInRegistries.RECIPE_TYPE.wrapAsHolder(getRecipeCheck().getRecipeType());
    }

    @Override
    protected LTXIRecipeInput getRecipeInput(Level level)
    {
        return new LTXIRecipeInput(getItems(BlockContentsType.INPUT), getFluids(BlockContentsType.INPUT), mode);
    }

    @Override
    protected int getBaseRecipeCraftingTime(R recipe)
    {
        return recipe.getCraftTime();
    }

    @Override
    protected void consumeIngredients(LTXIRecipeInput recipeInput, R recipe, Level level)
    {
        recipe.consumeItemIngredients(recipeInput, level.getRandom());
        recipe.consumeFluidIngredients(recipeInput, level.getRandom());
    }

    @Override
    public boolean canInsertRecipeResults(ServerLevel level, R recipe, LTXIRecipeInput input)
    {
        boolean itemsCheck = canInsertResourceResults(recipe.getItemResults(), getItems(BlockContentsType.OUTPUT));
        boolean fluidsCheck = canInsertResourceResults(recipe.getFluidResults(), getFluids(BlockContentsType.OUTPUT));

        return itemsCheck && fluidsCheck;
    }

    @Override
    protected void insertRecipeResults(Level level, R recipe, LTXIRecipeInput recipeInput)
    {
        // Insert item results
        List<ResourceStack<ItemResource>> itemResults = recipe.generateItemResults(recipeInput, level.registryAccess(), level.random);
        insertResourceResults(itemResults, getItems(BlockContentsType.OUTPUT));

        // Insert fluid results
        List<ResourceStack<FluidResource>> fluidResults = recipe.generateFluidResults(recipeInput, level.registryAccess(), level.random);
        insertResourceResults(fluidResults, getFluids(BlockContentsType.OUTPUT));
    }

    @Override
    protected void craftRecipe(ServerLevel level, R recipe, int maxOperations)
    {
        LTXIRecipeInput input = getRecipeInput(level);

        boolean a = recipe.getItemIngredients().stream().allMatch(o -> o.getConsumeChance() == 0f);
        boolean b = recipe.getFluidIngredients().stream().allMatch(o -> o.getConsumeChance() == 0f);
        boolean skipInputCheck = a && b;

        for (int i = 0 ; i < maxOperations; i++)
        {
            if (i > 0)
            {
                boolean canContinue = (skipInputCheck || recipe.matches(input, level)) && canInsertRecipeResults(level, recipe, input);
                if (!canContinue) break;
            }

            insertRecipeResults(level, recipe, input);
            consumeIngredients(input, recipe, level);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        this.mode = input.read(TAG_KEY_RECIPE_MODE, RecipeMode.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        output.storeNullable(TAG_KEY_RECIPE_MODE, RecipeMode.CODEC, mode);
    }

    public static abstract class StateMachine<R extends LTXIRecipe> extends LTXIRecipeMachineBlockEntity<R>
    {
        protected StateMachine(ConfigurableIOBlockEntityType<?> type, RecipeType<R> recipeType, BlockPos pos, BlockState state, int inputSlots, int outputSlots, int inputTanks, int outputTanks)
        {
            super(type, recipeType, pos, state, inputSlots, outputSlots, inputTanks, outputTanks);
        }

        @Override
        protected void onCraftingStateChanged(boolean newCraftingState)
        {
            BlockState newState = getBlockState().setValue(LTXIBlockProperties.BINARY_MACHINE_STATE, MachineState.of(newCraftingState));
            nonNullLevel().setBlockAndUpdate(getBlockPos(), newState);
        }
    }
}