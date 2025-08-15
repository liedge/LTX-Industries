package liedge.ltxindustries.blockentity;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.capability.fluid.FluidHolderBlockEntity;
import liedge.limacore.capability.fluid.LimaBlockEntityFluidHandler;
import liedge.limacore.capability.fluid.LimaFluidUtil;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.recipe.LimaRecipeCheck;
import liedge.ltxindustries.blockentity.base.*;
import liedge.ltxindustries.blockentity.template.ProductionMachineBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import static liedge.ltxindustries.block.LTXIBlockProperties.MACHINE_WORKING;

public abstract class StateBlockRecipeMachineBlockEntity<I extends RecipeInput, R extends Recipe<I>> extends ProductionMachineBlockEntity
        implements FluidHolderBlockEntity, TimedProcessMachineBlockEntity, EnergyConsumerBlockEntity, RecipeMachineBlockEntity<I, R>
{
    public static final int INPUT_TANK_CAPACITY = 32_000;
    public static final int OUTPUT_TANK_CAPACITY = 64_000;

    private final @Nullable LimaBlockEntityFluidHandler inputFluids;
    private final @Nullable LimaBlockEntityFluidHandler outputFluids;
    private final @Nullable IOController fluidController;
    private final Map<Direction, BlockCapabilityCache<IFluidHandler, Direction>> fluidConnections = new EnumMap<>(Direction.class);
    private final LimaRecipeCheck<I, R> recipeCheck;

    private int energyUsage = getBaseEnergyUsage();
    private int machineSpeed = getBaseTicksPerOperation();
    private int craftingProgress;
    private boolean shouldCheckRecipe;
    private boolean shouldCheckOutput;
    private boolean crafting;
    private int autoFluidOutputTimer = 0;

    protected StateBlockRecipeMachineBlockEntity(SidedAccessBlockEntityType<?> type, RecipeType<R> recipeType, BlockPos pos, BlockState state, int inputSlots, int outputSlots, int inputTanks, int outputTanks)
    {
        super(type, pos, state, 2, inputSlots, outputSlots);
        this.recipeCheck = LimaRecipeCheck.create(recipeType);
        this.inputFluids = inputTanks > 0 ? new LimaBlockEntityFluidHandler(this, inputTanks, BlockContentsType.INPUT) : null;
        this.outputFluids = outputTanks > 0 ? new LimaBlockEntityFluidHandler(this, outputTanks, BlockContentsType.OUTPUT) : null;
        this.fluidController = inputFluids != null || outputFluids != null ? new IOController(this, BlockEntityInputType.FLUIDS) : null;
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
        if (contentsType == BlockContentsType.INPUT)
        {
            shouldCheckRecipe = true;
        }
        else if (contentsType == BlockContentsType.OUTPUT && !crafting)
        {
            shouldCheckRecipe = true;
            shouldCheckOutput = true;
        }
    }

    // Fluid handler implementation
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
    public int getBaseFluidCapacity(BlockContentsType contentsType, int tank)
    {
        return contentsType == BlockContentsType.INPUT ? INPUT_TANK_CAPACITY : OUTPUT_TANK_CAPACITY;
    }

    @Override
    public int getBaseFluidTransferRate(BlockContentsType contentsType, int tank)
    {
        return getBaseFluidCapacity(contentsType, tank) / 4;
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
    protected IOController getFluidIOController()
    {
        return fluidController != null ? fluidController : super.getFluidIOController();
    }

    @Override
    public void onFluidsChanged(BlockContentsType contentsType, int tank)
    {
        setChanged();
        if (contentsType == BlockContentsType.INPUT)
        {
            shouldCheckRecipe = true;
        }
        else if (contentsType == BlockContentsType.OUTPUT && !crafting)
        {
            shouldCheckRecipe = true;
            shouldCheckOutput = true;
        }
    }

    @Override
    public @Nullable IFluidHandler getNeighborFluidHandler(Direction side)
    {
        return fluidConnections.get(side).getCapability();
    }

    private void autoOutputFluids()
    {
        if (outputFluids != null && fluidController != null && fluidController.isAutoOutput())
        {
            for (Direction side : Direction.values())
            {
                if (fluidController.getSideIOState(side).allowsOutput())
                {
                    IFluidHandler neighborFluids = getNeighborFluidHandler(side);
                    if (neighborFluids != null) LimaFluidUtil.transferFluidsBetween(outputFluids, neighborFluids, OUTPUT_TANK_CAPACITY, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        }
    }

    private void autoOutputFluids(int frequency)
    {
        if (autoFluidOutputTimer >= frequency)
        {
            autoOutputFluids();
            autoFluidOutputTimer = 0;
        }
        else
        {
            autoFluidOutputTimer++;
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
            Optional<RecipeHolder<R>> lookup = recipeCheck.getRecipeFor(getRecipeInput(level), level);
            boolean recipeCheck = lookup.isPresent() && (!shouldCheckOutput || canInsertRecipeResults(level, lookup.get().value()));

            if (!recipeCheck) craftingProgress = 0;

            shouldCheckRecipe = false;
            shouldCheckOutput = false;

            setCrafting(recipeCheck && energyStorage.getEnergyStored() >= getEnergyUsage());
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
                    shouldCheckOutput = true;
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
        TimedProcessMachineBlockEntity.applyUpgrades(this, context, upgrades);
        EnergyConsumerBlockEntity.applyUpgrades(this, context, upgrades);
    }

    @Override
    protected void createConnectionCaches(ServerLevel level, Direction side)
    {
        super.createConnectionCaches(level, side);
        fluidConnections.put(side, createCapabilityCache(Capabilities.FluidHandler.BLOCK, level, side));
    }

    @Override
    protected void onLoadServer(ServerLevel level)
    {
        super.onLoadServer(level);
        this.shouldCheckRecipe = true;
        this.shouldCheckOutput = true;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        craftingProgress = tag.getInt(TAG_KEY_PROGRESS);

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
        tag.putInt(TAG_KEY_PROGRESS, craftingProgress);

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