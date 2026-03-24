package liedge.ltxindustries.blockentity;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.recipe.LimaRecipeCheck;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import liedge.ltxindustries.blockentity.base.EnergyConsumerBlockEntity;
import liedge.ltxindustries.blockentity.base.RecipeMachineBlockEntity;
import liedge.ltxindustries.blockentity.template.ProductionMachineBlockEntity;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import liedge.ltxindustries.util.LTXITooltipUtil;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.transfer.item.ItemResource;

public abstract class BaseFabricatorBlockEntity extends ProductionMachineBlockEntity implements EnergyConsumerBlockEntity, RecipeMachineBlockEntity<LimaRecipeInput, FabricatingRecipe>
{
    public static final int AUX_BLUEPRINT_SLOT = 2;

    // Common properties
    private final LimaRecipeCheck<LimaRecipeInput, FabricatingRecipe> recipeCheck = LimaRecipeCheck.create(LTXIRecipeTypes.FABRICATING);
    private int energyUsage = getBaseEnergyUsage();
    private boolean crafting;
    protected int energyCraftProgress;

    // Client properties
    private ItemStack clientPreviewItem = ItemStack.EMPTY;

    protected BaseFabricatorBlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state, int inputSlots)
    {
        super(type, pos, state, 3, inputSlots, 1);
    }

    public ItemStack getClientPreviewItem()
    {
        return clientPreviewItem;
    }

    public int getEnergyCraftProgress()
    {
        return energyCraftProgress;
    }

    public LimaDataWatcher<Integer> keepProgressSynced()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getEnergyCraftProgress, i -> this.energyCraftProgress = i);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.FABRICATOR_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public void appendStatsTooltips(TooltipLineConsumer consumer)
    {
        LTXITooltipUtil.appendEnergyUsagePerTickTooltip(consumer, getEnergyUsage());
    }

    @Override
    public void onUpgradeRefresh(LootContext context, MachineUpgrades upgrades)
    {
        super.onUpgradeRefresh(context, upgrades);
        EnergyConsumerBlockEntity.applyUpgrades(this, context, upgrades);
    }

    @Override
    public LimaRecipeCheck<LimaRecipeInput, FabricatingRecipe> getRecipeCheck()
    {
        return recipeCheck;
    }

    @Override
    public boolean canInsertRecipeResults(ServerLevel level, FabricatingRecipe recipe, LimaRecipeInput input)
    {
        return canInsertResourceResults(recipe.getItemResults(), getItems(BlockContentsType.OUTPUT));
    }

    @Override
    public boolean isCrafting()
    {
        return crafting;
    }

    @Override
    public void setCrafting(boolean crafting)
    {
        this.crafting = crafting;
        setChanged();
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.BOOL, this::isCrafting, this::setCrafting));
        collector.register(AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.ITEM_RESOURCE, this::writePreviewResource, this::readPreviewResource));
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.FABRICATOR_ENERGY_USAGE.getAsInt();
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
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        // Fill energy buffer from energy input slot
        pullEnergyFromAux();

        // Fabricators handle logic differently
        tickServerFabricator(level, pos, state);

        // Auto output item if option available
        tickItemAutoOutput(20, getItems(BlockContentsType.OUTPUT));
    }

    protected abstract void tickServerFabricator(ServerLevel level, BlockPos pos, BlockState state);

    public abstract Item getValidBlueprintItem();

    @Override
    protected boolean isItemValidForAuxInventory(int index, ItemResource resource)
    {
        return index == AUX_BLUEPRINT_SLOT ? resource.is(getValidBlueprintItem()) : super.isItemValidForAuxInventory(index, resource);
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);

        recipeCheck.deserialize(input);
        crafting = input.getBooleanOr("crafting", false);
        energyCraftProgress = input.getIntOr("recipe_energy", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);

        recipeCheck.serialize(output);
        output.putBoolean("crafting", crafting);
        output.putInt("recipe_energy", energyCraftProgress);
    }

    // For data watcher use only, called on server
    private ItemResource writePreviewResource()
    {
        ItemResource resource = getOutputInventory().getResource(0);

        if (isCrafting() && level instanceof ServerLevel serverLevel)
        {
            return recipeCheck.getLastUsedRecipe(serverLevel).map(holder -> holder.value().getFirstItemResult().getResource()).orElse(resource);
        }

        return resource;
    }

    private void readPreviewResource(ItemResource resource)
    {
        this.clientPreviewItem = resource.toStack();
    }
}