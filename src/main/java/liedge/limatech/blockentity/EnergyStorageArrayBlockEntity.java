package liedge.limatech.blockentity;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.inventory.menu.LimaMenuProvider;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.block.LimaTechBlockProperties;
import liedge.limatech.blockentity.io.MachineIOControl;
import liedge.limatech.blockentity.io.MachineInputType;
import liedge.limatech.blockentity.io.SidedMachineIOHolder;
import liedge.limatech.registry.LimaTechMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static liedge.limacore.LimaCommonConstants.KEY_ITEM_CONTAINER;
import static liedge.limacore.registry.LimaCoreDataComponents.ITEM_CONTAINER;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public abstract class EnergyStorageArrayBlockEntity extends LimaBlockEntity implements LimaMenuProvider, EnergyHolderBlockEntity, ItemHolderBlockEntity, SidedMachineIOHolder
{
    private final LimaBlockEntityItemHandler inventory;
    private final Map<Direction, BlockCapabilityCache<IEnergyStorage, Direction>> energyConnections = new EnumMap<>(Direction.class);

    public EnergyStorageArrayBlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.inventory = new LimaBlockEntityItemHandler(this, 5);
    }

    public abstract LimaColor getRemoteEnergyFillColor();

    public abstract float getRemoteEnergyFill();

    protected abstract MachineIOControl energyIOControl();

    @Override
    public LimaBlockEntityItemHandler getItemHandler()
    {
        return inventory;
    }

    @Override
    public void onEnergyChanged()
    {
        setChanged();
    }

    @Override
    public void onItemSlotChanged(int slot)
    {
        setChanged();
    }

    @Override
    public IOAccess getEnergyIOForSide(Direction side)
    {
        return energyIOControl().getSideIO(side);
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
        if (energyIOControl().isAutoOutput())
        {
            Direction.stream().filter(s -> energyIOControl().getSideIO(s).allowsOutput()).forEach(side -> {
                IEnergyStorage sideEnergy = energyConnections.get(side).getCapability();
                if (sideEnergy != null) LimaEnergyUtil.transferEnergyBetween(machineEnergy, sideEnergy, machineEnergy.getMaxEnergyStored(), false);
            });
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput)
    {
        getItemHandler().copyFromComponent(componentInput.getOrDefault(ITEM_CONTAINER, ItemContainerContents.EMPTY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        components.set(ITEM_CONTAINER, getItemHandler().copyToComponent());
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag)
    {
        tag.remove(KEY_ITEM_CONTAINER);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        energyIOControl().deserializeNBT(registries, tag.getCompound("energy_io"));
        getItemHandler().deserializeNBT(registries, tag.getCompound(KEY_ITEM_CONTAINER));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.put("energy_io", energyIOControl().serializeNBT(registries));
        tag.put(KEY_ITEM_CONTAINER, getItemHandler().serializeNBT(registries));
    }

    @Override
    protected void onLoadServer(Level level)
    {
        energyIOControl().setFacing(getBlockState().getValue(HORIZONTAL_FACING));
        for (Direction side : Direction.values())
        {
            energyConnections.put(side, createCapabilityCache(Capabilities.EnergyStorage.BLOCK, (ServerLevel) level, side));
        }
        updateAndSyncBlockState(level);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return LimaItemUtil.hasEnergyCapability(stack);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector) {}

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LimaTechMenus.ENERGY_STORAGE_ARRAY.get();
    }

    @Override
    public @Nullable MachineIOControl getIOControls(MachineInputType inputType)
    {
        return inputType == MachineInputType.ENERGY ? energyIOControl() : null;
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
            state = state.setValue(LimaTechBlockProperties.getESASideIOProperty(side), energyIOControl().getSideIO(side));
        }
        level.setBlockAndUpdate(getBlockPos(), state);
    }
}