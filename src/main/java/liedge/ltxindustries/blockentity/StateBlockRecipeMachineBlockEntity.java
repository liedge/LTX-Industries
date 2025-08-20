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
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.base.*;
import liedge.ltxindustries.blockentity.template.ProductionMachineBlockEntity;
import liedge.ltxindustries.lib.upgrades.EffectRankPair;
import liedge.ltxindustries.lib.upgrades.effect.value.ValueUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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

import java.util.*;
import java.util.function.DoubleUnaryOperator;

import static liedge.ltxindustries.block.LTXIBlockProperties.MACHINE_WORKING;

public abstract class StateBlockRecipeMachineBlockEntity<I extends RecipeInput, R extends Recipe<I>> extends ProductionMachineBlockEntity
        implements FluidHolderBlockEntity, VariableTimedProcessBlockEntity, EnergyConsumerBlockEntity, RecipeMachineBlockEntity<I, R>
{
    public static final int INPUT_TANK_CAPACITY = 32_000;
    public static final int OUTPUT_TANK_CAPACITY = 64_000;

    private final @Nullable LimaBlockEntityFluidHandler inputFluids;
    private final @Nullable LimaBlockEntityFluidHandler outputFluids;
    private final @Nullable IOController fluidController;
    private final Map<Direction, BlockCapabilityCache<IFluidHandler, Direction>> fluidConnections = new EnumMap<>(Direction.class);
    private final LimaRecipeCheck<I, R> recipeCheck;

    private int energyUsage = getBaseEnergyUsage();
    private DoubleUnaryOperator recipeTimeFunction = DoubleUnaryOperator.identity();
    private int recipeCraftingTime;
    private int craftingProgress;
    private boolean crafting;
    private int autoFluidOutputTimer = 0;
    private boolean shouldCheckRecipe;
    private boolean shouldCheckCraftingTime;

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

            BlockState state = getBlockState();
            if (state.hasProperty(MACHINE_WORKING))
            {
                state = state.setValue(MACHINE_WORKING, crafting);
                nonNullLevel().setBlockAndUpdate(getBlockPos(), state);
            }
        }
    }

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
        if (contentsType == BlockContentsType.INPUT || (contentsType == BlockContentsType.OUTPUT && !crafting))
        {
            shouldCheckRecipe = true;
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
                    if (neighborFluids != null) LimaFluidUtil.transferFluidsFromLimaTank(outputFluids, neighborFluids, OUTPUT_TANK_CAPACITY, IFluidHandler.FluidAction.EXECUTE);
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
                    int time = Math.max(0, Mth.floor(recipeTimeFunction.applyAsDouble(baseTime)));
                    setTicksPerOperation(time);
                    shouldCheckCraftingTime = false;
                    LTXIndustries.LOGGER.debug("Recomputed recipe process time for type {} to {}", LimaRegistryUtil.getNonNullRegistryId(this.recipeCheck.getRecipeType(), BuiltInRegistries.RECIPE_TYPE), time);
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
        this.recipeTimeFunction = createRecipeTimeFunction(LTXIUpgradeEffectComponents.TICKS_PER_OPERATION.get(), context);
        this.shouldCheckCraftingTime = true;
        this.shouldCheckRecipe = true;
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

    private DoubleUnaryOperator createRecipeTimeFunction(DataComponentType<List<ValueUpgradeEffect>> type, LootContext context)
    {
        List<EffectRankPair<ValueUpgradeEffect>> list = this.getUpgrades().boxedFlatStream(type).sorted(Comparator.comparing(entry -> entry.effect().getOperation())).toList();

        if (list.isEmpty()) return DoubleUnaryOperator.identity();

        return base ->
        {
            double total = base;
            for (EffectRankPair<ValueUpgradeEffect> pair : list)
            {
                total = pair.effect().apply(context, pair.upgradeRank(), base, total);
            }
            return total;
        };
    }
}