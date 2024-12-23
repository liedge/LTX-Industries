package liedge.limatech.blockentity;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.capability.energy.LimaBlockEntityEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.capability.itemhandler.LimaItemHandlerUtil;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.recipe.MutableRecipeReference;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaItemUtil;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.blockentity.io.MachineIOControl;
import liedge.limatech.blockentity.io.MachineInputType;
import liedge.limatech.blockentity.io.SidedMachineIOHolder;
import liedge.limatech.recipe.BaseFabricatingRecipe;
import liedge.limatech.registry.LimaTechMenus;
import liedge.limatech.registry.LimaTechRecipeTypes;
import liedge.limatech.util.config.LimaTechMachinesConfig;
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
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

import static liedge.limacore.util.LimaNbtUtil.deserializeString;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class FabricatorBlockEntity extends MachineBlockEntity implements TimedProcessMachineBlockEntity, SidedMachineIOHolder
{
    private final LimaBlockEntityEnergyStorage machineEnergy;
    private final LimaBlockEntityItemHandler machineItems;
    private final MutableRecipeReference<BaseFabricatingRecipe> currentRecipe = new MutableRecipeReference<>(LimaTechRecipeTypes.FABRICATING);
    private final MachineIOControl itemIOControl;
    private final Map<Direction, BlockCapabilityCache<IItemHandler, Direction>> itemConnections = new EnumMap<>(Direction.class);

    private boolean crafting = false;
    private int energyUsedForRecipe;
    private ItemStack previewItem = ItemStack.EMPTY;
    private int autoOutputTimer;
    private int clientProcessTime;

    public FabricatorBlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.machineEnergy = new LimaBlockEntityEnergyStorage(this, LimaTechMachinesConfig.FABRICATOR_ENERGY_CAPACITY.getAsInt(), LimaTechMachinesConfig.FABRICATOR_ENERGY_IO_RATE.getAsInt());
        this.machineItems = new LimaBlockEntityItemHandler(this, 2);

        Direction front = state.getValue(HORIZONTAL_FACING);
        this.itemIOControl = new MachineIOControl(this, MachineInputType.ITEMS, IOAccess.ONLY_OUTPUT_AND_DISABLED, IOAccess.DISABLED, front, false, false);
    }

    @Override
    public LimaBlockEntityEnergyStorage getEnergyStorage()
    {
        return machineEnergy;
    }

    @Override
    public LimaBlockEntityItemHandler getItemHandler()
    {
        return machineItems;
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

    public void startCrafting(RecipeHolder<BaseFabricatingRecipe> holder, LimaRecipeInput input, boolean forceStart)
    {
        BaseFabricatingRecipe recipe = holder.value();

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
        BaseFabricatingRecipe recipe = currentRecipe.getRecipeValue(level);

        if (insertResult && recipe != null)
        {
            getItemHandler().insertItem(1, recipe.assemble(null, nonNullRegistryAccess()), false);
        }

        energyUsedForRecipe = 0;
        setCrafting(false);
        currentRecipe.setHolderValue(null);
    }

    @Override
    public IOAccess getItemIOForSide(Direction side)
    {
        return itemIOControl.getSideIO(side);
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
        BaseFabricatingRecipe recipe = currentRecipe.getRecipeValue(level);
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
        if (itemIOControl.isAutoOutput())
        {
            if (autoOutputTimer >= 20)
            {
                for (Direction side : Direction.values())
                {
                    if (itemIOControl.getSideIO(side).allowsOutput())
                    {
                        IItemHandler adjacentInventory = itemConnections.get(side).getCapability();
                        if (adjacentInventory != null)
                        {
                            ItemStack outputItem = getItemHandler().getStackInSlot(1);
                            ItemStack insertResult = LimaItemHandlerUtil.insertIntoAnySlot(adjacentInventory, outputItem, true);

                            // Insert the extracted stack into adjacent inventory
                            if (outputItem != insertResult)
                            {
                                int inserted = outputItem.getCount() - insertResult.getCount();
                                LimaItemHandlerUtil.insertIntoAnySlot(adjacentInventory, getItemHandler().extractItem(1, inserted, false), false);
                            }
                        }
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
    public int getCurrentProcessTime()
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

    @Override
    public void setCurrentProcessTime(int currentProcessTime)
    {
        this.clientProcessTime = currentProcessTime;
    }

    @Override
    public int getTotalProcessDuration()
    {
        return 100;
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
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return slot != 0 || LimaItemUtil.hasEnergyCapability(stack);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();

        itemIOControl.setFacing(getBlockState().getValue(HORIZONTAL_FACING));
        if (level instanceof ServerLevel serverLevel)
        {
            for (Direction side : Direction.values())
            {
                itemConnections.put(side, createCapabilityCache(Capabilities.ItemHandler.BLOCK, serverLevel, side));
            }
        }
    }

    @Override
    public IOAccess getExternalItemSlotIO(int slot)
    {
        return slot == 0 ? IOAccess.INPUT_ONLY : IOAccess.OUTPUT_ONLY;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        deserializeString(currentRecipe, registries, tag.get("current_recipe"));
        crafting = tag.getBoolean("crafting");
        energyUsedForRecipe = tag.getInt("recipe_energy");
        itemIOControl.deserializeNBT(registries, tag.getCompound("item_io"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.put("current_recipe", currentRecipe.serializeNBT(registries));
        tag.putBoolean("crafting", crafting);
        tag.putInt("recipe_energy", energyUsedForRecipe);
        tag.put("item_io", itemIOControl.serializeNBT(registries));
    }

    @Override
    public @Nullable MachineIOControl getIOControls(MachineInputType inputType)
    {
        return inputType == MachineInputType.ITEMS ? itemIOControl : null;
    }

    @Override
    public void onIOControlsChanged(MachineInputType inputType)
    {
        invalidateCapabilities();
        setChanged();
    }

    @Override
    public boolean allowsAutoInput(MachineInputType inputType)
    {
        return false;
    }

    @Override
    public boolean allowsAutoOutput(MachineInputType inputType)
    {
        return inputType == MachineInputType.ITEMS;
    }
}