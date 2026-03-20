package liedge.ltxindustries.blockentity.template;

import com.google.common.base.Preconditions;
import com.mojang.serialization.DynamicOps;
import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.energy.LimaBlockEntityEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.capability.itemhandler.LimaItemHandlerUtil;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.util.LimaItemUtil;
import liedge.limacore.util.LimaNbtUtil;
import liedge.ltxindustries.LTXIConstants;
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
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class LTXIMachineBlockEntity extends LimaBlockEntity implements ConfigurableIOBlockEntity, UpgradesHolderBlockEntity, EnergyHolderBlockEntity
{
    // Recommended standard inventory indices for machines
    public static final int AUX_MODULE_ITEM_SLOT = 0;
    public static final int AUX_ENERGY_ITEM_SLOT = 1;

    private final ConfigurableIOBlockEntityType<?> type;
    private final Map<Direction, BlockCapabilityCache<IItemHandler, Direction>> itemConnections = new EnumMap<>(Direction.class);

    private final LimaBlockEntityItemHandler auxInventory;
    private BlockIOConfiguration itemIOConfig;

    private final LimaEnergyStorage energyStorage;
    private BlockIOConfiguration energyIOConfig;

    private MachineUpgrades upgrades = MachineUpgrades.EMPTY;

    private int autoItemOutputTimer;
    private int autoItemInputTimer;

    protected LTXIMachineBlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state, int auxInventorySize, @Nullable LimaEnergyStorage energyStorage)
    {
        super(type, pos, state);
        Preconditions.checkArgument(auxInventorySize > 1, "Machine auxiliary inventory size must have at least 2 slots.");

        this.type = type;
        this.itemIOConfig = BlockIOConfiguration.create(type, BlockEntityInputType.ITEMS);
        this.auxInventory = new LimaBlockEntityItemHandler(this, auxInventorySize, BlockContentsType.AUXILIARY);

        this.energyStorage = energyStorage != null ? energyStorage : new LimaBlockEntityEnergyStorage(this);
        this.energyIOConfig = BlockIOConfiguration.create(type, BlockEntityInputType.ENERGY);
    }

    protected LTXIMachineBlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state, @Nullable LimaEnergyStorage energyStorage)
    {
        this(type, pos, state, 2, energyStorage);
    }

    public LimaBlockEntityItemHandler getAuxInventory()
    {
        return auxInventory;
    }

    //#region Sided IO implementation

    @Override
    public Collection<BlockEntityInputType> getConfigurableInputTypes()
    {
        return type.getValidInputTypes();
    }

    @Override
    public IOConfigurationRules getIOConfigRules(BlockEntityInputType inputType)
    {
        return type.getIOConfigRules(inputType);
    }

    @Override
    public final @Nullable BlockIOConfiguration getIOConfiguration(BlockEntityInputType inputType)
    {
        return switch (inputType)
        {
            case ITEMS -> itemIOConfig;
            case ENERGY -> energyIOConfig;
            case FLUIDS -> getFluidIOConfiguration();
        };
    }

    @Override
    public boolean setIOConfiguration(BlockEntityInputType inputType, BlockIOConfiguration configuration)
    {
        if (getConfigurableInputTypes().contains(inputType) && configuration.isValidForRules(getIOConfigRules(inputType)))
        {
            boolean changed = switch (inputType)
            {
                case ITEMS -> setItemIOConfiguration(configuration);
                case ENERGY -> setEnergyIOConfiguration(configuration);
                case FLUIDS -> setFluidIOConfiguration(configuration);
            };

            if (changed)
            {
                invalidateCapabilities();
                setChanged();
            }

            return changed;
        }

        return false;
    }

    @Override
    public IOAccess getSideIOForItems(@Nullable Direction side)
    {
        return side != null ? itemIOConfig.getIOAccess(getFacing(), side) : IOAccess.DISABLED;
    }

    @Override
    public IOAccess getSideIOForEnergy(@Nullable Direction side)
    {
        return side != null ? energyIOConfig.getIOAccess(getFacing(), side) : IOAccess.DISABLED;
    }

    public IOAccess getSideIOForFluids(@Nullable Direction side)
    {
        return side != null ? getIOConfigurationOrThrow(BlockEntityInputType.FLUIDS).getIOAccess(getFacing(), side) : IOAccess.DISABLED;
    }

    protected boolean setItemIOConfiguration(BlockIOConfiguration configuration)
    {
        if (Objects.equals(this.itemIOConfig, configuration)) return false;

        this.itemIOConfig = configuration;
        return true;
    }

    protected boolean setEnergyIOConfiguration(BlockIOConfiguration configuration)
    {
        if (Objects.equals(this.energyIOConfig, configuration)) return false;

        this.energyIOConfig = configuration;
        return true;
    }

    protected @Nullable BlockIOConfiguration getFluidIOConfiguration()
    {
        return null;
    }

    protected boolean setFluidIOConfiguration(BlockIOConfiguration configuration)
    {
        return false;
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

    //#region Energy holder implementation

    @Override
    public final LimaEnergyStorage getEnergyStorage()
    {
        return energyStorage;
    }

    @Override
    public int getBaseEnergyTransferRate()
    {
        return getBaseEnergyCapacity() / 20;
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
        getEnergyStorage().setEnergyStored(componentInput.getOrDefault(LimaCoreDataComponents.ENERGY, 0));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        components.set(LTXIDataComponents.MACHINE_UPGRADES, getUpgrades());
        if (getEnergyStorage() instanceof LimaBlockEntityEnergyStorage blockEntityEnergy) blockEntityEnergy.writeComponents(components);
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag)
    {
        tag.remove(LTXIConstants.KEY_UPGRADES_CONTAINER);
        tag.remove(LimaCommonConstants.KEY_ENERGY_CONTAINER);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        DynamicOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);

        // Load upgrades
        this.upgrades = LimaNbtUtil.tryDecode(MachineUpgrades.CODEC, ops, tag, LTXIConstants.KEY_UPGRADES_CONTAINER, MachineUpgrades.EMPTY);

        // Load inventories
        loadItemContainers(tag, registries);

        // Load energy storage
        loadEnergyStorage(tag, registries);

        // Load IO configurations
        loadIOConfigurations(tag, ops);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        DynamicOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);

        // Save upgrades
        LimaNbtUtil.tryEncodeTo(MachineUpgrades.CODEC, ops, upgrades, tag, LTXIConstants.KEY_UPGRADES_CONTAINER);

        // Save inventories
        saveItemContainers(tag, registries);

        // Save energy storage
        saveEnergyStorage(tag, registries);

        // Save IO configurations
        saveIOConfigurations(tag, ops);
    }

    //#endregion

    // Misc/Helpers

    @Override
    public boolean isItemValid(BlockContentsType contentsType, int slot, ItemStack stack)
    {
        return contentsType != BlockContentsType.AUXILIARY || isItemValidForAuxInventory(slot, stack);
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

    protected void pullItemsFromSides(IItemHandler thisInventory, Predicate<ItemStack> predicate)
    {
        if (itemIOConfig.autoInput())
        {
            Direction front = getFacing();
            for (var entry : itemIOConfig)
            {
                if (entry.getValue().allowsInput())
                {
                    IItemHandler neighborItems = getNeighborItemHandler(entry.getKey().resolveAbsoluteSide(front));
                    if (neighborItems != null) LimaItemHandlerUtil.transferItemsBetween(neighborItems, thisInventory, predicate);
                }
            }
        }
    }

    protected void pushItemsToSides(IItemHandler thisInventory, Predicate<ItemStack> predicate)
    {
        if (itemIOConfig.autoOutput())
        {
            Direction front = getFacing();
            for (var entry : itemIOConfig)
            {
                if (entry.getValue().allowsOutput())
                {
                    IItemHandler neighborItems = getNeighborItemHandler(entry.getKey().resolveAbsoluteSide(front));
                    if (neighborItems != null) LimaItemHandlerUtil.transferItemsBetween(thisInventory, neighborItems, predicate);
                }
            }
        }
    }

    protected void pullEnergyFromAux()
    {
        if (energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored())
        {
            IEnergyStorage itemEnergy = auxInventory.getStackInSlot(AUX_ENERGY_ITEM_SLOT).getCapability(Capabilities.EnergyStorage.ITEM);
            if (itemEnergy != null) LimaEnergyUtil.transferEnergyBetween(itemEnergy, energyStorage, energyStorage.getTransferRate(), false);
        }
    }

    protected void pushEnergyToSides()
    {
        if (energyIOConfig.autoOutput())
        {
            Direction front = getFacing();
            for (var entry : energyIOConfig)
            {
                if (entry.getValue().allowsOutput())
                {
                    IEnergyStorage neighborEnergy = getNeighborEnergyStorage(entry.getKey().resolveAbsoluteSide(front));
                    if (neighborEnergy != null) LimaEnergyUtil.transferEnergyBetween(energyStorage, neighborEnergy, energyStorage.getTransferRate(), false);
                }
            }
        }
    }

    protected void tickItemAutoInput(int frequency, IItemHandler thisInventory)
    {
        if (autoItemInputTimer >= frequency)
        {
            pullItemsFromSides(thisInventory, LimaItemUtil.ALWAYS_TRUE);
            autoItemInputTimer = 0;
        }
        else
        {
            autoItemInputTimer++;
        }
    }

    protected void tickItemAutoOutput(int frequency, IItemHandler thisInventory, Predicate<ItemStack> predicate)
    {
        if (autoItemOutputTimer >= frequency)
        {
            pushItemsToSides(thisInventory, predicate);
            autoItemOutputTimer = 0;
        }
        else
        {
            autoItemOutputTimer++;
        }
    }

    protected void tickItemAutoOutput(int frequency, IItemHandler thisInventory)
    {
        tickItemAutoOutput(frequency, thisInventory, LimaItemUtil.ALWAYS_TRUE);
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
}