package liedge.ltxindustries.blockentity.template;

import com.google.common.base.Predicates;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.ltxindustries.blockentity.base.BlockIOConfiguration;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntity;
import liedge.ltxindustries.blockentity.base.IORuleSet;
import liedge.ltxindustries.blockentity.base.ResourceType;
import liedge.ltxindustries.util.SidedResourceUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class ConfigurableSidesBlockEntity extends UpgradableBaseBlockEntity implements ConfigurableIOBlockEntity
{
    // IO configurations
    private @Nullable BlockIOConfiguration itemsIOConfig;
    private @Nullable BlockIOConfiguration energyIOConfig;
    private @Nullable BlockIOConfiguration fluidsIOConfig;

    // Connection maps
    private Map<Direction, BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction>> itemConnections = Map.of();
    private Map<Direction, BlockCapabilityCache<EnergyHandler, @Nullable Direction>> energyConnections = Map.of();
    private Map<Direction, BlockCapabilityCache<ResourceHandler<FluidResource>, @Nullable Direction>> fluidConnections = Map.of();

    // Auto IO timers
    private int autoInputTimer;
    private int autoOutputTimer;

    protected ConfigurableSidesBlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state, int auxInvSize)
    {
        super(type, pos, state, auxInvSize);

        IORuleSet rules = getRuleSet();
        this.itemsIOConfig = BlockIOConfiguration.create(rules, ResourceType.ITEMS);
        this.energyIOConfig = BlockIOConfiguration.create(rules, ResourceType.ENERGY);
        this.fluidsIOConfig = BlockIOConfiguration.create(rules, ResourceType.FLUIDS);
    }

    @Override
    public final @Nullable BlockIOConfiguration getIOConfiguration(ResourceType inputType)
    {
        return switch (inputType)
        {
            case ITEMS -> itemsIOConfig;
            case ENERGY -> energyIOConfig;
            case FLUIDS -> fluidsIOConfig;
        };
    }

    @Override
    public final boolean setIOConfiguration(ResourceType inputType, BlockIOConfiguration configuration)
    {
        if (supportsInputType(inputType) && configuration.isValidForRules(getIOConfigRules(inputType)))
        {
            boolean changed = setIOConfigInternal(inputType, configuration);
            if (changed)
            {
                setChanged();
                invalidateCapabilities();
            }

            return changed;
        }

        return false;
    }

    @Override
    public IOAccess getTopLevelItemIO(@Nullable Direction side)
    {
        if (side == null || itemsIOConfig == null)
            return IOAccess.DISABLED;
        else
            return itemsIOConfig.getIOAccess(getFacing(), side);
    }

    public IOAccess getTopLevelEnergyIO(@Nullable Direction side)
    {
        if (side == null || energyIOConfig == null)
            return IOAccess.DISABLED;
        else
            return energyIOConfig.getIOAccess(getFacing(), side);
    }

    public IOAccess getTopLevelFluidIO(@Nullable Direction side)
    {
        if (side == null || fluidsIOConfig == null)
            return IOAccess.DISABLED;
        else
            return fluidsIOConfig.getIOAccess(getFacing(), side);
    }

    private boolean setItemsIOConfig(BlockIOConfiguration itemsIOConfig)
    {
        if (Objects.equals(this.itemsIOConfig, itemsIOConfig)) return false;

        this.itemsIOConfig = itemsIOConfig;
        return true;
    }

    private boolean setEnergyIOConfig(BlockIOConfiguration energyIOConfig)
    {
        if (Objects.equals(this.energyIOConfig, energyIOConfig)) return false;

        this.energyIOConfig = energyIOConfig;
        return true;
    }

    private boolean setFluidsIOConfig(BlockIOConfiguration fluidsIOConfig)
    {
        if (Objects.equals(this.fluidsIOConfig, fluidsIOConfig)) return false;

        this.fluidsIOConfig = fluidsIOConfig;
        return true;
    }

    //#region Cap utilities

    public @Nullable ResourceHandler<ItemResource> getNeighborItemHandler(Direction side)
    {
        BlockCapabilityCache<ResourceHandler<ItemResource>, ?> cap = itemConnections.get(side);
        return cap != null ? cap.getCapability() : null;
    }

    public @Nullable EnergyHandler getNeighborEnergyHandler(Direction side)
    {
        BlockCapabilityCache<EnergyHandler, ?> cap = energyConnections.get(side);
        return cap != null ? cap.getCapability() : null;
    }

    public @Nullable ResourceHandler<FluidResource> getNeighborFluidHandler(Direction side)
    {
        BlockCapabilityCache<ResourceHandler<FluidResource>, ?> cap = fluidConnections.get(side);
        return cap != null ? cap.getCapability() : null;
    }

    protected void tickAutoResourceInput(int frequency, @Nullable ResourceHandler<ItemResource> items, @Nullable ResourceHandler<FluidResource> fluids, Predicate<ItemResource> itemFilter, Predicate<FluidResource> fluidFilter)
    {
        if (autoInputTimer >= frequency)
        {
            SidedResourceUtil.pullResources(itemsIOConfig, getFacing(), items, Item.DEFAULT_MAX_STACK_SIZE, itemFilter, this::getNeighborItemHandler);
            SidedResourceUtil.pullResources(fluidsIOConfig, getFacing(), fluids, Integer.MAX_VALUE, fluidFilter, this::getNeighborFluidHandler);

            autoInputTimer = 0;
        }
        else
        {
            autoInputTimer++;
        }
    }

    protected void tickAutoResourceInput(int frequency, @Nullable ResourceHandler<ItemResource> items, @Nullable ResourceHandler<FluidResource> fluids)
    {
        tickAutoResourceInput(frequency, items, fluids, Predicates.alwaysTrue(), Predicates.alwaysTrue());
    }

    protected void tickAutoResourceOutput(int frequency, @Nullable ResourceHandler<ItemResource> items, @Nullable ResourceHandler<FluidResource> fluids, Predicate<ItemResource> itemFilter, Predicate<FluidResource> fluidFilter)
    {
        if (autoOutputTimer >= frequency)
        {
            SidedResourceUtil.pushResources(itemsIOConfig, getFacing(), items, Item.DEFAULT_MAX_STACK_SIZE, itemFilter, this::getNeighborItemHandler);
            SidedResourceUtil.pushResources(fluidsIOConfig, getFacing(), fluids, Integer.MAX_VALUE, fluidFilter, this::getNeighborFluidHandler);

            autoOutputTimer = 0;
        }
        else
        {
            autoOutputTimer++;
        }
    }

    protected void tickAutoResourceOutput(int frequency, @Nullable ResourceHandler<ItemResource> items, @Nullable ResourceHandler<FluidResource> fluids)
    {
        tickAutoResourceOutput(frequency, items, fluids, Predicates.alwaysTrue(), Predicates.alwaysTrue());
    }

    //#endregion

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);

        loadIOConfigurations(input, this::setIOConfigInternal);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);

        saveIOConfigurations(output);
    }

    @Override
    protected void onLoadServer(ServerLevel level)
    {
        super.onLoadServer(level);

        itemConnections = createConnections(level, Capabilities.Item.BLOCK, itemsIOConfig);
        energyConnections = createConnections(level, Capabilities.Energy.BLOCK, energyIOConfig);
        fluidConnections = createConnections(level, Capabilities.Fluid.BLOCK, fluidsIOConfig);
    }

    private boolean setIOConfigInternal(ResourceType inputType, BlockIOConfiguration configuration)
    {
        return switch (inputType)
        {
            case ITEMS -> setItemsIOConfig(configuration);
            case ENERGY -> setEnergyIOConfig(configuration);
            case FLUIDS -> setFluidsIOConfig(configuration);
        };
    }

    private <T> Map<Direction, BlockCapabilityCache<T, @Nullable Direction>> createConnections(ServerLevel level, BlockCapability<T, @Nullable Direction> capability, @Nullable BlockIOConfiguration configuration)
    {
        if (configuration == null) return Map.of();

        Map<Direction, BlockCapabilityCache<T, @Nullable Direction>> connections = new EnumMap<>(Direction.class);
        for (Direction side : Direction.values())
        {
            connections.put(side, createCapabilityCache(capability, level, side));
        }

        return connections;
    }
}