package liedge.limatech.blockentity;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.block.LimaTechBlockProperties;
import liedge.limatech.blockentity.io.MachineIOControl;
import liedge.limatech.blockentity.io.MachineInputType;
import liedge.limatech.blockentity.io.SidedMachineIOHolder;
import liedge.limatech.registry.LimaTechMenus;
import liedge.limatech.util.config.LimaTechMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class EnergyStorageArrayBlockEntity extends MachineBlockEntity implements SidedMachineIOHolder
{
    public static final int INPUT_ENERGY_ITEM_SLOT = 0;
    public static final int OUTPUT_ENERGY_ITEM_SLOT = 1;

    private final MachineIOControl energyIOControl;
    private final Map<Direction, BlockCapabilityCache<IEnergyStorage, Direction>> energyConnections = new EnumMap<>(Direction.class);

    private int remoteEnergyFill;

    public EnergyStorageArrayBlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, LimaTechMachinesConfig.ESA_BASE_ENERGY_CAPACITY.getAsInt(), LimaTechMachinesConfig.ESA_BASE_TRANSFER_RATE.getAsInt(), 2);

        Direction front = state.getValue(HORIZONTAL_FACING);
        this.energyIOControl = new MachineIOControl(this, MachineInputType.ENERGY, IOAccess.INPUT_OR_OUTPUT_ONLY_AND_DISABLED, IOAccess.INPUT_ONLY, front, false, true);
    }

    public float getRemoteEnergyFill()
    {
        return remoteEnergyFill / 10f;
    }

    @Override
    public IOAccess getEnergyIOForSide(Direction side)
    {
        return energyIOControl.getSideIO(side);
    }

    @Override
    public void onBlockStateUpdated(BlockPos pos, BlockState oldState, BlockState newState)
    {
        if (checkServerSide())
        {
            Direction oldFront = oldState.getValue(HORIZONTAL_FACING);
            Direction newFront = newState.getValue(HORIZONTAL_FACING);

            if (oldFront != newFront) getIOControlsOrThrow(MachineInputType.ENERGY).setFacing(newFront);
        }
    }

    @Override
    protected void tickServer(Level level, BlockPos pos, BlockState state)
    {
        LimaEnergyStorage machineEnergy = getEnergyStorage();

        // Fill buffer from input slot
        if (machineEnergy.getEnergyStored() < machineEnergy.getMaxEnergyStored())
        {
            IEnergyStorage itemEnergy = getItemHandler().getStackInSlot(INPUT_ENERGY_ITEM_SLOT).getCapability(Capabilities.EnergyStorage.ITEM);
            if (itemEnergy != null)
                LimaEnergyUtil.transferEnergyBetween(itemEnergy, machineEnergy, machineEnergy.getMaxEnergyStored(), false);
        }

        // Charge item in output slot
        if (machineEnergy.getEnergyStored() > 0)
        {
            IEnergyStorage itemEnergy = getItemHandler().getStackInSlot(OUTPUT_ENERGY_ITEM_SLOT).getCapability(Capabilities.EnergyStorage.ITEM);
            if (itemEnergy != null)
                LimaEnergyUtil.transferEnergyBetween(machineEnergy, itemEnergy, machineEnergy.getMaxEnergyStored(), false);
        }

        // Auto output energy if option enabled
        if (energyIOControl.isAutoOutput())
        {
            for (Direction side : Direction.values())
            {
                if (energyIOControl.getSideIO(side).allowsOutput())
                {
                    IEnergyStorage adjacentEnergy = energyConnections.get(side).getCapability();
                    if (adjacentEnergy != null)
                    {
                        LimaEnergyUtil.transferEnergyBetween(machineEnergy, adjacentEnergy, machineEnergy.getMaxEnergyStored(), false);
                    }
                }
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        energyIOControl.deserializeNBT(registries, tag.getCompound("energy_io"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.put("energy_io", energyIOControl.serializeNBT(registries));
    }

    @Override
    public void onLoad()
    {
        super.onLoad();

        energyIOControl.setFacing(getBlockState().getValue(HORIZONTAL_FACING));
        if (level instanceof ServerLevel serverLevel)
        {
            for (Direction side : Direction.values())
            {
                energyConnections.put(side, createCapabilityCache(Capabilities.EnergyStorage.BLOCK, serverLevel, side));
            }

            updateAndSyncBlockState(serverLevel);
        }
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return LimaItemUtil.hasEnergyCapability(stack);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, () -> Mth.floor(LimaEnergyUtil.getFillPercentage(getEnergyStorage()) * 10f), i -> this.remoteEnergyFill = i));
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LimaTechMenus.ENERGY_STORAGE_ARRAY.get();
    }

    @Override
    public @Nullable MachineIOControl getIOControls(MachineInputType inputType)
    {
        return inputType == MachineInputType.ENERGY ? energyIOControl : null;
    }

    @Override
    public void onIOControlsChanged(MachineInputType inputType)
    {
        if (level != null && !level.isClientSide())
        {
            invalidateCapabilities();
            setChanged();
            updateAndSyncBlockState(level);
        }
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
            state = state.setValue(LimaTechBlockProperties.getESASideIOProperty(side), energyIOControl.getSideIO(side));
        }
        level.setBlockAndUpdate(getBlockPos(), state);
    }
}