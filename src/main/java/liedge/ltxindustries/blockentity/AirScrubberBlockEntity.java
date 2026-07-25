package liedge.ltxindustries.blockentity;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.recipe.LimaRecipeCheck;
import liedge.limacore.recipe.LimaRecipeUtil;
import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.blockentity.base.EnergyConsumerBlockEntity;
import liedge.ltxindustries.blockentity.base.RecipeMachineBlockEntity;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.blockentity.base.TimedProcessBlockEntity;
import liedge.ltxindustries.blockentity.template.ProductionMachineBlockEntity;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.recipe.AirScrubbingInput;
import liedge.ltxindustries.recipe.AirScrubbingRecipe;
import liedge.ltxindustries.recipe.RecipeMode;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import liedge.ltxindustries.util.LTXITooltipUtil;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class AirScrubberBlockEntity extends ProductionMachineBlockEntity implements RecipeMachineBlockEntity<AirScrubbingInput, AirScrubbingRecipe>,
        EnergyConsumerBlockEntity, TimedProcessBlockEntity.FixedBaseDuration, RecipeModeHolderBlockEntity
{
    private final LimaRecipeCheck<AirScrubbingInput, AirScrubbingRecipe> recipeCheck = LimaRecipeCheck.create(LTXIRecipeTypes.AIR_SCRUBBING);
    private boolean checkRecipe;
    private boolean active;
    private int energyUsage;
    private int ticksPerOperation = getBaseTicksPerOperation();
    private int currentProcessTime;
    private @Nullable Holder<RecipeMode> mode;
    private HolderSet<RecipeMode> availableModes = HolderSet.empty();

    // Remote properties
    private int spinSpeed;
    private float impellerRot0;
    private float impellerRot;

    public AirScrubberBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.ATMOSPHERIC_SCRUBBER.get(), pos, state, 2, 0, 2, 0, 4);
    }

    @Override
    public void appendStatsTooltips(TooltipLineConsumer consumer)
    {
        appendOperationTicksTooltip(consumer);
        LTXITooltipUtil.appendEnergyUsagePerTickTooltip(consumer, getEnergyUsage());
    }

    @Override
    public void onEnergyChanged(int previousAmount)
    {
        setChanged();
        if (previousAmount < getEnergyUsage() && hasMinimumEnergy()) checkRecipe = true;
    }

    @Override
    public void onItemChanged(BlockContentsType contentsType, int index, ItemStack previousContents)
    {
        setChanged();
        if (contentsType == BlockContentsType.OUTPUT && !active) checkRecipe = true;
    }

    @Override
    public void onFluidChanged(BlockContentsType contentsType, int index, FluidStack previousContents)
    {
        setChanged();
        if (contentsType == BlockContentsType.OUTPUT && !active) checkRecipe = true;
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        pullEnergyFromAux();

        if (checkRecipe || (!isCrafting() && level.getGameTime() % 200L == 0))
        {
            Optional<RecipeHolder<AirScrubbingRecipe>> lastUsed = recipeCheck.getLastUsedRecipe(level);
            AirScrubbingInput input = new AirScrubbingInput(mode, pos);
            Optional<RecipeHolder<AirScrubbingRecipe>> lookup = recipeCheck.getRecipeFor(input, level);

            boolean hasValidRecipe = false;

            if (lookup.isPresent())
            {
                RecipeHolder<AirScrubbingRecipe> recipeHolder = lookup.get();
                boolean recipeChanged = lastUsed.filter(recipeHolder::equals).isEmpty();

                if (recipeChanged)
                {
                    this.currentProcessTime = 0;
                }

                hasValidRecipe = canInsertRecipeResults(level, recipeHolder.value(), input);
            }

            if (!hasValidRecipe) currentProcessTime = 0;

            checkRecipe = false;

            setCrafting(hasValidRecipe && hasMinimumEnergy());
        }

        RecipeHolder<AirScrubbingRecipe> lastUsedRecipe = recipeCheck.getLastUsedRecipe(level).orElse(null);
        if (isCrafting() && lastUsedRecipe != null)
        {
            if (consumeUsageEnergy())
            {
                currentProcessTime++;

                if (currentProcessTime >= ticksPerOperation)
                {
                    AirScrubbingRecipe recipe = lastUsedRecipe.value();

                    LimaRecipeUtil.insertResultStacks(recipe.getItemResults(), level.getRandom(), getItems(BlockContentsType.OUTPUT), null);
                    LimaRecipeUtil.insertResultStacks(recipe.getFluidResults(), level.getRandom(), getFluids(BlockContentsType.OUTPUT), null);

                    currentProcessTime = 0;
                    checkRecipe = true;
                }
            }
            else
            {
                setCrafting(false);
            }
        }

        tickAutoResourceOutput(20, getItems(BlockContentsType.OUTPUT), getFluids(BlockContentsType.OUTPUT));
    }

    @Override
    protected void tickClient(Level level, BlockPos pos, BlockState state)
    {
        if (LTXIBlockProperties.isMachineActive(state))
        {
            if (spinSpeed < 15) spinSpeed += 3;
        }
        else
        {
            if (spinSpeed > 0) spinSpeed = Math.max(0, spinSpeed - 3);
        }

        impellerRot0 = impellerRot;
        impellerRot = (impellerRot + spinSpeed) % 360;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector) { }

    @Override
    public LimaRecipeCheck<AirScrubbingInput, AirScrubbingRecipe> getRecipeCheck()
    {
        return recipeCheck;
    }

    @Override
    public boolean isCrafting()
    {
        return active;
    }

    @Override
    public void setCrafting(boolean crafting)
    {
        this.active = crafting;
        LTXIBlockProperties.updateBinaryState(nonNullLevel(), getBlockPos(), getBlockState(), crafting);
    }

    @Override
    public boolean canInsertRecipeResults(ServerLevel level, AirScrubbingRecipe recipe, AirScrubbingInput inputAccess)
    {
        return canInsertResourceResults(recipe.getItemResults(), getItems(BlockContentsType.OUTPUT)) &&
                canInsertResourceResults(recipe.getFluidResults(), getFluids(BlockContentsType.OUTPUT));
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
            checkRecipe = true;
        }
    }

    @Override
    public HolderSet<RecipeMode> getAvailableRecipeModes()
    {
        return availableModes;
    }

    @Override
    public void setAvailableRecipeModes(HolderSet<RecipeMode> availableModes)
    {
        this.availableModes = availableModes;
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.AIR_SCRUBBER_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.AIR_SCRUBBER_ENERGY_USAGE.getAsInt();
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
    public int getBaseTicksPerOperation()
    {
        return LTXIMachinesConfig.AIR_SCRUBBER_BASE_SPEED.getAsInt();
    }

    @Override
    public int getCurrentProcessTime()
    {
        return currentProcessTime;
    }

    @Override
    public void setCurrentProcessTime(int currentProcessTime)
    {
        this.currentProcessTime = currentProcessTime;
    }

    @Override
    public int getTicksPerOperation()
    {
        return ticksPerOperation;
    }

    @Override
    public void setTicksPerOperation(int ticksPerOperation)
    {
        this.ticksPerOperation = ticksPerOperation;
    }

    @Override
    protected void onLoadServer(ServerLevel level)
    {
        super.onLoadServer(level);
        this.checkRecipe = true;
    }

    @Override
    public void onUpgradeRefresh(LootContext context, Upgrades upgrades)
    {
        super.onUpgradeRefresh(context, upgrades);

        EnergyConsumerBlockEntity.applyUpgrades(this, context, upgrades);
        FixedBaseDuration.applyUpgrades(this, context, upgrades);
        RecipeModeHolderBlockEntity.applyUpgrades(this, upgrades);
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        currentProcessTime = input.getIntOr(TAG_KEY_PROGRESS, 0);
        this.mode = input.read(TAG_KEY_RECIPE_MODE, RecipeMode.CODEC).orElse(null);
        recipeCheck.deserialize(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        output.putInt(TAG_KEY_PROGRESS, currentProcessTime);
        output.storeNullable(TAG_KEY_RECIPE_MODE, RecipeMode.CODEC, mode);
        recipeCheck.serialize(output);
    }

    public float lerpImpellerRot(float partialTick)
    {
        return Mth.rotLerp(partialTick, impellerRot0, impellerRot);
    }
}