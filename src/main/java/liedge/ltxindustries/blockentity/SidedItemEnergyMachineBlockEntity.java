package liedge.ltxindustries.blockentity;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.energy.LimaBlockEntityEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import liedge.limacore.capability.itemhandler.LimaItemHandlerUtil;
import liedge.limacore.inventory.menu.LimaMenuProvider;
import liedge.limacore.util.LimaItemUtil;
import liedge.limacore.util.LimaNbtUtil;
import liedge.ltxindustries.blockentity.base.*;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

import static liedge.limacore.LimaCommonConstants.KEY_ENERGY_CONTAINER;
import static liedge.limacore.LimaCommonConstants.KEY_ITEM_CONTAINER;
import static liedge.limacore.registry.game.LimaCoreDataComponents.*;
import static liedge.ltxindustries.registry.game.LTXIDataComponents.MACHINE_UPGRADES;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public abstract class SidedItemEnergyMachineBlockEntity extends LimaBlockEntity implements LimaMenuProvider, EnergyHolderBlockEntity, UpgradableMachineBlockEntity, SidedAccessBlockEntity
{
    // Standardize energy item slot
    public static final int ENERGY_ITEM_SLOT = 0;

    private final LimaBlockEntityItemHandler inventory;
    private final LimaEnergyStorage energyStorage;

    private final IOController itemControl;
    private final IOController energyControl;
    private final Map<Direction, BlockCapabilityCache<IItemHandler, Direction>> itemConnections = new EnumMap<>(Direction.class);

    private final LimaBlockEntityItemHandler upgradeModuleSlot;
    private MachineUpgrades upgrades = MachineUpgrades.EMPTY;

    private int itemAutoOutputTimer = 0;

    protected SidedItemEnergyMachineBlockEntity(SidedAccessBlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize, @Nullable LimaEnergyStorage energyStorage)
    {
        super(type, pos, state);
        this.inventory = new LimaBlockEntityItemHandler(this, inventorySize);
        this.energyStorage = energyStorage != null ? energyStorage : new LimaBlockEntityEnergyStorage(this);
        this.upgradeModuleSlot = new LimaBlockEntityItemHandler(this, 1, 1);
        this.energyControl = new IOController(this, BlockEntityInputType.ENERGY);
        this.itemControl = new IOController(this, BlockEntityInputType.ITEMS);
    }

    protected SidedItemEnergyMachineBlockEntity(SidedAccessBlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize)
    {
        this(type, pos, state, inventorySize, null);
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

    public @Nullable IItemHandler getAdjacentItemHandler(Direction side)
    {
        return itemConnections.get(side).getCapability();
    }

    public @Nullable IEnergyStorage getAdjacentEnergyStorage(Direction side)
    {
        return null;
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

    protected boolean isItemValidForPrimaryHandler(int slot, ItemStack stack)
    {
        return slot != ENERGY_ITEM_SLOT || LimaItemUtil.hasEnergyCapability(stack);
    }

    @Override
    public final boolean isItemValid(int handlerIndex, int slot, ItemStack stack)
    {
        return handlerIndex != 0 || isItemValidForPrimaryHandler(slot, stack);
    }

    public abstract IOAccess getPrimaryHandlerItemSlotIO(int slot);

    @Override
    public final IOAccess getItemSlotIO(int handlerIndex, int slot)
    {
        return handlerIndex == 0 ? getPrimaryHandlerItemSlotIO(slot) : IOAccess.DISABLED;
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
            onUpgradeRefresh(createUpgradeContext(serverLevel), upgrades);
        }
    }

    // Energy methods
    @Override
    public final LimaEnergyStorage getEnergyStorage()
    {
        return energyStorage;
    }

    @Override
    public IOAccess getEnergyIOForSide(Direction side)
    {
        return energyControl.getSideIOState(side);
    }

    @Override
    public int getBaseEnergyTransferRate() // Unless otherwise needed, default to 1/20th of the base capacity
    {
        return getBaseEnergyCapacity() / 20;
    }

    //#region Tick Helper methods
    protected void fillEnergyBuffer()
    {
        LimaEnergyStorage energy = getEnergyStorage();
        if (energy.getEnergyStored() < energy.getMaxEnergyStored())
        {
            IEnergyStorage itemEnergy = inventory.getStackInSlot(ENERGY_ITEM_SLOT).getCapability(Capabilities.EnergyStorage.ITEM);
            if (itemEnergy != null) LimaEnergyUtil.transferEnergyBetween(itemEnergy, energy, energy.getTransferRate(), false);
        }
    }

    protected void autoOutputItems(int frequency, int outputSlotStart, int outputSlotCount)
    {
        if (itemControl.isAutoOutput())
        {
            if (itemAutoOutputTimer >= frequency)
            {
                for (Direction side : Direction.values())
                {
                    if (itemControl.getSideIOState(side).allowsOutput())
                    {
                        IItemHandler adjacentInventory = getAdjacentItemHandler(side);
                        if (adjacentInventory != null)
                        {
                            int outputSlotEnd = outputSlotStart + outputSlotCount;
                            LimaItemHandlerUtil.transferBetweenInventories(this.inventory, adjacentInventory, outputSlotStart, outputSlotEnd);
                        }
                    }
                }

                itemAutoOutputTimer = 0;
            }
            else
            {
                itemAutoOutputTimer++;
            }
        }
    }

    protected void autoOutputEnergy()
    {
        if (energyControl.isAutoOutput())
        {
            for (Direction side : Direction.values())
            {
                if (energyControl.getSideIOState(side).allowsOutput())
                {
                    LimaEnergyStorage thisEnergy = getEnergyStorage();
                    IEnergyStorage adjacentEnergy = getAdjacentEnergyStorage(side);
                    if (adjacentEnergy != null) LimaEnergyUtil.transferEnergyBetween(thisEnergy, adjacentEnergy, thisEnergy.getTransferRate(), false);
                }
            }
        }
    }
    //#endregion

    // Load/Save methods
    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput)
    {
        if (getEnergyStorage() instanceof LimaBlockEntityEnergyStorage) getEnergyStorage().setEnergyStored(componentInput.getOrDefault(ENERGY, 0));
        inventory.copyFromComponent(componentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
        setUpgrades(componentInput.getOrDefault(MACHINE_UPGRADES, MachineUpgrades.EMPTY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        if (getEnergyStorage() instanceof LimaBlockEntityEnergyStorage limaEnergy) limaEnergy.writeComponents(components);

        components.set(DataComponents.CONTAINER, inventory.copyToComponent());
        components.set(MACHINE_UPGRADES, upgrades);
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag)
    {
        tag.remove(KEY_ENERGY_CONTAINER);
        tag.remove(KEY_ITEM_CONTAINER);
        tag.remove(TAG_KEY_UPGRADES);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound(KEY_ITEM_CONTAINER));
        if (getEnergyStorage() instanceof LimaBlockEntityEnergyStorage blockEntityEnergy) LimaNbtUtil.deserializeInt(blockEntityEnergy, registries, tag.get(KEY_ENERGY_CONTAINER));
        itemControl.deserializeNBT(registries, tag.getCompound("item_io"));
        energyControl.deserializeNBT(registries, tag.getCompound("energy_io"));
        upgradeModuleSlot.deserializeNBT(registries, tag.getCompound(TAG_KEY_UPGRADE_SLOT_INVENTORY));
        upgrades = LimaNbtUtil.tryDecode(MachineUpgrades.CODEC, RegistryOps.create(NbtOps.INSTANCE, registries), tag, TAG_KEY_UPGRADES, MachineUpgrades.EMPTY);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.put(KEY_ITEM_CONTAINER, inventory.serializeNBT(registries));
        if (getEnergyStorage() instanceof LimaBlockEntityEnergyStorage blockEntityEnergy) tag.put(KEY_ENERGY_CONTAINER, blockEntityEnergy.serializeNBT(registries));
        tag.put("item_io", itemControl.serializeNBT(registries));
        tag.put("energy_io", energyControl.serializeNBT(registries));
        tag.put(TAG_KEY_UPGRADE_SLOT_INVENTORY, upgradeModuleSlot.serializeNBT(registries));
        LimaNbtUtil.tryEncodeTo(MachineUpgrades.CODEC, RegistryOps.create(NbtOps.INSTANCE, registries), upgrades, tag, TAG_KEY_UPGRADES);
    }

    protected void createConnectionCaches(ServerLevel level, Direction side)
    {
        itemConnections.put(side, createCapabilityCache(Capabilities.ItemHandler.BLOCK, level, side));
    }

    @Override
    protected void onLoadServer(ServerLevel level)
    {
        onUpgradeRefresh(createUpgradeContext(level), getUpgrades());

        for (Direction side : Direction.values())
        {
            createConnectionCaches(level, side);
        }
    }

    @Override
    public SidedAccessBlockEntityType<?> getType()
    {
        return (SidedAccessBlockEntityType<?>) super.getType();
    }
}