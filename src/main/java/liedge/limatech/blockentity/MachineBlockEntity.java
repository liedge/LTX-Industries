package liedge.limatech.blockentity;

import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.inventory.ItemHandlerHolder;
import liedge.limacore.inventory.LimaItemStackHandler;
import liedge.limacore.inventory.menu.LimaMenuProvider;
import liedge.limacore.lib.energy.BlockEnergyStorage;
import liedge.limacore.lib.energy.EnergyStorageHolder;
import liedge.limacore.lib.energy.LimaEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;

import static liedge.limacore.LimaCommonConstants.KEY_ENERGY_CONTAINER;
import static liedge.limacore.LimaCommonConstants.KEY_ITEM_CONTAINER;
import static liedge.limacore.registry.LimaCoreDataComponents.ENERGY;
import static liedge.limacore.registry.LimaCoreDataComponents.ITEM_CONTAINER;
import static liedge.limacore.util.LimaNbtUtil.deserializeInt;

public abstract class MachineBlockEntity extends LimaBlockEntity implements LimaMenuProvider, EnergyStorageHolder, ItemHandlerHolder
{
    protected final BlockEnergyStorage machineEnergy;
    protected final LimaItemStackHandler machineItems;

    protected MachineBlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state, int energyCapacity, int energyTransferRate, int inventorySize)
    {
        super(type, pos, state);
        this.machineEnergy = new BlockEnergyStorage(this, energyCapacity, energyTransferRate);
        this.machineItems = new LimaItemStackHandler(this, inventorySize);
    }

    @Override
    public LimaEnergyStorage getEnergyStorage()
    {
        return machineEnergy;
    }

    @Override
    public LimaItemStackHandler getItemHandler()
    {
        return machineItems;
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
    public abstract boolean isItemValid(int slot, ItemStack stack);

    @Override
    protected void applyImplicitComponents(DataComponentInput input)
    {
        machineEnergy.setEnergyStored(input.getOrDefault(ENERGY, 0));
        machineItems.copyFromComponent(input.getOrDefault(ITEM_CONTAINER, ItemContainerContents.EMPTY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder)
    {
        builder.set(ENERGY, machineEnergy.getEnergyStored());
        builder.set(ITEM_CONTAINER, machineItems.copyToComponent());
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag)
    {
        super.removeComponentsFromTag(tag);
        tag.remove(KEY_ENERGY_CONTAINER);
        tag.remove(KEY_ITEM_CONTAINER);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        deserializeInt(machineEnergy, registries, tag.get(KEY_ENERGY_CONTAINER));
        machineItems.deserializeNBT(registries, tag.getCompound(KEY_ITEM_CONTAINER));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.put(KEY_ENERGY_CONTAINER, machineEnergy.serializeNBT(registries));
        tag.put(KEY_ITEM_CONTAINER, machineItems.serializeNBT(registries));
    }
}