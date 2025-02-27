package liedge.limatech.blockentity;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.energy.LimaBlockEntityEnergyStorage;
import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import liedge.limacore.capability.itemhandler.StandaloneBlockEntityItemHandler;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.inventory.menu.LimaMenuProvider;
import liedge.limacore.util.LimaNbtUtil;
import liedge.limatech.blockentity.io.MachineIOControl;
import liedge.limatech.blockentity.io.MachineInputType;
import liedge.limatech.blockentity.io.SidedMachineIOHolder;
import liedge.limatech.lib.upgrades.machine.MachineUpgrades;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static liedge.limacore.LimaCommonConstants.KEY_ENERGY_CONTAINER;
import static liedge.limacore.LimaCommonConstants.KEY_ITEM_CONTAINER;
import static liedge.limacore.registry.LimaCoreDataComponents.ENERGY;
import static liedge.limacore.registry.LimaCoreDataComponents.ITEM_CONTAINER;
import static liedge.limatech.registry.LimaTechDataComponents.MACHINE_UPGRADES;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public abstract class SidedItemEnergyMachineBlockEntity extends LimaBlockEntity implements LimaMenuProvider, EnergyHolderBlockEntity, ItemHolderBlockEntity, UpgradableMachineBlockEntity, SidedMachineIOHolder
{
    private final LimaBlockEntityItemHandler inventory;
    private final MachineIOControl energyControl;
    private final MachineIOControl itemControl;
    private final StandaloneBlockEntityItemHandler upgradeModuleSlot = new StandaloneBlockEntityItemHandler(this, 1);
    private MachineUpgrades upgrades = MachineUpgrades.EMPTY;

    protected SidedItemEnergyMachineBlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize)
    {
        super(type, pos, state);
        this.inventory = new LimaBlockEntityItemHandler(this, inventorySize);
        Direction front = state.getValue(HORIZONTAL_FACING);
        this.energyControl = initEnergyIOControl(front);
        this.itemControl = initItemIOControl(front);
    }

    // IO control methods/initializers
    protected abstract MachineIOControl initItemIOControl(Direction front);

    protected abstract MachineIOControl initEnergyIOControl(Direction front);

    @Override
    public void onBlockStateUpdated(BlockPos pos, BlockState oldState, BlockState newState)
    {
        if (checkServerSide())
        {
            Direction oldFront = oldState.getValue(HORIZONTAL_FACING);
            Direction newFront = newState.getValue(HORIZONTAL_FACING);

            if (oldFront != newFront) updateFacingForAllIO(newFront);
        }
    }

    @Override
    public @Nullable MachineIOControl getIOControls(MachineInputType inputType)
    {
        return switch (inputType)
        {
            case ITEMS -> itemControl;
            case ENERGY -> energyControl;
            case FLUIDS -> null;
        };
    }

    @Override
    public void onIOControlsChanged(MachineInputType inputType)
    {
        if (level != null && !level.isClientSide())
        {
            invalidateCapabilities();
            setChanged();
            onIOControlsChangedInternal(inputType, level);
        }
    }

    public MachineIOControl getItemControl()
    {
        return itemControl;
    }

    public MachineIOControl getEnergyControl()
    {
        return energyControl;
    }

    protected void onIOControlsChangedInternal(MachineInputType inputType, Level level) {}

    // Item handler methods
    @Override
    public LimaItemHandlerBase getItemHandler()
    {
        return inventory;
    }

    @Override
    public IOAccess getItemIOForSide(Direction side)
    {
        return itemControl.getSideIO(side);
    }

    @Override
    public void onItemSlotChanged(int slot)
    {
        setChanged();
    }

    // Upgrades methods
    @Override
    public LimaItemHandlerBase getUpgradeModuleInventory()
    {
        return upgradeModuleSlot;
    }

    @Override
    public MachineUpgrades getUpgrades()
    {
        return upgrades;
    }

    @Override
    public void setUpgrades(MachineUpgrades upgrades)
    {
        this.upgrades = upgrades;
        setChanged();
        reloadUpgrades();
    }

    // Energy methods
    @Override
    public void onEnergyChanged()
    {
        setChanged();
    }

    @Override
    public IOAccess getEnergyIOForSide(Direction side)
    {
        return energyControl.getSideIO(side);
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
        upgrades = LimaNbtUtil.lenientDecode(MachineUpgrades.CODEC, RegistryOps.create(NbtOps.INSTANCE, registries), tag, "upgrades");
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
        tag.put("upgrades", LimaCoreCodecs.strictEncode(MachineUpgrades.CODEC, RegistryOps.create(NbtOps.INSTANCE, registries), upgrades));
    }

    @Override
    protected void onLoadServer(ServerLevel level)
    {
        Direction front = getBlockState().getValue(HORIZONTAL_FACING);
        energyControl.setFacing(front);
        itemControl.setFacing(front);

        reloadUpgrades();
    }
}