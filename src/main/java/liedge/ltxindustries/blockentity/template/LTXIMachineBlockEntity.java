package liedge.ltxindustries.blockentity.template;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.capability.itemhandler.BlockInventoryType;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.capability.itemhandler.LimaItemHandlerUtil;
import liedge.limacore.util.LimaItemUtil;
import liedge.limacore.util.LimaNbtUtil;
import liedge.ltxindustries.blockentity.base.*;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;

public sealed abstract class LTXIMachineBlockEntity extends LimaBlockEntity implements SidedAccessBlockEntity, UpgradesHolderBlockEntity permits EnergyMachineBlockEntity
{
    // Recommended standard inventory indices for machines
    public static final int AUX_MODULE_ITEM_SLOT = 0;
    public static final int AUX_ENERGY_ITEM_SLOT = 1;

    // Commonly used keys. Not required but recommended for unified serialization
    protected static final String KEY_ITEM_IO = "item_io";
    protected static final String KEY_ENERGY_IO = "energy_io";
    protected static final String KEY_FLUID_IO = "fluid_io";

    private final IOController itemController;
    private final Map<Direction, BlockCapabilityCache<IItemHandler, Direction>> itemConnections = new EnumMap<>(Direction.class);

    private final LimaBlockEntityItemHandler auxInventory;
    private MachineUpgrades upgrades = MachineUpgrades.EMPTY;

    private int autoItemOutputTimer;

    protected LTXIMachineBlockEntity(SidedAccessBlockEntityType<?> type, BlockPos pos, BlockState state, int auxInventorySize)
    {
        super(type, pos, state);

        this.itemController = new IOController(this, BlockEntityInputType.ITEMS);
        this.auxInventory = new LimaBlockEntityItemHandler(this, auxInventorySize, BlockInventoryType.AUXILIARY);
    }

    public LimaBlockEntityItemHandler getAuxInventory()
    {
        return auxInventory;
    }

    //#region Sided IO implementation

