package liedge.limatech.blockentity;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.capability.energy.LimaBlockEntityEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.blockentity.io.MachineIOControl;
import liedge.limatech.blockentity.io.MachineInputType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

import static liedge.limacore.LimaCommonConstants.KEY_ENERGY_CONTAINER;
import static liedge.limacore.registry.LimaCoreDataComponents.ENERGY;
import static liedge.limacore.util.LimaNbtUtil.deserializeInt;
import static liedge.limatech.util.config.LimaTechMachinesConfig.ESA_BASE_ENERGY_CAPACITY;
import static liedge.limatech.util.config.LimaTechMachinesConfig.ESA_BASE_TRANSFER_RATE;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class TieredESABlockEntity extends EnergyStorageArrayBlockEntity
{
    private final MachineIOControl energyControl;
    private final LimaBlockEntityEnergyStorage energyStorage;
    private int remoteEnergyFill;

    public TieredESABlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.energyStorage = new LimaBlockEntityEnergyStorage(this, ESA_BASE_ENERGY_CAPACITY.getAsInt(), ESA_BASE_TRANSFER_RATE.getAsInt());
        Direction front = state.getValue(HORIZONTAL_FACING);
        this.energyControl = new MachineIOControl(this, MachineInputType.ENERGY, IOAccess.INPUT_OR_OUTPUT_ONLY_AND_DISABLED, IOAccess.INPUT_ONLY, front, false, true);
    }

    @Override
    public LimaColor getRemoteEnergyFillColor()
    {
        return LimaTechConstants.REM_BLUE;
    }

    @Override
    public float getRemoteEnergyFill()
    {
        return remoteEnergyFill / 10f;
    }

    @Override
    protected MachineIOControl energyIOControl()
    {
        return energyControl;
    }

    @Override
    public LimaBlockEntityEnergyStorage getEnergyStorage()
    {
        return energyStorage;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, () -> Mth.floor(LimaEnergyUtil.getFillPercentage(getEnergyStorage()) * 10f), i -> this.remoteEnergyFill = i));
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput)
    {
        super.applyImplicitComponents(componentInput);
        energyStorage.setEnergyStored(componentInput.getOrDefault(ENERGY, 0));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        super.collectImplicitComponents(components);
        components.set(ENERGY, energyStorage.getEnergyStored());
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag)
    {
        super.removeComponentsFromTag(tag);
        tag.remove(KEY_ENERGY_CONTAINER);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        deserializeInt(energyStorage, registries, tag.get(KEY_ENERGY_CONTAINER));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.put(KEY_ENERGY_CONTAINER, energyStorage.serializeNBT(registries));
    }
}