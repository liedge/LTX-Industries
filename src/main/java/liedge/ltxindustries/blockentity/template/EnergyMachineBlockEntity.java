package liedge.ltxindustries.blockentity.template;

import com.google.common.base.Preconditions;
import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.energy.LimaBlockEntityEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.util.LimaNbtUtil;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.blockentity.base.BlockIOConfiguration;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import static liedge.limacore.LimaCommonConstants.KEY_ENERGY_CONTAINER;

public abstract non-sealed class EnergyMachineBlockEntity extends LTXIMachineBlockEntity implements EnergyHolderBlockEntity
{
    private final LimaEnergyStorage energyStorage;
    private BlockIOConfiguration energyIOConfig;

    protected EnergyMachineBlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state, @Nullable LimaEnergyStorage energyStorage, int auxInventorySize)
    {
        super(type, pos, state, auxInventorySize);
        Preconditions.checkArgument(auxInventorySize > 1, "Auxiliary inventory for powered machines must have at least 2 slots.");

        this.energyStorage = energyStorage != null ? energyStorage : new LimaBlockEntityEnergyStorage(this);
        this.energyIOConfig = BlockIOConfiguration.create(type, BlockEntityInputType.ENERGY);
    }

    protected EnergyMachineBlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state, @Nullable LimaEnergyStorage energyStorage)
    {
        this(type, pos, state, energyStorage, 2);
    }

    protected void fillEnergyBuffer()
    {
        LimaEnergyStorage energy = getEnergyStorage();
        if (energy.getEnergyStored() < energy.getMaxEnergyStored())
        {
            IEnergyStorage itemEnergy = getAuxInventory().getStackInSlot(AUX_ENERGY_ITEM_SLOT).getCapability(Capabilities.EnergyStorage.ITEM);
            if (itemEnergy != null) LimaEnergyUtil.transferEnergyBetween(itemEnergy, energy, energy.getTransferRate(), false);
        }
    }

    protected void autoOutputEnergy()
    {
        if (energyIOConfig.autoOutput())
        {
            for (Direction side : Direction.values())
            {
                if (energyIOConfig.getIOAccess(getFacing(), side).allowsOutput())
                {
                    LimaEnergyStorage energy = getEnergyStorage();
                    IEnergyStorage neighborEnergy = getNeighborEnergyStorage(side);
                    if (neighborEnergy != null) LimaEnergyUtil.transferEnergyBetween(energy, neighborEnergy, energy.getTransferRate(), false);
                }
            }
        }
    }

    @Override
    protected BlockIOConfiguration getEnergyIOConfiguration()
    {
        return energyIOConfig;
    }

    @Override
    protected void setEnergyIOConfiguration(BlockIOConfiguration configuration)
    {
        this.energyIOConfig = configuration;
    }

    @Override
    public LimaEnergyStorage getEnergyStorage()
    {
        return energyStorage;
    }

    @Override
    public int getBaseEnergyTransferRate()
    {
        return getBaseEnergyCapacity() / 20;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput)
    {
        super.applyImplicitComponents(componentInput);

        getEnergyStorage().setEnergyStored(componentInput.getOrDefault(LimaCoreDataComponents.ENERGY, 0));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        super.collectImplicitComponents(components);

        if (getEnergyStorage() instanceof LimaBlockEntityEnergyStorage blockEntityEnergy) blockEntityEnergy.writeComponents(components);
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

        if (getEnergyStorage() instanceof LimaBlockEntityEnergyStorage blockEntityEnergy) LimaNbtUtil.deserializeInt(blockEntityEnergy, registries, tag.get(KEY_ENERGY_CONTAINER));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);

        if (getEnergyStorage() instanceof LimaBlockEntityEnergyStorage blockEntityEnergy) tag.put(KEY_ENERGY_CONTAINER, blockEntityEnergy.serializeNBT(registries));
    }
}