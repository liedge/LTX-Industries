package liedge.ltxindustries.blockentity;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.capability.fluid.FluidHolderBlockEntity;
import liedge.limacore.capability.fluid.LimaBlockEntityFluidHandler;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.blockentity.base.IOController;
import liedge.ltxindustries.blockentity.base.SidedAccessBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class LimaRecipeMachineBlockEntity<R extends LimaCustomRecipe<LimaRecipeInput>> extends StateBlockRecipeMachineBlockEntity<LimaRecipeInput, R> implements FluidHolderBlockEntity
{
    private final @Nullable LimaBlockEntityFluidHandler inputFluids;
    private final @Nullable LimaBlockEntityFluidHandler outputFluids;
    private final @Nullable IOController fluidController;

    protected LimaRecipeMachineBlockEntity(SidedAccessBlockEntityType<?> type, RecipeType<R> recipeType, BlockPos pos, BlockState state, int inputSlots, int outputSlots, int inputTanks, int outputTanks)
    {
        super(type, recipeType, pos, state, inputSlots, outputSlots);

        this.inputFluids = inputTanks > 0 ? new LimaBlockEntityFluidHandler(this, inputTanks, BlockContentsType.INPUT) : null;
        this.outputFluids = outputTanks > 0 ? new LimaBlockEntityFluidHandler(this, outputTanks, BlockContentsType.OUTPUT) : null;
        this.fluidController = inputFluids != null || outputFluids != null ? new IOController(this, BlockEntityInputType.FLUIDS) : null;
    }

    protected LimaRecipeMachineBlockEntity(SidedAccessBlockEntityType<?> type, RecipeType<R> recipeType, BlockPos pos, BlockState state, int inputSlots, int outputSlots)
    {
        this(type, recipeType, pos, state, inputSlots, outputSlots, 0, 0);
    }

    @Override
    public @Nullable LimaBlockEntityFluidHandler getFluidHandler(BlockContentsType contentsType)
    {
        return switch (contentsType)
        {
            case INPUT -> inputFluids;
            case OUTPUT -> outputFluids;
            default -> null;
        };
    }

    @Override
    protected IOController getFluidIOController()
    {
        return fluidController != null ? fluidController : super.getFluidIOController();
    }

    @Override
    public void onFluidsChanged(BlockContentsType contentsType, int tank)
    {
        setChanged();
        this.shouldCheckRecipe = true;
    }

    @Override
    public int getBaseFluidCapacity(BlockContentsType contentsType, int tank)
    {
        return contentsType == BlockContentsType.INPUT ? 32000 : 64000;
    }

    @Override
    public int getBaseFluidTransferRate(BlockContentsType contentsType, int tank)
    {
        return 8000;
    }

    @Override
    public boolean isValidFluid(BlockContentsType contentsType, int tank, FluidStack stack)
    {
        return true;
    }

    @Override
    public @Nullable IFluidHandler createFluidIOWrapper(@Nullable Direction side)
    {
        return wrapInputOutputTanks(side);
    }

    @Override
    protected LimaRecipeInput getRecipeInput(Level level)
    {
        return LimaRecipeInput.create(getItemHandler(BlockContentsType.INPUT), inputFluids);
    }

    @Override
    protected void consumeIngredients(LimaRecipeInput recipeInput, R recipe, Level level)
    {
        recipe.consumeItemIngredients(recipeInput, false);
        recipe.consumeFluidIngredients(recipeInput, IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public boolean canInsertRecipeResults(Level level, R recipe)
    {
        // Check item results
        List<ItemStack> results = recipe.getPossibleItemResults();
        for (ItemStack stack : results)
        {
            if (!ItemHandlerHelper.insertItem(getOutputInventory(), stack, true).isEmpty())
                return false;
        }

        // Check fluid results
        if (outputFluids != null)
        {
            List<FluidStack> fluidResults = recipe.getFluidResults();
            for (FluidStack stack : fluidResults)
            {
                if (outputFluids.fillAny(stack, IFluidHandler.FluidAction.SIMULATE, true) != stack.getAmount())
                    return false;
            }
        }

        return true;
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

        if (tag.contains(LimaCommonConstants.KEY_FLUID_TANKS, Tag.TAG_COMPOUND))
        {
            CompoundTag tanksTag = tag.getCompound(LimaCommonConstants.KEY_FLUID_TANKS);
            for (BlockContentsType type : BlockContentsType.values())
            {
                LimaBlockEntityFluidHandler handler = getFluidHandler(type);
                if (handler != null && tanksTag.contains(type.getSerializedName())) handler.deserializeNBT(registries, tanksTag.getList(type.getSerializedName(), Tag.TAG_COMPOUND));
            }
        }

        if (fluidController != null) fluidController.deserializeNBT(registries, tag.getCompound(KEY_FLUID_IO));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);

        CompoundTag tanksTag = new CompoundTag();
        for (BlockContentsType type : BlockContentsType.values())
        {
            LimaBlockEntityFluidHandler handler = getFluidHandler(type);
            if (handler != null) tanksTag.put(type.getSerializedName(), handler.serializeNBT(registries));
        }
        if (!tanksTag.isEmpty()) tag.put(LimaCommonConstants.KEY_FLUID_TANKS, tanksTag);

        if (fluidController != null) tag.put(KEY_FLUID_IO, fluidController.serializeNBT(registries));
    }
}