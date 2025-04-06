package liedge.limatech.blockentity;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.IOAccessSets;
import liedge.limacore.capability.energy.LimaBlockEntityEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import liedge.limacore.capability.itemhandler.LimaItemHandlerUtil;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.blockentity.base.SidedAccessBlockEntityType;
import liedge.limatech.blockentity.base.SidedAccessRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import static liedge.limatech.block.LimaTechBlockProperties.MACHINE_WORKING;

public abstract class SimpleRecipeMachineBlockEntity<I extends RecipeInput, R extends Recipe<I>> extends SidedItemEnergyMachineBlockEntity implements TimedProcessMachineBlockEntity
{
    public static final SidedAccessRules ITEM_ACCESS_RULES = SidedAccessRules.allSides(IOAccessSets.ALL_ALLOWED, IOAccess.DISABLED, false, true);
    public static final SidedAccessRules ENERGY_ACCESS_RULES = SidedAccessRules.allSides(IOAccessSets.INPUT_ONLY_OR_DISABLED, IOAccess.INPUT_ONLY, false, false);

    private final LimaBlockEntityEnergyStorage machineEnergy;
    private final int baseEnergyCapacity;
    private final int baseEnergyTransferRate;
    private final Map<Direction, BlockCapabilityCache<IItemHandler, Direction>> itemConnections = new EnumMap<>(Direction.class);

    private int energyUsage;
    private int machineSpeed = getBaseTicksPerOperation();
    private int craftingProgress;
    private boolean shouldCheckRecipe;
    private boolean crafting;

    private int autoOutputTimer = 0;
    private @Nullable RecipeHolder<R> lastUsedRecipe;

    protected SimpleRecipeMachineBlockEntity(SidedAccessBlockEntityType<?> type, BlockPos pos, BlockState state, int baseEnergyCapacity, int inventorySize)
    {
        super(type, pos, state, inventorySize);
        this.baseEnergyCapacity = baseEnergyCapacity;
        this.baseEnergyTransferRate = baseEnergyCapacity / 20;
        this.machineEnergy = new LimaBlockEntityEnergyStorage(this);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return baseEnergyCapacity;
    }

    @Override
    public int getBaseEnergyTransferRate()
    {
        return baseEnergyTransferRate;
    }

    @Override
    public LimaBlockEntityEnergyStorage getEnergyStorage()
    {
        return machineEnergy;
    }

    @Override
    public int getEnergyUsage()
    {
        return energyUsage;
    }

    @Override
    public void setEnergyUsage(int energyUsage)
    {
        this.energyUsage = energyUsage;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector) {}

    @Override
    public int getCurrentProcessTime()
    {
        return craftingProgress;
    }

    @Override
    public void setCurrentProcessTime(int currentProcessTime)
    {
        this.craftingProgress = currentProcessTime;
    }

    @Override
    public boolean isItemValid(int handlerIndex, int slot, ItemStack stack)
    {
        if (handlerIndex == 0)
        {
            return slot != 0 || LimaItemUtil.hasEnergyCapability(stack);
        }

        return super.isItemValid(handlerIndex, slot, stack);
    }

    @Override
    public int getTicksPerOperation()
    {
        return machineSpeed;
    }

    @Override
    public void setTicksPerOperation(int ticksPerOperation)
    {
        this.machineSpeed = ticksPerOperation;
    }

    public abstract RecipeType<R> machineRecipeType();

    protected abstract I getRecipeInput(Level level);

    protected abstract boolean isInputSlot(int slot);

    protected abstract int outputSlotIndex();

    protected abstract void consumeIngredients(I recipeInput, R recipe, Level level);

