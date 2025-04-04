package liedge.limatech.blockentity;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.capability.energy.LimaBlockEntityEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.capability.itemhandler.LimaItemHandlerUtil;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.recipe.MutableRecipeReference;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaItemUtil;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.recipe.FabricatingRecipe;
import liedge.limatech.registry.game.LimaTechBlockEntities;
import liedge.limatech.registry.game.LimaTechMenus;
import liedge.limatech.registry.game.LimaTechRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.EnumMap;
import java.util.Map;

import static liedge.limacore.util.LimaNbtUtil.deserializeString;
import static liedge.limatech.util.config.LimaTechMachinesConfig.FABRICATOR_ENERGY_CAPACITY;
import static liedge.limatech.util.config.LimaTechMachinesConfig.FABRICATOR_ENERGY_IO_RATE;

public class FabricatorBlockEntity extends SidedItemEnergyMachineBlockEntity
{
    private final LimaBlockEntityEnergyStorage machineEnergy;
    private final MutableRecipeReference<FabricatingRecipe> currentRecipe = new MutableRecipeReference<>(LimaTechRecipeTypes.FABRICATING);
    private final Map<Direction, BlockCapabilityCache<IItemHandler, Direction>> itemConnections = new EnumMap<>(Direction.class);

    private boolean crafting = false;
    private int energyUsedForRecipe;
    private ItemStack previewItem = ItemStack.EMPTY;
    private int autoOutputTimer;
    private int clientProcessTime;

    public FabricatorBlockEntity(BlockPos pos, BlockState state)
    {
        super(LimaTechBlockEntities.FABRICATOR.get(), pos, state, 2);
        this.machineEnergy = new LimaBlockEntityEnergyStorage(this);
    }

    public int getClientProcessTime()
    {
        if (checkServerSide())
        {
            return currentRecipe.toIntOrElse(level, recipe -> (int) (LimaMathUtil.divideFloat(energyUsedForRecipe, recipe.getEnergyRequired()) * 100f), 0);
        }
        else
        {
            return clientProcessTime;
        }
    }

    public LimaDataWatcher<Integer> keepProgressSynced()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getClientProcessTime, i -> this.clientProcessTime = i);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return FABRICATOR_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyTransferRate()
    {
        return FABRICATOR_ENERGY_IO_RATE.getAsInt();
    }

    @Override
    public LimaBlockEntityEnergyStorage getEnergyStorage()
    {
        return machineEnergy;
    }

    public MutableRecipeReference<FabricatingRecipe> getCurrentRecipe()
    {
        return currentRecipe;
    }

    public boolean isCrafting()
    {
        return crafting;
    }

    private void setCrafting(boolean crafting)
    {
        this.crafting = crafting;
        setChanged();
    }

    public void startCrafting(RecipeHolder<FabricatingRecipe> holder, LimaRecipeInput input, boolean forceStart)
    {
        FabricatingRecipe recipe = holder.value();

        if (!isCrafting() && LimaItemUtil.canMergeItemStacks(getItemHandler().getStackInSlot(1), recipe.getResultItem(null)))
        {
            if (forceStart)
            {
                setCrafting(true);
                currentRecipe.setHolderValue(holder);
            }
            else if (recipe.matches(input, null))
            {
                recipe.consumeIngredientsLenientSlots(input, false);
                setCrafting(true);
                currentRecipe.setHolderValue(holder);
            }
        }
    }

    public ItemStack getPreviewItem()
    {
        return previewItem;
    }

    private void stopCrafting(boolean insertResult)
    {
        FabricatingRecipe recipe = currentRecipe.getRecipeValue(level);

        if (insertResult && recipe != null)
        {
            getItemHandler().insertItem(1, recipe.assemble(null, nonNullLevel().registryAccess()), false);
        }

        energyUsedForRecipe = 0;
        setCrafting(false);
        currentRecipe.setHolderValue(null);
    }

    @Override
    protected void tickServer(Level level, BlockPos pos, BlockState state)
    {
        LimaEnergyStorage machineEnergy = getEnergyStorage();

        // Fill buffer from input slot
        if (machineEnergy.getEnergyStored() < machineEnergy.getMaxEnergyStored())
        {
            IEnergyStorage itemEnergy = getItemHandler().getStackInSlot(0).getCapability(Capabilities.EnergyStorage.ITEM);
            if (itemEnergy != null)
            {
                LimaEnergyUtil.transferEnergyBetween(itemEnergy, machineEnergy, machineEnergy.getTransferRate(), false);
            }
        }

        // Try progressing crafting recipe
        FabricatingRecipe recipe = currentRecipe.getRecipeValue(level);
        if (isCrafting() && recipe != null)
        {
            int totalEnergyRequired = recipe.getEnergyRequired();

            if (energyUsedForRecipe < totalEnergyRequired)
            {
                int energyRemainingNeeded = (totalEnergyRequired - energyUsedForRecipe);
                energyUsedForRecipe += machineEnergy.extractEnergy(energyRemainingNeeded, false, false);
            }
            else
            {
                stopCrafting(true);
            }
        }
        else
        {
            stopCrafting(false);
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
                        if (adjacentInventory != null) LimaItemHandlerUtil.transferStackBetweenInventories(getItemHandler(), adjacentInventory, 1);
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
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.BOOL, this::isCrafting, this::setCrafting));
        collector.register(AutomaticDataWatcher.keepItemSynced(() -> currentRecipe.mapOrElse(level, r -> r.getResultItem(null), getItemHandler().getStackInSlot(1).copy()), stack -> this.previewItem = stack));
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LimaTechMenus.FABRICATOR.get();
    }

    @Override
    protected void onLoadServer(ServerLevel level)
    {
        super.onLoadServer(level);

        for (Direction side : Direction.values())
        {
            itemConnections.put(side, createCapabilityCache(Capabilities.ItemHandler.BLOCK, level, side));
        }
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
    public IOAccess getItemSlotIO(int handlerIndex, int slot)
    {
        if (handlerIndex == 0) return slot == 0 ? IOAccess.INPUT_ONLY : IOAccess.OUTPUT_ONLY;

        return super.getItemSlotIO(handlerIndex, slot);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);

        deserializeString(currentRecipe, registries, tag.get("current_recipe"));
        crafting = tag.getBoolean("crafting");
        energyUsedForRecipe = tag.getInt("recipe_energy");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);

        tag.put("current_recipe", currentRecipe.serializeNBT(registries));
        tag.putBoolean("crafting", crafting);
        tag.putInt("recipe_energy", energyUsedForRecipe);
    }
}