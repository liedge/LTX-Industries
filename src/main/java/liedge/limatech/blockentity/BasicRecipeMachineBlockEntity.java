package liedge.limatech.blockentity;

import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.lib.IODirection;
import liedge.limacore.lib.energy.LimaEnergyStorage;
import liedge.limacore.lib.energy.LimaEnergyUtil;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.recipe.BasicMachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.Optional;

import static liedge.limatech.block.BasicMachineBlock.MACHINE_WORKING;

public abstract class BasicRecipeMachineBlockEntity<I extends RecipeInput, R extends Recipe<I>> extends MachineBlockEntity implements ProgressMachine
{
    private int progress;
    private boolean performLookup;
    private boolean crafting;
    private @Nullable RecipeHolder<R> lastUsedRecipe;

    protected BasicRecipeMachineBlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state, int energyCapacity, int energyTransferRate, int inventorySize)
    {
        super(type, pos, state, energyCapacity, energyTransferRate, inventorySize);
    }

    @Override
    public int getMachineProgress()
    {
        return progress;
    }

    @Override
    public void setMachineProgress(int machineProgress)
    {
        this.progress = machineProgress;
    }

    public abstract RecipeType<R> machineRecipeType();

    public abstract int machineEnergyUse();

    protected abstract boolean isInputSlot(int slot);

    protected abstract int outputSlotIndex();

    protected abstract I getOrCreateRecipeInput(Level level);

    protected abstract ItemStack craftItem(RecipeHolder<R> recipeHolder, RegistryAccess registryAccess);

    private boolean recipeStillValid(Level level)
    {
        I input = getOrCreateRecipeInput(level);

        if (lastUsedRecipe != null && lastUsedRecipe.value().matches(input, level))
        {
            return true;
        }
        else if (performLookup)
        {
            performLookup = false;
            LimaTech.LOGGER.debug("Issuing new call to recipe manager");
            Optional<RecipeHolder<R>> optional = level.getRecipeManager().getRecipeFor(machineRecipeType(), input, level);
            if (optional.isPresent())
            {
                lastUsedRecipe = optional.get();
                return true;
            }
        }

        return false;
    }

    private boolean canInsertResultItem(Level level, RecipeHolder<R> lastUsedRecipe)
    {
        return LimaItemUtil.canCombineStacks(getItemHandler().getStackInSlot(outputSlotIndex()), lastUsedRecipe.value().getResultItem(level.registryAccess()));
    }

    private void updateCraftingState(Level level, boolean crafting)
    {
        if (this.crafting != crafting)
        {
            this.crafting = crafting;
            setChanged();

            BlockState state = getBlockState();
            if (state.hasProperty(MACHINE_WORKING))
            {
                state = state.setValue(MACHINE_WORKING, crafting);
                level.setBlock(worldPosition, state, Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public IODirection getIOForSlot(int slot)
    {
        if (slot == outputSlotIndex())
        {
            return IODirection.OUTPUT_ONLY;
        }
        else if (isInputSlot(slot))
        {
            return IODirection.INPUT_ONLY;
        }
        else
        {
            return IODirection.NONE;
        }
    }

    @Override
    public void onItemSlotChanged(int slot)
    {
        super.onItemSlotChanged(slot);
        if (isInputSlot(slot)) performLookup = true;
    }

    @Override
    protected void tickServer(Level level, BlockPos pos, BlockState state)
    {
        // Fill internal energy buffer from energy item slot
        LimaEnergyStorage machineEnergy = getEnergyStorage();
        if (machineEnergy.getEnergyStored() < machineEnergy.getMaxEnergyStored())
        {
            IEnergyStorage itemEnergy = getItemHandler().getStackInSlot(0).getCapability(Capabilities.EnergyStorage.ITEM);
            if (itemEnergy != null)
            {
                LimaEnergyUtil.transferEnergyBetween(itemEnergy, machineEnergy, machineEnergy.getTransferRate(), false);
            }
        }

        // Tick active recipe (if any)
        if (recipeStillValid(level) && lastUsedRecipe != null && canInsertResultItem(level, lastUsedRecipe))
        {
            // Separate energy check - will not lose progress if out of energy
            if (machineEnergy.getEnergyStored() >= machineEnergyUse())
            {
                machineEnergy.extractEnergy(machineEnergyUse(), false);
                progress++;
                updateCraftingState(level, true);

                if (progress >= getMachineProcessTime())
                {
                    getItemHandler().insertItem(outputSlotIndex(), craftItem(lastUsedRecipe, level.registryAccess()), false);
                    progress = 0;
                }
            }
        }
        else
        {
            progress = 0;
            updateCraftingState(level, false);
        }
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        performLookup = true;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        progress = tag.getInt("progress");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.putInt("progress", progress);
    }

    public static abstract class LimaRecipeMachine<R extends BasicMachineRecipe> extends BasicRecipeMachineBlockEntity<BasicMachineRecipe.LimaBasicRecipeInput, R> implements BasicMachineRecipe.LimaBasicRecipeInput
    {
        protected LimaRecipeMachine(LimaBlockEntityType<?> type, BlockPos pos, BlockState state, int energyCapacity, int energyTransferRate, int inventorySize)
        {
            super(type, pos, state, energyCapacity, energyTransferRate, inventorySize);
        }

        @Override
        protected final BasicMachineRecipe.LimaBasicRecipeInput getOrCreateRecipeInput(Level level)
        {
            return this;
        }

        @Override
        protected final ItemStack craftItem(RecipeHolder<R> recipeHolder, RegistryAccess registryAccess)
        {
            return recipeHolder.value().assemble(this, registryAccess);
        }
    }
}