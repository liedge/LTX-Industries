package liedge.ltxindustries.blockentity;

import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.capability.itemhandler.BlockInventoryType;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.recipe.LimaRecipeCheck;
import liedge.ltxindustries.blockentity.base.EnergyConsumerBlockEntity;
import liedge.ltxindustries.blockentity.base.RecipeMachineBlockEntity;
import liedge.ltxindustries.blockentity.base.SidedAccessBlockEntityType;
import liedge.ltxindustries.blockentity.base.TimedProcessMachineBlockEntity;
import liedge.ltxindustries.blockentity.template.ProductionMachineBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
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

import static liedge.ltxindustries.block.LTXIBlockProperties.MACHINE_WORKING;

public abstract class StateBlockRecipeMachineBlockEntity<I extends RecipeInput, R extends Recipe<I>> extends ProductionMachineBlockEntity implements TimedProcessMachineBlockEntity, EnergyConsumerBlockEntity, RecipeMachineBlockEntity<I, R>
{
    private final LimaRecipeCheck<I, R> recipeCheck;

    private int energyUsage = getBaseEnergyUsage();
    private int machineSpeed = getBaseTicksPerOperation();
    private int craftingProgress;
    private boolean shouldCheckRecipe;
    private boolean crafting;

    protected StateBlockRecipeMachineBlockEntity(SidedAccessBlockEntityType<?> type, RecipeType<R> recipeType, BlockPos pos, BlockState state, int inputSlots, int outputSlots)
    {
        super(type, pos, state, 2, inputSlots, outputSlots);
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
        return machineSpeed;
    }

    @Override
    public void setTicksPerOperation(int ticksPerOperation)
    {
        this.machineSpeed = ticksPerOperation;
    }

    @Override
    public void appendStatsTooltips(TooltipLineConsumer consumer)
    {
        consumer.accept(LTXILangKeys.MACHINE_TICKS_PER_OP_TOOLTIP.translateArgs(getTicksPerOperation()));
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

            BlockState state = getBlockState();
            if (state.hasProperty(MACHINE_WORKING))
            {
                state = state.setValue(MACHINE_WORKING, crafting);
                nonNullLevel().setBlockAndUpdate(getBlockPos(), state);
            }
        }
    }

    protected abstract I getRecipeInput(Level level);

    protected abstract void consumeIngredients(I recipeInput, R recipe, Level level);

    protected abstract void insertRecipeResults(Level level, R recipe, I recipeInput);

    private Optional<RecipeHolder<R>> checkRecipe(Level level, I recipeInput)
    {
        shouldCheckRecipe = false;
        return recipeCheck.getRecipeFor(recipeInput, level);
    }

    @Override
    public void onItemSlotChanged(BlockInventoryType inventoryType, int slot)
    {
        super.onItemSlotChanged(inventoryType, slot);
        if (inventoryType == BlockInventoryType.INPUT || inventoryType == BlockInventoryType.OUTPUT) shouldCheckRecipe = true;
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        LimaEnergyStorage energyStorage = getEnergyStorage();

        // Fill internal energy buffer from energy item slot
        fillEnergyBuffer();

        // Perform recipe check if required - set crafting state accordingly
        I recipeInput = getRecipeInput(level);
        if (shouldCheckRecipe)
        {
            boolean check = checkRecipe(level, recipeInput).map(r -> canInsertRecipeResults(level, r)).orElse(false);
            setCrafting(check);
        }

        // Tick recipe progress
        RecipeHolder<R> lastUsedRecipe = recipeCheck.getLastUsedRecipe(level).orElse(null);
        if (crafting && lastUsedRecipe != null && canInsertRecipeResults(level, lastUsedRecipe))
        {
            if (LimaEnergyUtil.consumeEnergy(energyStorage, getEnergyUsage(), false))
            {
                craftingProgress++; // Set changed already called by energy storage extraction (which must pass to get here)

                if (craftingProgress >= getTicksPerOperation()) // Fixed the N+1 tick duration, we now have true N tick duration
                {
                    insertRecipeResults(level, lastUsedRecipe.value(), recipeInput);
                    consumeIngredients(recipeInput, lastUsedRecipe.value(), level);

                    // Check state of recipe after every successful craft. Last recipe is used first so should be *relatively* quick.
                    craftingProgress = 0;
                    shouldCheckRecipe = true;
                }
            }
        }
        else
        {
            craftingProgress = 0;
        }

        // Push auto outputs via sides every 20 ticks, if option enabled
        autoOutputItems(20, getOutputInventory());
    }

    @Override
    public void onUpgradeRefresh(LootContext context, MachineUpgrades upgrades)
    {
        super.onUpgradeRefresh(context, upgrades);
        TimedProcessMachineBlockEntity.applyUpgrades(this, context, upgrades);
        EnergyConsumerBlockEntity.applyUpgrades(this, context, upgrades);
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