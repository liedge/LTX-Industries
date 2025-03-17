package liedge.limatech.blockentity;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.IOAccessSets;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.block.LimaTechBlockProperties;
import liedge.limatech.blockentity.io.MachineIOControl;
import liedge.limatech.blockentity.io.MachineInputType;
import liedge.limatech.registry.LimaTechMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class BaseESABlockEntity extends SidedItemEnergyMachineBlockEntity
{
    private final Map<Direction, BlockCapabilityCache<IEnergyStorage, Direction>> energyConnections = new EnumMap<>(Direction.class);

    public BaseESABlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, 5);
    }

    public abstract LimaColor getRemoteEnergyFillColor();

    public abstract float getRemoteEnergyFill();

    @Override
    protected MachineIOControl initItemIOControl(Direction front)
    {
        return new MachineIOControl(this, MachineInputType.ITEMS, IOAccessSets.ALL_ALLOWED, IOAccess.DISABLED, front, false, false);
    }

    @Override
    public IOAccess getItemSlotIO(int handlerIndex, int slot)
    {
        if (handlerIndex == 0)
        {
            if (slot == 0)
            {
                return IOAccess.INPUT_ONLY;
            }
            else if (slot > 0 && slot < 5)
            {
                ItemStack slotItem = getItemHandler().getStackInSlot(slot);
                if (slotItem.isEmpty()) return IOAccess.INPUT_ONLY;

                IEnergyStorage storage = slotItem.getCapability(Capabilities.EnergyStorage.ITEM);
                if (storage != null && storage.getEnergyStored() == storage.getMaxEnergyStored()) return IOAccess.OUTPUT_ONLY;
            }
        }

        return IOAccess.DISABLED;
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
                LimaEnergyUtil.transferEnergyBetween(itemEnergy, machineEnergy, machineEnergy.getMaxEnergyStored(), false);
        }

        // Charge item in output slot
        if (machineEnergy.getEnergyStored() > 0)
        {
            IntStream.rangeClosed(1, 4)
                    .mapToObj(i -> getItemHandler().getStackInSlot(i).getCapability(Capabilities.EnergyStorage.ITEM))
                    .flatMap(Stream::ofNullable)
                    .forEach(itemEnergy -> LimaEnergyUtil.transferEnergyBetween(machineEnergy, itemEnergy, machineEnergy.getEnergyStored(), false));
        }

        // Auto output energy if option enabled
        if (getEnergyControl().isAutoOutput())
        {
            Direction.stream().filter(s -> getEnergyControl().getSideIO(s).allowsOutput()).forEach(side -> {
                IEnergyStorage sideEnergy = energyConnections.get(side).getCapability();
                if (sideEnergy != null) LimaEnergyUtil.transferEnergyBetween(machineEnergy, sideEnergy, machineEnergy.getMaxEnergyStored(), false);
            });
        }
    }

    @Override
    protected void onLoadServer(ServerLevel level)
    {
        super.onLoadServer(level);

        for (Direction side : Direction.values())
        {
            energyConnections.put(side, createCapabilityCache(Capabilities.EnergyStorage.BLOCK, level, side));
        }

        updateAndSyncBlockState(level);
    }

    @Override
    public boolean isItemValid(int handlerIndex, int slot, ItemStack stack)
    {
        if (handlerIndex == 0)
        {
            return LimaItemUtil.hasEnergyCapability(stack);
        }
        else
        {
            return super.isItemValid(handlerIndex, slot, stack);
        }
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector) {}

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LimaTechMenus.ENERGY_STORAGE_ARRAY.get();
    }

    @Override
    protected void onIOControlsChangedInternal(MachineInputType inputType, Level level)
    {
        updateAndSyncBlockState(level);
    }

    @Override
    public boolean allowsAutoInput(MachineInputType inputType)
    {
        return false;
    }

    @Override
    public boolean allowsAutoOutput(MachineInputType inputType)
    {
        return inputType == MachineInputType.ENERGY;
    }

    private void updateAndSyncBlockState(Level level)
    {
        BlockState state = getBlockState();
        for (Direction side : Direction.values())
        {
            state = state.setValue(LimaTechBlockProperties.getESASideIOProperty(side), getEnergyControl().getSideIO(side));
        }
        level.setBlockAndUpdate(getBlockPos(), state);
    }
}