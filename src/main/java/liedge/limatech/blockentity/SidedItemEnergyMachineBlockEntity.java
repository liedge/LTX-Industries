package liedge.limatech.blockentity;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.energy.LimaBlockEntityEnergyStorage;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import liedge.limacore.inventory.menu.LimaMenuProvider;
import liedge.limacore.util.LimaNbtUtil;
import liedge.limatech.blockentity.base.BlockEntityInputType;
import liedge.limatech.blockentity.base.IOController;
import liedge.limatech.blockentity.base.SidedAccessBlockEntity;
import liedge.limatech.blockentity.base.SidedAccessBlockEntityType;
import liedge.limatech.lib.upgrades.machine.MachineUpgrades;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static liedge.limacore.LimaCommonConstants.KEY_ENERGY_CONTAINER;
import static liedge.limacore.LimaCommonConstants.KEY_ITEM_CONTAINER;
import static liedge.limacore.registry.game.LimaCoreDataComponents.*;
import static liedge.limatech.registry.game.LimaTechDataComponents.MACHINE_UPGRADES;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public abstract class SidedItemEnergyMachineBlockEntity extends LimaBlockEntity implements LimaMenuProvider, EnergyHolderBlockEntity, UpgradableMachineBlockEntity, SidedAccessBlockEntity
{
    private final LimaBlockEntityItemHandler inventory;
    private final IOController energyControl;
    private final IOController itemControl;
    private final LimaBlockEntityItemHandler upgradeModuleSlot;
    private MachineUpgrades upgrades = MachineUpgrades.EMPTY;

    protected SidedItemEnergyMachineBlockEntity(SidedAccessBlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize)
    {
        super(type, pos, state);
        this.inventory = new LimaBlockEntityItemHandler(this, inventorySize);
        this.upgradeModuleSlot = new LimaBlockEntityItemHandler(this, 1, 1);
        this.energyControl = new IOController(this, BlockEntityInputType.ENERGY);
        this.itemControl = new IOController(this, BlockEntityInputType.ITEMS);
    }

    // IO control methods/initializers
    @Override
    public void onBlockStateUpdated(BlockPos pos, BlockState oldState, BlockState newState)
    {
        if (checkServerSide())
        {
            onIOControlsChanged(BlockEntityInputType.ITEMS);
            onIOControlsChanged(BlockEntityInputType.ENERGY);
        }
    }

    @Override
    public IOController getIOController(BlockEntityInputType inputType) throws IllegalArgumentException
    {
        return switch (inputType)
        {
            case ITEMS -> itemControl;
            case ENERGY -> energyControl;
            case FLUIDS -> throw new IllegalArgumentException("Fluid IO controls not supported.");
        };
    }

    @Override
    public Direction getFacing()
    {
        return getBlockState().getValue(HORIZONTAL_FACING);
    }

    @Override
    public void onIOControlsChanged(BlockEntityInputType inputType)
    {
        if (level != null && !level.isClientSide())
        {
            invalidateCapabilities();
            setChanged();
            onIOControlsChangedInternal(inputType, level);
        }
    }

    public IOController getItemControl()
    {
        return itemControl;
    }

    public IOController getEnergyControl()
    {
        return energyControl;
    }

    protected void onIOControlsChangedInternal(BlockEntityInputType inputType, Level level) {}

    // Item handler methods
    @Override
    public LimaItemHandlerBase getItemHandler(int handlerIndex) throws IndexOutOfBoundsException
    {
        return switch (handlerIndex)
        {
            case 0 -> inventory;
            case 1 -> upgradeModuleSlot;
            default -> throw new IndexOutOfBoundsException("Invalid item handler index " + handlerIndex + " accessed.");
        };
    }

    @Override
    public IOAccess getItemHandlerSideIO(Direction side)
    {
        return itemControl.getSideIOState(side);
    }

    @Override
    public boolean isItemValid(int handlerIndex, int slot, ItemStack stack)
    {
        return true;
    }

    // Upgrades methods
    @Override
    public MachineUpgrades getUpgrades()
    {
        return upgrades;
    }

    @Override
    public void setUpgrades(MachineUpgrades upgrades)
    {
        this.upgrades = upgrades;

        if (level instanceof ServerLevel serverLevel)
        {
            setChanged();
            onUpgradeRefresh(serverLevel);
        }
    }

    // Energy methods
    @Override
    public IOAccess getEnergyIOForSide(Direction side)
    {
        return energyControl.getSideIOState(side);
    }

    // Load/Save methods
    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput)
    {
        if (getEnergyStorage() instanceof LimaBlockEntityEnergyStorage) getEnergyStorage().setEnergyStored(componentInput.getOrDefault(ENERGY, 0));
        inventory.copyFromComponent(componentInput.getOrDefault(ITEM_CONTAINER, ItemContainerContents.EMPTY));
        setUpgrades(componentInput.getOrDefault(MACHINE_UPGRADES, MachineUpgrades.EMPTY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        if (getEnergyStorage() instanceof LimaBlockEntityEnergyStorage) components.set(ENERGY, getEnergyStorage().getEnergyStored());

        if (getEnergyStorage() instanceof LimaBlockEntityEnergyStorage energyStorage)
        {
            components.set(ENERGY, energyStorage.getEnergyStored());
            components.set(ENERGY_PROPERTIES, energyStorage.copyProperties());
        }

        components.set(ITEM_CONTAINER, inventory.copyToComponent());
        components.set(MACHINE_UPGRADES, upgrades);
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag)
    {
        tag.remove(KEY_ENERGY_CONTAINER);
        tag.remove(KEY_ITEM_CONTAINER);
        tag.remove("upgrades");
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound(KEY_ITEM_CONTAINER));
        if (getEnergyStorage() instanceof LimaBlockEntityEnergyStorage blockEntityEnergy) LimaNbtUtil.deserializeInt(blockEntityEnergy, registries, tag.get(KEY_ENERGY_CONTAINER));
        itemControl.deserializeNBT(registries, tag.getCompound("item_io"));
        energyControl.deserializeNBT(registries, tag.getCompound("energy_io"));
        upgradeModuleSlot.deserializeNBT(registries, tag.getCompound("upgrade_slot"));
        upgrades = LimaNbtUtil.tryDecode(MachineUpgrades.CODEC, RegistryOps.create(NbtOps.INSTANCE, registries), tag, "upgrades", MachineUpgrades.EMPTY);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.put(KEY_ITEM_CONTAINER, inventory.serializeNBT(registries));
        if (getEnergyStorage() instanceof LimaBlockEntityEnergyStorage blockEntityEnergy) tag.put(KEY_ENERGY_CONTAINER, blockEntityEnergy.serializeNBT(registries));
        tag.put("item_io", itemControl.serializeNBT(registries));
        tag.put("energy_io", energyControl.serializeNBT(registries));
        tag.put("upgrade_slot", upgradeModuleSlot.serializeNBT(registries));
        LimaNbtUtil.tryEncodeTo(MachineUpgrades.CODEC, RegistryOps.create(NbtOps.INSTANCE, registries), upgrades, tag, "upgrades");
    }

    @Override
    protected void onLoadServer(ServerLevel level)
    {
        onUpgradeRefresh(level);
    }

    @Override
    public SidedAccessBlockEntityType<?> getType()
    {
        return (SidedAccessBlockEntityType<?>) super.getType();
    }
}