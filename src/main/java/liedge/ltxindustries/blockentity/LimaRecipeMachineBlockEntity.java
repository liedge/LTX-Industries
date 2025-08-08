package liedge.ltxindustries.blockentity;

import liedge.limacore.capability.fluid.FluidHolderBlockEntity;
import liedge.limacore.capability.fluid.LimaBlockEntityFluidHandler;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.blockentity.base.IOController;
import liedge.ltxindustries.blockentity.base.SidedAccessBlockEntityType;
import net.minecraft.core.BlockPos;
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
import java.util.Objects;

import static liedge.limacore.LimaCommonConstants.KEY_FLUID_TANKS;

public abstract class LimaRecipeMachineBlockEntity<R extends LimaCustomRecipe<LimaRecipeInput>> extends StateBlockRecipeMachineBlockEntity<LimaRecipeInput, R> implements FluidHolderBlockEntity
{
    private final @Nullable LimaBlockEntityFluidHandler fluidHandler;
    private final @Nullable IOController fluidController;

    protected LimaRecipeMachineBlockEntity(SidedAccessBlockEntityType<?> type, RecipeType<R> recipeType, BlockPos pos, BlockState state, int inputSlots, int outputSlots, int fluidTanks)
    {
        super(type, recipeType, pos, state, inputSlots, outputSlots);
        if (fluidTanks > 0)
        {
            this.fluidHandler = new LimaBlockEntityFluidHandler(this, fluidTanks);
            this.fluidController = new IOController(this, BlockEntityInputType.FLUIDS);
        }
        else
        {
            this.fluidHandler = null;
            this.fluidController = null;
        }
    }

    protected LimaRecipeMachineBlockEntity(SidedAccessBlockEntityType<?> type, RecipeType<R> recipeType, BlockPos pos, BlockState state, int inputSlots, int outputSlots)
    {
        this(type, recipeType, pos, state, inputSlots, outputSlots, 0);
    }

    @Override
    public LimaBlockEntityFluidHandler getFluidHandler()
    {
        return Objects.requireNonNull(fluidHandler, "Machine does not support fluid handling.");
    }

    @Override
    protected IOController getFluidIOController()
    {
        return fluidController != null ? fluidController : super.getFluidIOController();
    }

    @Override
    public void onFluidsChanged(int tank)
    {
        setChanged();
        this.shouldCheckRecipe = true;
    }

    @Override
    public int getBaseFluidCapacity(int tank)
    {
        return 32000;
    }

    @Override
    public int getBaseFluidTransferRate(int tank)
    {
        return 8000;
    }

    @Override
    public boolean isValidFluid(int tank, FluidStack fluidStack)
    {
        return true;
    }

    @Override
    protected LimaRecipeInput getRecipeInput(Level level)
    {
        return fluidHandler != null ? LimaRecipeInput.of(getInputInventory(), fluidHandler) : LimaRecipeInput.of(getInputInventory());
    }

    @Override
    protected void consumeIngredients(LimaRecipeInput recipeInput, R recipe, Level level)
    {
        recipe.consumeItemIngredients(recipeInput, false);
        if (fluidHandler != null) recipe.consumeFluidIngredients(recipeInput, IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public boolean canInsertRecipeResults(Level level, R recipe)
    {
        List<ItemStack> results = recipe.getPossibleItemResults();

        for (ItemStack stack : results)
        {
            if (!ItemHandlerHelper.insertItem(getOutputInventory(), stack, true).isEmpty())
                return false;
        }

        return true;
    }

    @Override
    protected void insertRecipeResults(Level level, R recipe, LimaRecipeInput recipeInput)
    {
        List<ItemStack> results = recipe.generateItemResults(recipeInput, level.registryAccess(), level.random);

        for (ItemStack stack : results)
        {
            ItemHandlerHelper.insertItem(getOutputInventory(), stack, false);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);

        if (fluidHandler != null && fluidController != null)
        {
            fluidHandler.deserializeNBT(registries, tag.getList(KEY_FLUID_TANKS, Tag.TAG_COMPOUND));
            fluidController.deserializeNBT(registries, tag.getCompound(KEY_FLUID_IO));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);

        if (fluidHandler != null && fluidController != null)
        {
            tag.put(KEY_FLUID_TANKS, fluidHandler.serializeNBT(registries));
            tag.put(KEY_FLUID_IO, fluidController.serializeNBT(registries));
        }
    }
}