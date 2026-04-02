package liedge.ltxindustries.blockentity.template;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.recipe.LimaRecipeCheck;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import liedge.ltxindustries.blockentity.base.EnergyConsumerBlockEntity;
import liedge.ltxindustries.blockentity.base.RecipeMachineBlockEntity;
import liedge.ltxindustries.blockentity.base.VariableTimedProcessBlockEntity;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;
import java.util.function.IntUnaryOperator;

public abstract class BaseRecipeMachineBlockEntity<I extends RecipeInput, R extends Recipe<I>> extends ProductionMachineBlockEntity
        implements VariableTimedProcessBlockEntity, EnergyConsumerBlockEntity, RecipeMachineBlockEntity<I, R>
{
    private final LimaRecipeCheck<I, R> recipeCheck;
    private int energyUsage = getBaseEnergyUsage();
    private IntUnaryOperator recipeTimeFunction = IntUnaryOperator.identity();
    private int recipeCraftingTime;
    private int operationCount = 1;
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
        if (isCrafting() != crafting)
        {
            this.crafting = crafting;
            setChanged();
            onCraftingStateChanged(crafting);
        }
    }

    protected abstract void onCraftingStateChanged(boolean newCraftingState);

    protected abstract I getRecipeInput(Level level);

    protected abstract int getBaseRecipeCraftingTime(R recipe);

    protected abstract void consumeIngredients(I inputAccess, R recipe, Level level);

    protected abstract void insertRecipeResults(Level level, R recipe, I recipeInput);

    protected abstract void craftRecipe(ServerLevel level, R recipe, int maxOperations);

    protected void reCheckRecipe()
    {
        this.shouldCheckRecipe = true;
    }

    @Override
    public void onEnergyChanged(int previousEnergy)
    {
        setChanged();
        if (previousEnergy < getEnergyUsage() && hasMinimumEnergy()) reCheckRecipe();
    }

    @Override
    public void onItemChanged(BlockContentsType contentsType, int index, ItemStack previousContents)
    {
        setChanged();

        if (contentsType == BlockContentsType.INPUT || (contentsType == BlockContentsType.OUTPUT && !crafting))
        {
            reCheckRecipe();
        }
    }

    @Override
    public void onFluidChanged(BlockContentsType contentsType, int index, FluidStack previousContents)
    {
        setChanged();

        if (contentsType == BlockContentsType.INPUT || (contentsType == BlockContentsType.OUTPUT && !crafting))
        {
            reCheckRecipe();
        }
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        // Fill internal energy buffer from energy item slot
        pullEnergyFromAux();

        // Perform recipe check if required - set crafting state accordingly
        if (shouldCheckRecipe)
        {
            Optional<RecipeHolder<R>> lastUsed = recipeCheck.getLastUsedRecipe(level);
            I recipeInput = getRecipeInput(level);
            Optional<RecipeHolder<R>> lookup = recipeCheck.getRecipeFor(recipeInput, level);

            boolean hasValidRecipe = false;

            if (lookup.isPresent())
            {
                RecipeHolder<R> recipeHolder = lookup.get();
                boolean recipeChanged = lastUsed.filter(recipeHolder::equals).isEmpty();

                if (recipeChanged) this.shouldCheckCraftingTime = true;

                hasValidRecipe = canInsertRecipeResults(level, recipeHolder.value(), recipeInput);
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

            setCrafting(hasValidRecipe && hasMinimumEnergy());
        }

        // Tick recipe progress
        RecipeHolder<R> lastUsedRecipe = recipeCheck.getLastUsedRecipe(level).orElse(null);
        if (crafting && lastUsedRecipe != null)
        {
            if (consumeUsageEnergy())
            {
                craftingProgress++; // Set changed already called by energy storage extraction (which must pass to get here)

                if (craftingProgress >= getTicksPerOperation()) // Fixed the N+1 tick duration, we now have true N tick duration
                {
                    // Check state of recipe after every successful craft. Last recipe is used first so should be *relatively* quick.
                    craftRecipe(level, lastUsedRecipe.value(), this.operationCount);
                    craftingProgress = 0;
                    reCheckRecipe();
                }
            }
            else
            {
                setCrafting(false);
            }
        }

        // Tick auto input/output of items and fluids
        tickItemAutoInput(20, getItems(BlockContentsType.INPUT));
        tickItemAutoOutput(20, getItems(BlockContentsType.OUTPUT));
        tickFluidAutoInput(20);
        tickFluidAutoOutput(20);
    }

    @Override
    public void onUpgradeRefresh(LootContext context, Upgrades upgrades)
    {
        super.onUpgradeRefresh(context, upgrades);
        EnergyConsumerBlockEntity.applyUpgrades(this, context, upgrades);
        this.recipeTimeFunction = createCachedSpeedFunction(upgrades, context);
        int parallel = Mth.floor(upgrades.runValueOps(LTXIUpgradeEffectComponents.PARALLEL_OPERATIONS, context, 1));
        this.operationCount = Mth.clamp(parallel, 1, 64);

        this.shouldCheckCraftingTime = true;
        reCheckRecipe();
    }

    @Override
    protected void onLoadServer(ServerLevel level)
    {
        super.onLoadServer(level);
        reCheckRecipe();
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        craftingProgress = input.getIntOr(TAG_KEY_PROGRESS, 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        output.putInt(TAG_KEY_PROGRESS, craftingProgress);
    }
}