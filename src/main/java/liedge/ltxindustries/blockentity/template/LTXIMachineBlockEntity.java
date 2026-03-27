package liedge.ltxindustries.blockentity.template;

import com.google.common.base.Preconditions;
import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.transfer.LimaTransferUtil;
import liedge.limacore.transfer.energy.AdjustableBlockEntityEnergy;
import liedge.limacore.transfer.energy.EnergyHolderBlockEntity;
import liedge.limacore.transfer.energy.VariableEnergyHandler;
import liedge.limacore.transfer.item.LimaBlockEntityItems;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.base.*;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class LTXIMachineBlockEntity extends LimaBlockEntity implements ConfigurableIOBlockEntity, UpgradesHolderBlockEntity, EnergyHolderBlockEntity
{
    // Recommended standard inventory indices for machines
    public static final int AUX_MODULE_ITEM_SLOT = 0;
    public static final int AUX_ENERGY_ITEM_SLOT = 1;

    private final ConfigurableIOBlockEntityType<?> type;
    private final Map<Direction, BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction>> itemConnections = new EnumMap<>(Direction.class);

    protected final LimaBlockEntityItems auxInventory;
    private BlockIOConfiguration itemIOConfig;

    private final VariableEnergyHandler energy;
    private BlockIOConfiguration energyIOConfig;

    private MachineUpgrades upgrades = MachineUpgrades.EMPTY;

    private int autoItemOutputTimer;
    private int autoItemInputTimer;

    protected LTXIMachineBlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state, int auxInventorySize, @Nullable VariableEnergyHandler energy)
    {
        super(type, pos, state);
        Preconditions.checkArgument(auxInventorySize > 1, "Machine auxiliary inventory size must have at least 2 slots.");

        this.type = type;
        this.itemIOConfig = BlockIOConfiguration.create(type, BlockEntityInputType.ITEMS);
        this.auxInventory = new LimaBlockEntityItems(this, BlockContentsType.AUXILIARY, auxInventorySize);

        this.energy = energy != null ? energy : new AdjustableBlockEntityEnergy(this);
        this.energyIOConfig = BlockIOConfiguration.create(type, BlockEntityInputType.ENERGY);
    }

    protected LTXIMachineBlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state, @Nullable VariableEnergyHandler energyStorage)
    {
        this(type, pos, state, 2, energyStorage);
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

    public @Nullable ResourceHandler<ItemResource> getNeighborItemHandler(Direction side)
    {
        return itemConnections.get(side).getCapability();
    }

    public @Nullable EnergyHandler getNeighborEnergyStorage(Direction side)
    {
        return null;
    }

    public @Nullable ResourceHandler<FluidResource> getNeighborFluidHandler(Direction side)
    {
        return null;
    }

    protected void createConnectionCaches(ServerLevel level, Direction side)
    {
        itemConnections.put(side, createCapabilityCache(Capabilities.Item.BLOCK, level, side));
    }

    //#endregion

    //#region Item holder implementation

    @Override
    public boolean isItemValid(BlockContentsType contentsType, int index, ItemResource resource)
    {
        return contentsType != BlockContentsType.AUXILIARY || isItemValidForAuxInventory(index, resource);
    }

    protected boolean isItemValidForAuxInventory(int index, ItemResource resource)
    {
        return switch (index)
        {
            case AUX_MODULE_ITEM_SLOT -> resource.is(LTXIItems.MACHINE_UPGRADE_MODULE.asItem());
            case AUX_ENERGY_ITEM_SLOT -> LimaItemUtil.hasEnergyCapability(ItemAccess.forStack(resource.toStack()));
            default -> false;
        };
    }

    @Override
    public IOAccess getTopLevelItemIO(@Nullable Direction side)
    {
        return side != null ? itemIOConfig.getIOAccess(getFacing(), side) : IOAccess.DISABLED;
    }

    //#endregion

    //#region Energy holder implementation

    @Override
    public final VariableEnergyHandler getEnergy()
    {
        return energy;
    }

    @Override
    public int getBaseEnergyTransferRate()
    {
        return getBaseEnergyCapacity() / 20;
    }

    @Override
    public IOAccess getTopLevelEnergyIO(@Nullable Direction side)
    {
        return side != null ? energyIOConfig.getIOAccess(getFacing(), side) : IOAccess.DISABLED;
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
    protected void applyImplicitComponents(DataComponentGetter getter)
    {
        setUpgrades(getter.getOrDefault(LTXIDataComponents.MACHINE_UPGRADES, MachineUpgrades.EMPTY));
        if (energy instanceof AdjustableBlockEntityEnergy adjustable) adjustable.set(getter.getOrDefault(LimaCoreDataComponents.ENERGY, 0));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        components.set(LTXIDataComponents.MACHINE_UPGRADES, getUpgrades());
        if (energy instanceof AdjustableBlockEntityEnergy adjustable) adjustable.writeComponents(components);
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output)
    {
        output.discard(LTXIConstants.KEY_UPGRADES_CONTAINER);
        output.discard(LimaCommonConstants.KEY_ENERGY_CONTAINER);
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);

        this.upgrades = input.read(LTXIConstants.KEY_UPGRADES_CONTAINER, MachineUpgrades.CODEC).orElse(MachineUpgrades.EMPTY);
        loadItemResources(input);
        loadEnergyStorage(input);
        loadIOConfigurations(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);

        output.store(LTXIConstants.KEY_UPGRADES_CONTAINER, MachineUpgrades.CODEC, upgrades);
        saveItemResources(output);
        saveEnergyStorage(output);
        saveIOConfigurations(output);
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

    //#endregion

    // Misc/Helpers
    protected <T extends Resource> void pullResourcesFromSides(BlockIOConfiguration configuration, @Nullable ResourceHandler<T> thisInventory, int amount, Predicate<T> filter, Function<Direction, @Nullable ResourceHandler<T>> neighborAccess)
    {
        if (configuration.autoInput())
        {
            Direction front = getFacing();
            for (var entry : configuration)
            {
                if (entry.getValue().allowsInput())
                {
                    ResourceHandler<T> neighbor = neighborAccess.apply(entry.getKey().resolveAbsoluteSide(front));
                    ResourceHandlerUtil.moveStacking(neighbor, thisInventory, filter, amount, null);
                }
            }
        }
    }

    protected <T extends Resource> void pushResourcesToSides(BlockIOConfiguration configuration, @Nullable ResourceHandler<T> thisInventory, int amount, Predicate<T> filter, Function<Direction, @Nullable ResourceHandler<T>> neighborAccess)
    {
        if (configuration.autoOutput())
        {
            Direction front = getFacing();
            for (var entry : configuration)
            {
                if (entry.getValue().allowsOutput())
                {
                    ResourceHandler<T> neighbor = neighborAccess.apply(entry.getKey().resolveAbsoluteSide(front));
                    ResourceHandlerUtil.moveStacking(thisInventory, neighbor, filter, amount, null);
                }
            }
        }
    }

    protected void pullEnergyFromAux()
    {
        if (!EnergyHandlerUtil.isFull(energy))
        {
            ItemAccess access = ItemAccess.forHandlerIndexStrict(auxInventory, AUX_ENERGY_ITEM_SLOT);
            EnergyHandler itemEnergy = access.getCapability(Capabilities.Energy.ITEM);

            if (itemEnergy != null) EnergyHandlerUtil.move(itemEnergy, energy, energy.getTransferRate(), null);
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
                    EnergyHandler neighborEnergy = getNeighborEnergyStorage(entry.getKey().resolveAbsoluteSide(front));
                    if (neighborEnergy != null) EnergyHandlerUtil.move(energy, neighborEnergy, energy.getTransferRate(), null);
                }
            }
        }
    }

    protected void tickItemAutoInput(int frequency, @Nullable ResourceHandler<ItemResource> thisInventory)
    {
        if (autoItemInputTimer >= frequency)
        {
            pullResourcesFromSides(itemIOConfig, thisInventory, Item.DEFAULT_MAX_STACK_SIZE, LimaTransferUtil.ALL_ITEMS, this::getNeighborItemHandler);
            autoItemInputTimer = 0;
        }
        else
        {
            autoItemInputTimer++;
        }
    }

    protected void tickItemAutoOutput(int frequency, @Nullable ResourceHandler<ItemResource> thisInventory, Predicate<ItemResource> filter)
    {
        if (autoItemOutputTimer >= frequency)
        {
            pushResourcesToSides(itemIOConfig, thisInventory, Item.DEFAULT_MAX_STACK_SIZE, filter, this::getNeighborItemHandler);
            autoItemOutputTimer = 0;
        }
        else
        {
            autoItemOutputTimer++;
        }
    }

    protected void tickItemAutoOutput(int frequency, @Nullable ResourceHandler<ItemResource> thisInventory)
    {
        tickItemAutoOutput(frequency, thisInventory, LimaTransferUtil.ALL_ITEMS);
    }
}