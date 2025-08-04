package liedge.ltxindustries.blockentity;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.capability.itemhandler.BlockInventoryType;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.blockentity.base.IOController;
import liedge.ltxindustries.blockentity.base.SidedAccessBlockEntityType;
import liedge.ltxindustries.blockentity.template.EnergyMachineBlockEntity;
import liedge.ltxindustries.registry.game.LTXIMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public abstract class BaseESABlockEntity extends EnergyMachineBlockEntity
{
    private final LimaBlockEntityItemHandler chargingInventory;
    private final Map<Direction, BlockCapabilityCache<IEnergyStorage, Direction>> energyConnections = new EnumMap<>(Direction.class);

    public BaseESABlockEntity(SidedAccessBlockEntityType<?> type, BlockPos pos, BlockState state, @Nullable LimaEnergyStorage energyStorage)
    {
        super(type, pos, state, energyStorage);
        this.chargingInventory = new LimaBlockEntityItemHandler(this, 4, BlockInventoryType.GENERAL);
    }

    public abstract LimaColor getRemoteEnergyFillColor();

    public abstract float getRemoteEnergyFill();

    public LimaBlockEntityItemHandler getChargingInventory()
    {
        return chargingInventory;
    }

    @Override
    public boolean hasStatsTooltips()
    {
        return false;
    }

    @Override
    public @Nullable LimaBlockEntityItemHandler getItemHandler(BlockInventoryType inventoryType)
    {
        return switch (inventoryType)
        {
            case GENERAL -> chargingInventory;
            case AUXILIARY -> getAuxInventory();
            default -> null;
        };
    }

    @Override
    public boolean isItemValid(BlockInventoryType inventoryType, int slot, ItemStack stack)
    {
        return inventoryType == BlockInventoryType.GENERAL ? LimaItemUtil.hasEnergyCapability(stack) : super.isItemValid(inventoryType, slot, stack);
    }

    @Override
    public IOAccess getItemHandlerSlotIO(BlockInventoryType type, int slot)
    {
        return type == BlockInventoryType.GENERAL ? checkStackEnergy(chargingInventory.getStackInSlot(slot)) : IOAccess.DISABLED;
    }

    private IOAccess checkStackEnergy(ItemStack stack)
    {
        if (stack.isEmpty()) return IOAccess.INPUT_ONLY;

        IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (storage == null || storage.getEnergyStored() >= storage.getMaxEnergyStored()) return IOAccess.OUTPUT_ONLY;

        return IOAccess.DISABLED;
    }

    @Override
    public @Nullable IItemHandler createItemIOWrapper(@Nullable Direction side)
    {
        return chargingInventory.createIOWrapper(getSideIOForItems(side));
    }

    @Override
    public @Nullable IEnergyStorage getNeighborEnergyStorage(Direction side)
    {
        return energyConnections.get(side).getCapability();
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        // Fill buffer from input slot
        fillEnergyBuffer();

        // Charge items in the charging inventory
        LimaEnergyStorage machineEnergy = getEnergyStorage();
        if (machineEnergy.getEnergyStored() > 0)
        {
            for (int i = 0; i < chargingInventory.getSlots(); i++)
            {
                ItemStack stack = chargingInventory.getStackInSlot(i);
                IEnergyStorage stackEnergy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
                if (stackEnergy != null)
                {
                    LimaEnergyUtil.transferEnergyBetween(machineEnergy, stackEnergy, machineEnergy.getEnergyStored(), false);
                }
            }
        }

        // Auto output energy if option enabled
        autoOutputItems(100, chargingInventory, stack -> checkStackEnergy(stack).allowsOutput());
        autoOutputEnergy();
    }

    @Override
    protected void createConnectionCaches(ServerLevel level, Direction side)
    {
        super.createConnectionCaches(level, side);
        energyConnections.put(side, createCapabilityCache(Capabilities.EnergyStorage.BLOCK, level, side));
    }

    @Override
    protected void onLoadServer(ServerLevel level)
    {
        super.onLoadServer(level);
        updateAndSyncBlockState(level);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector) {}

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LTXIMenus.ENERGY_STORAGE_ARRAY.get();
    }

    @Override
    protected void onIOControlsChanged(BlockEntityInputType inputType, Level level)
    {
        if (inputType == BlockEntityInputType.ENERGY) updateAndSyncBlockState(level);
    }

    private void updateAndSyncBlockState(Level level)
    {
        IOController energyControl = getIOController(BlockEntityInputType.ENERGY);
        BlockState state = getBlockState();
        for (Direction side : Direction.values())
        {
            state = state.setValue(LTXIBlockProperties.getESASideIOProperty(side), energyControl.getSideIOState(side));
        }
        level.setBlockAndUpdate(getBlockPos(), state);
    }
}