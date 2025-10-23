package liedge.ltxindustries.blockentity.template;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.capability.fluid.LimaFluidHandler;
import liedge.limacore.recipe.result.ItemResult;
import liedge.limacore.util.LimaNbtUtil;
import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.block.MachineState;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.recipe.LTXIRecipe;
import liedge.ltxindustries.recipe.LTXIRecipeInput;
import liedge.ltxindustries.recipe.RecipeMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
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
    public RecipeType<?> getRecipeTypeForMode()
    {
        return getRecipeCheck().getRecipeType();
    }

    @Override
    protected LTXIRecipeInput getRecipeInput(Level level)
    {
        return new LTXIRecipeInput(getItemHandler(BlockContentsType.INPUT), getFluidHandler(BlockContentsType.INPUT), mode);
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
        recipe.consumeFluidIngredients(recipeInput);
    }

    @Override
    public boolean canInsertRecipeResults(Level level, R recipe)
    {
        // Check item results
        List<ItemResult> itemResults = recipe.getItemResults();
        boolean itemCheck = switch (itemResults.size())
        {
            case 0 -> true;
            case 1 -> ItemHandlerHelper.insertItem(getOutputInventory(), itemResults.getFirst().getMaximumResult(), true).isEmpty();
            default ->
            {
                ItemStackHandler interim = getOutputInventory().copyHandler();
                for (ItemResult result : itemResults)
                {
                    ItemStack maxOutput = result.getMaximumResult();
                    if (result.requiredOutput() && !ItemHandlerHelper.insertItem(interim, maxOutput, false).isEmpty()) yield false;
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
    protected void insertRecipeResults(Level level, R recipe, LTXIRecipeInput recipeInput)
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

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        this.mode = LimaNbtUtil.tryDecode(RecipeMode.CODEC, RegistryOps.create(NbtOps.INSTANCE, registries), tag, TAG_KEY_RECIPE_MODE);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        LimaNbtUtil.tryEncodeTo(RecipeMode.CODEC, RegistryOps.create(NbtOps.INSTANCE, registries), this.mode, tag, TAG_KEY_RECIPE_MODE);
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