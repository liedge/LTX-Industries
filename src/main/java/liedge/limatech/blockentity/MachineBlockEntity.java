package liedge.limatech.blockentity;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.energy.LimaBlockEntityEnergyStorage;
import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.inventory.menu.LimaMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;

import static liedge.limacore.LimaCommonConstants.KEY_ENERGY_CONTAINER;
import static liedge.limacore.LimaCommonConstants.KEY_ITEM_CONTAINER;
import static liedge.limacore.registry.LimaCoreDataComponents.ENERGY;
import static liedge.limacore.registry.LimaCoreDataComponents.ITEM_CONTAINER;
import static liedge.limacore.util.LimaNbtUtil.deserializeInt;

public abstract class MachineBlockEntity extends LimaBlockEntity implements LimaMenuProvider, EnergyHolderBlockEntity, ItemHolderBlockEntity
{
    protected MachineBlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public abstract LimaBlockEntityEnergyStorage getEnergyStorage();

    @Override
    public abstract LimaBlockEntityItemHandler getItemHandler();

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
        return IOAccess.INPUT_ONLY;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput)
    {
        getEnergyStorage().setEnergyStored(componentInput.getOrDefault(ENERGY, 0));
        getItemHandler().copyFromComponent(componentInput.getOrDefault(ITEM_CONTAINER, ItemContainerContents.EMPTY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        components.set(ENERGY, getEnergyStorage().getEnergyStored());
        components.set(ITEM_CONTAINER, getItemHandler().copyToComponent());
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag)
    {
        tag.remove(KEY_ENERGY_CONTAINER);
        tag.remove(KEY_ITEM_CONTAINER);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        deserializeInt(getEnergyStorage(), registries, tag.get(KEY_ENERGY_CONTAINER));
        getItemHandler().deserializeNBT(registries, tag.getCompound(KEY_ITEM_CONTAINER));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.put(KEY_ENERGY_CONTAINER, getEnergyStorage().serializeNBT(registries));
        tag.put(KEY_ITEM_CONTAINER, getItemHandler().serializeNBT(registries));
    }
}