    private boolean checkRecipe(Level level, I recipeInput)
    {
        shouldCheckRecipe = false;
        Optional<RecipeHolder<R>> holder = level.getRecipeManager().getRecipeFor(machineRecipeType(), recipeInput, level, lastUsedRecipe);

        if (holder.isPresent())
        {
            lastUsedRecipe = holder.get();
            return true;
        }

        return false;
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
                level.setBlockAndUpdate(getBlockPos(), state);
            }
        }
    }

    private boolean canInsertResultItem(Level level, R recipe)
    {
        return LimaItemUtil.canMergeItemStacks(getItemHandler().getStackInSlot(outputSlotIndex()), recipe.getResultItem(level.registryAccess()));
    }

    @Override
    public IOAccess getItemSlotIO(int handlerIndex, int slot)
    {
        if (handlerIndex == 0)
        {
            if (slot == outputSlotIndex())
            {
                return IOAccess.OUTPUT_ONLY;
            }
            else if (isInputSlot(slot))
            {
                return IOAccess.INPUT_ONLY;
            }
            else
            {
                return IOAccess.DISABLED;
            }
        }

        return IOAccess.DISABLED;
    }

    @Override
    public void onItemSlotChanged(int handlerIndex, int slot)
    {
        super.onItemSlotChanged(handlerIndex, slot);
        if (handlerIndex == 0 && slot != 0) shouldCheckRecipe = true;
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        LimaEnergyStorage energyStorage = getEnergyStorage();
        LimaItemHandlerBase inventory = getItemHandler();

        // Fill internal energy buffer from energy item slot
        if (energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored())
        {
            IEnergyStorage itemEnergy = inventory.getStackInSlot(0).getCapability(Capabilities.EnergyStorage.ITEM);
            if (itemEnergy != null)
            {
                LimaEnergyUtil.transferEnergyBetween(itemEnergy, energyStorage, energyStorage.getTransferRate(), false);
            }
        }

        // Perform recipe check if required - set crafting state accordingly
        I recipeInput = getRecipeInput(level);
        if (shouldCheckRecipe)
        {
            updateCraftingState(level, checkRecipe(level, recipeInput) && lastUsedRecipe != null && canInsertResultItem(level, lastUsedRecipe.value()));
        }

        // Tick recipe progress
        if (crafting && lastUsedRecipe != null && canInsertResultItem(level, lastUsedRecipe.value()))
        {
            if ((getTicksPerOperation() == 0 && LimaEnergyUtil.consumeEnergy(energyStorage, getEnergyUsage(), false)) || craftingProgress >= getTicksPerOperation()) // In zero-tick operations call energy consumption
            {
                ItemStack craftedItem = lastUsedRecipe.value().assemble(recipeInput, level.registryAccess());
                getItemHandler().insertItem(outputSlotIndex(), craftedItem, false);
                consumeIngredients(recipeInput, lastUsedRecipe.value(), level);
                craftingProgress = 0;
                shouldCheckRecipe = true; // Check state of recipe after every successful craft.
            }
            else if (LimaEnergyUtil.consumeEnergy(energyStorage, getEnergyUsage(), false))
            {
                craftingProgress++; // setChanged already called by energy storage extraction
            }
        }
        else
        {
            craftingProgress = 0;
        }

        // Auto output item if option available
        if (getItemControl().isAutoOutput())
        {
            if (autoOutputTimer >= 20)
            {
                for (Direction side : Direction.values())
                {
                    if (getItemControl().getSideIOState(side).allowsOutput())
                    {
                        IItemHandler adjacentInventory = itemConnections.get(side).getCapability();
                        if (adjacentInventory != null) LimaItemHandlerUtil.transferStackBetweenInventories(getItemHandler(), adjacentInventory, outputSlotIndex());
                    }
                }

                autoOutputTimer = 0;
            }
            else
            {
                autoOutputTimer++;
            }
        }
    }

    @Override
    protected void onLoadServer(ServerLevel level)
    {
        super.onLoadServer(level);

        this.shouldCheckRecipe = true;
        for (Direction side : Direction.values())
        {
            itemConnections.put(side, createCapabilityCache(Capabilities.ItemHandler.BLOCK, level, side));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        craftingProgress = tag.getInt("progress");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.putInt("progress", craftingProgress);
    }
}