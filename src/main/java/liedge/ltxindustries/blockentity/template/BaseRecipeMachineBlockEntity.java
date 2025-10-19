package liedge.ltxindustries.blockentity.template;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.recipe.LimaRecipeCheck;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import liedge.ltxindustries.blockentity.base.EnergyConsumerBlockEntity;
import liedge.ltxindustries.blockentity.base.RecipeMachineBlockEntity;
import liedge.ltxindustries.blockentity.base.VariableTimedProcessBlockEntity;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.Optional;
import java.util.function.IntUnaryOperator;

public abstract class BaseRecipeMachineBlockEntity<I extends RecipeInput, R extends Recipe<I>> extends ProductionMachineBlockEntity
        implements VariableTimedProcessBlockEntity, EnergyConsumerBlockEntity, RecipeMachineBlockEntity<I, R>
{
    private final LimaRecipeCheck<I, R> recipeCheck;
    private int energyUsage = getBaseEnergyUsage();
    private IntUnaryOperator recipeTimeFunction = IntUnaryOperator.identity();
    private int recipeCraftingTime;
    private int craftingProgress;
    private boolean crafting;
    private boolean shouldCheckRecipe;
    private boolean shouldCheckCraftingTime;

    protected BaseRecipeMachineBlockEntity(ConfigurableIOBlockEntityType<?> type, RecipeType<R> recipeType, BlockPos pos, BlockState state, int inputSlots, int outputSlots, int inputTanks, int outputTanks)
    {
        super(type, pos, state, 2, inputSlots, outputSlots, inputTanks, outputTanks);
        this.recipeCheck = LimaRecipeCheck.create(recipeType);
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
    public int getTicksPerOperation()
    {
        return recipeCraftingTime;
    }

    @Override
    public void setTicksPerOperation(int ticksPerOperation)
    {
        this.recipeCraftingTime = ticksPerOperation;
    }

    @Override
    public void appendStatsTooltips(TooltipLineConsumer consumer)
    {
        LTXITooltipUtil.appendEnergyUsagePerTickTooltip(consumer, getEnergyUsage());
    }

    @Override
    public LimaRecipeCheck<I, R> getRecipeCheck()
    {
        return recipeCheck;
    }

    @Override
    public boolean isCrafting()
    {
        return crafting;
    }

    @Override
    public void setCrafting(boolean crafting)
    {
        if (this.crafting != crafting)
        {
            this.crafting = crafting;
            setChanged();

            onCraftingStateChanged(crafting);
        }
    }

    /*
    protected void onCraftingStateChanged(boolean newCraftingState)
    {
        BlockState newState = getBlockState().setValue(LTXIBlockProperties.BINARY_MACHINE_STATE, newCraftingState ? MachineState.ACTIVE : MachineState.IDLE);
        nonNullLevel().setBlockAndUpdate(getBlockPos(), newState);
    }
    */
    protected abstract void onCraftingStateChanged(boolean newCraftingState);

    protected abstract I getRecipeInput(Level level);

    protected abstract int getBaseRecipeCraftingTime(R recipe);

    protected abstract void consumeIngredients(I recipeInput, R recipe, Level level);

    protected abstract void insertRecipeResults(Level level, R recipe, I recipeInput);

    @Override
    public void onEnergyChanged(int previousEnergy)
    {
        setChanged();
        if (previousEnergy < getEnergyUsage() && getEnergyStorage().getEnergyStored() >= getEnergyUsage())
            shouldCheckRecipe = true;
    }

    @Override
    public void onItemSlotChanged(BlockContentsType contentsType, int slot)
    {
        setChanged();
        if (contentsType == BlockContentsType.INPUT || (contentsType == BlockContentsType.OUTPUT && !crafting))
        {
            shouldCheckRecipe = true;
        }
    }

    // Fluid handler implementation
    @Override
    public void onFluidsChanged(BlockContentsType contentsType, int tank)
    {
        super.onFluidsChanged(contentsType, tank);
        if (contentsType == BlockContentsType.INPUT || (contentsType == BlockContentsType.OUTPUT && !crafting))
        {
            shouldCheckRecipe = true;
        }
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        LimaEnergyStorage energyStorage = getEnergyStorage();

        // Fill internal energy buffer from energy item slot
        fillEnergyBuffer();

        // Perform recipe check if required - set crafting state accordingly
        if (shouldCheckRecipe)
        {
            Optional<RecipeHolder<R>> lastUsed = recipeCheck.getLastUsedRecipe(level);
            Optional<RecipeHolder<R>> lookup = recipeCheck.getRecipeFor(getRecipeInput(level), level);

            boolean hasValidRecipe = false;

            if (lookup.isPresent())
            {
                RecipeHolder<R> recipeHolder = lookup.get();
                boolean recipeChanged = lastUsed.filter(recipeHolder::equals).isEmpty();

                if (recipeChanged) this.shouldCheckCraftingTime = true;

                hasValidRecipe = canInsertRecipeResults(level, recipeHolder);
                if (hasValidRecipe && shouldCheckCraftingTime)
                {
                    int baseTime = getBaseRecipeCraftingTime(recipeHolder.value());
                    int time = recipeTimeFunction.applyAsInt(baseTime);
                    setTicksPerOperation(time);
                    shouldCheckCraftingTime = false;
                }
            }

            if (!hasValidRecipe) craftingProgress = 0;

            shouldCheckRecipe = false;

            setCrafting(hasValidRecipe && energyStorage.getEnergyStored() >= getEnergyUsage());
        }

        // Tick recipe progress
        RecipeHolder<R> lastUsedRecipe = recipeCheck.getLastUsedRecipe(level).orElse(null);
        if (crafting && lastUsedRecipe != null)
        {
            if (LimaEnergyUtil.consumeEnergy(energyStorage, getEnergyUsage(), false))
            {
                craftingProgress++; // Set changed already called by energy storage extraction (which must pass to get here)

                if (craftingProgress >= getTicksPerOperation()) // Fixed the N+1 tick duration, we now have true N tick duration
                {
                    I recipeInput = getRecipeInput(level);
                    insertRecipeResults(level, lastUsedRecipe.value(), recipeInput);
                    consumeIngredients(recipeInput, lastUsedRecipe.value(), level);

                    // Check state of recipe after every successful craft. Last recipe is used first so should be *relatively* quick.
                    craftingProgress = 0;
                    shouldCheckRecipe = true;
                }
            }
            else
            {
                setCrafting(false);
            }
        }

        // Push auto outputs via sides every 20 ticks, if option enabled
        autoOutputItems(20, getOutputInventory());
        autoOutputFluids(20);
    }

    @Override
    public void onUpgradeRefresh(LootContext context, MachineUpgrades upgrades)
    {
        super.onUpgradeRefresh(context, upgrades);
        EnergyConsumerBlockEntity.applyUpgrades(this, context, upgrades);
        this.recipeTimeFunction = createCachedSpeedFunction(upgrades, context);
        this.shouldCheckCraftingTime = true;
        this.shouldCheckRecipe = true;
    }

    @Override
    protected void onLoadServer(ServerLevel level)
    {
        super.onLoadServer(level);
        this.shouldCheckRecipe = true;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        craftingProgress = tag.getInt(TAG_KEY_PROGRESS);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.putInt(TAG_KEY_PROGRESS, craftingProgress);
    }
}