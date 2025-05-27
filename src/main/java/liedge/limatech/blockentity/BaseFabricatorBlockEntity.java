package liedge.limatech.blockentity;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.recipe.LimaRecipeCheck;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaItemUtil;
import liedge.limacore.util.LimaNbtUtil;
import liedge.limatech.blockentity.base.EnergyConsumerBlockEntity;
import liedge.limatech.blockentity.base.RecipeMachineBlockEntity;
import liedge.limatech.blockentity.base.SidedAccessBlockEntityType;
import liedge.limatech.lib.upgrades.machine.MachineUpgrades;
import liedge.limatech.recipe.FabricatingRecipe;
import liedge.limatech.registry.game.LimaTechItems;
import liedge.limatech.registry.game.LimaTechRecipeTypes;
import liedge.limatech.util.LimaTechTooltipUtil;
import liedge.limatech.util.config.LimaTechMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;

public abstract class BaseFabricatorBlockEntity extends SidedItemEnergyMachineBlockEntity implements EnergyConsumerBlockEntity, RecipeMachineBlockEntity<LimaRecipeInput, FabricatingRecipe>
{
    public static final int OUTPUT_SLOT = 1;
    public static final int BLUEPRINT_ITEM_SLOT = 2;

    private final LimaRecipeCheck<LimaRecipeInput, FabricatingRecipe> recipeCheck = LimaRecipeCheck.create(LimaTechRecipeTypes.FABRICATING);

    // Common properties
    private int energyUsage = getBaseEnergyUsage();
    private boolean crafting;
    protected int energyCraftProgress;

    // Client properties
    private ItemStack clientPreviewItem = ItemStack.EMPTY;

    protected BaseFabricatorBlockEntity(SidedAccessBlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize)
    {
        super(type, pos, state, inventorySize);
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
        return LimaTechMachinesConfig.FABRICATOR_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public void appendStatsTooltips(TooltipLineConsumer consumer)
    {
        LimaTechTooltipUtil.appendEnergyUsagePerTickTooltip(consumer, getEnergyUsage());
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
    public int getOutputSlot()
    {
        return OUTPUT_SLOT;
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
        collector.register(AutomaticDataWatcher.keepItemSynced(this::createPreviewItem, stack -> this.clientPreviewItem = stack));
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LimaTechMachinesConfig.FABRICATOR_ENERGY_USAGE.getAsInt();
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
        fillEnergyBuffer();

        // Fabricators handle logic differently
        tickServerFabricator(level, pos, state);

        // Auto output item if option available
        autoOutputItems(20, getOutputSlot(), 1);
    }

    protected abstract void tickServerFabricator(ServerLevel level, BlockPos pos, BlockState state);

    @Override
    protected boolean isItemValidForPrimaryHandler(int slot, ItemStack stack)
    {
        return switch (slot)
        {
            case ENERGY_ITEM_SLOT -> LimaItemUtil.hasEnergyCapability(stack);
            case BLUEPRINT_ITEM_SLOT -> stack.is(LimaTechItems.FABRICATION_BLUEPRINT);
            default -> true;
        };
    }

    @Override
    public IOAccess getPrimaryHandlerItemSlotIO(int slot)
    {
        if (slot == OUTPUT_SLOT) return IOAccess.OUTPUT_ONLY;
        else if (isInputSlot(slot)) return IOAccess.INPUT_ONLY;
        else return IOAccess.DISABLED;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);

        LimaNbtUtil.deserializeString(recipeCheck, registries, tag.get("current_recipe"));
        crafting = tag.getBoolean("crafting");
        energyCraftProgress = tag.getInt("recipe_energy");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);

        tag.put("current_recipe", recipeCheck.serializeNBT(registries));
        tag.putBoolean("crafting", crafting);
        tag.putInt("recipe_energy", energyCraftProgress);
    }

    // For data watcher use only, called on server
    private ItemStack createPreviewItem()
    {
        ItemStack currentOutputItem = getItemHandler().getStackInSlot(OUTPUT_SLOT).copy();

        if (isCrafting())
        {
            return recipeCheck.getLastUsedRecipe(level).map(r -> r.value().getResultItem()).orElse(currentOutputItem);
        }
        else
        {
            return currentOutputItem;
        }
    }
}