    @Override
    public final Direction getFacing()
    {
        return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public final IOController getIOController(BlockEntityInputType inputType) throws UnsupportedOperationException
    {
        return switch (inputType)
        {
            case ITEMS -> getItemIOController();
            case ENERGY -> getEnergyIOController();
            case FLUIDS -> getFluidIOController();
        };
    }

    @Override
    public void onIOControlsChanged(BlockEntityInputType inputType)
    {
        if (level != null && !level.isClientSide())
        {
            invalidateCapabilities();
            setChanged();
            onIOControlsChanged(inputType, level);
        }
    }

    protected void onIOControlsChanged(BlockEntityInputType inputType, Level level) {}

    @Override
    public void onBlockStateUpdated(BlockPos pos, BlockState oldState, BlockState newState)
    {
        if (level != null && !level.isClientSide())
        {
            invalidateCapabilities();
            setChanged();
            onIOControlsChanged(BlockEntityInputType.ITEMS, level);
            onIOControlsChanged(BlockEntityInputType.ENERGY, level);
            onIOControlsChanged(BlockEntityInputType.FLUIDS, level);
        }
    }

    @Override
    public IOAccess getSideIOForItems(@Nullable Direction side)
    {
        return side != null ? getItemIOController().getSideIOState(side) : IOAccess.DISABLED;
    }

    public IOAccess getSideIOForEnergy(@Nullable Direction side)
    {
        return side != null ? getEnergyIOController().getSideIOState(side) : IOAccess.DISABLED;
    }

    public IOAccess getSideIOForFluids(@Nullable Direction side)
    {
        return side != null ? getFluidIOController().getSideIOState(side) : IOAccess.DISABLED;
    }

    protected IOController getItemIOController()
    {
        return itemController;
    }

    protected IOController getEnergyIOController()
    {
        throw new UnsupportedOperationException("Machine does not support energy handling.");
    }

    protected IOController getFluidIOController()
    {
        throw new UnsupportedOperationException("Machine does not support fluid handling.");
    }

    public @Nullable IItemHandler getNeighborItemHandler(Direction side)
    {
        return itemConnections.get(side).getCapability();
    }

    public @Nullable IEnergyStorage getNeighborEnergyStorage(Direction side)
    {
        return null;
    }

    public @Nullable IFluidHandler getNeighborFluidHandler(Direction side)
    {
        return null;
    }

    protected void createConnectionCaches(ServerLevel level, Direction side)
    {
        itemConnections.put(side, createCapabilityCache(Capabilities.ItemHandler.BLOCK, level, side));
    }

    //#endregion

    //#region Upgrades holder implementation

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

    //#endregion

    //#region Serialization

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput)
    {
        setUpgrades(componentInput.getOrDefault(LTXIDataComponents.MACHINE_UPGRADES, MachineUpgrades.EMPTY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        components.set(LTXIDataComponents.MACHINE_UPGRADES, getUpgrades());
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag)
    {
        tag.remove(TAG_KEY_UPGRADES);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);

        CompoundTag inventoriesTag = tag.getCompound(LimaCommonConstants.KEY_ITEM_CONTAINER);
        for (BlockInventoryType type : BlockInventoryType.values())
        {
            LimaBlockEntityItemHandler handler = getItemHandler(type);
            if (handler != null && inventoriesTag.contains(type.getSerializedName())) handler.deserializeNBT(registries, inventoriesTag.getCompound(type.getSerializedName()));
        }

        itemController.deserializeNBT(registries, tag.getCompound(KEY_ITEM_IO));
        upgrades = LimaNbtUtil.tryDecode(MachineUpgrades.CODEC, RegistryOps.create(NbtOps.INSTANCE, registries), tag, TAG_KEY_UPGRADES, MachineUpgrades.EMPTY);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);

        CompoundTag inventoriesTag = new CompoundTag();
        for (BlockInventoryType type : BlockInventoryType.values())
        {
            LimaBlockEntityItemHandler handler = getItemHandler(type);
            if (handler != null) inventoriesTag.put(type.getSerializedName(), handler.serializeNBT(registries));
        }
        tag.put(LimaCommonConstants.KEY_ITEM_CONTAINER, inventoriesTag);

        tag.put(KEY_ITEM_IO, itemController.serializeNBT(registries));
        LimaNbtUtil.tryEncodeTo(MachineUpgrades.CODEC, RegistryOps.create(NbtOps.INSTANCE, registries), upgrades, tag, TAG_KEY_UPGRADES);
    }

    //#endregion

    // Misc/Helpers

    @Override
    public boolean isItemValid(BlockInventoryType inventoryType, int slot, ItemStack stack)
    {
        return inventoryType != BlockInventoryType.AUXILIARY || isItemValidForAuxInventory(slot, stack);
    }

    protected boolean isItemValidForAuxInventory(int slot, ItemStack stack)
    {
        return switch (slot)
        {
            case AUX_MODULE_ITEM_SLOT -> stack.is(LTXIItems.MACHINE_UPGRADE_MODULE);
            case AUX_ENERGY_ITEM_SLOT -> LimaItemUtil.hasEnergyCapability(stack);
            default -> false;
        };
    }

    protected void autoOutputItems(IItemHandler internalInventory, Predicate<ItemStack> predicate)
    {
        if (itemController.isAutoOutput())
        {
            for (Direction side : Direction.values())
            {
                if (itemController.getSideIOState(side).allowsOutput())
                {
                    IItemHandler neighborInv = getNeighborItemHandler(side);
                    if (neighborInv != null) LimaItemHandlerUtil.transferItemsBetween(internalInventory, neighborInv, predicate);
                }
            }
        }
    }

    protected void autoOutputItems(int frequency, IItemHandler internalInventory, Predicate<ItemStack> predicate)
    {
        if (autoItemOutputTimer >= frequency)
        {
            autoOutputItems(internalInventory, predicate);
            autoItemOutputTimer = 0;
        }
        else
        {
            autoItemOutputTimer++;
        }
    }

    protected void autoOutputItems(int frequency, IItemHandler internalInventory)
    {
        autoOutputItems(frequency, internalInventory, LimaItemUtil.ALWAYS_TRUE);
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