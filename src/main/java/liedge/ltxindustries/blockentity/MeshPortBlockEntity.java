package liedge.ltxindustries.blockentity;

import com.google.common.base.Predicates;
import liedge.limacore.LimaCoreCapabilities;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.RelativeHorizontalSide;
import liedge.ltxindustries.block.mesh.MeshPosition;
import liedge.ltxindustries.blockentity.base.BlockIOConfiguration;
import liedge.ltxindustries.blockentity.base.IORules;
import liedge.ltxindustries.blockentity.base.ResourceType;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.util.SidedResourceUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
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

public final class MeshPortBlockEntity extends MeshBlockEntity
{
    // Primary connections
    private @Nullable BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable IOAccess> primaryItems;
    private @Nullable BlockCapabilityCache<EnergyHandler, @Nullable IOAccess> primaryEnergy;
    private @Nullable BlockCapabilityCache<ResourceHandler<FluidResource>, @Nullable IOAccess> primaryFluids;

    // IO configs
    private @Nullable BlockIOConfiguration itemsIOConfig;
    private @Nullable BlockIOConfiguration energyIOConfig;
    private @Nullable BlockIOConfiguration fluidsIOConfig;

    // Neighbor Connections
    private Map<Direction, BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction>> itemConnections = Map.of();
    private Map<Direction, BlockCapabilityCache<EnergyHandler, @Nullable Direction>> energyConnections = Map.of();
    private Map<Direction, BlockCapabilityCache<ResourceHandler<FluidResource>, @Nullable Direction>> fluidConnections = Map.of();

    private int autoOutputTimer;

    public MeshPortBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.PORT_MESH_BLOCK.get(), pos, state);
    }

    @Override
    public @Nullable ResourceHandler<ItemResource> getPrimaryItems(@Nullable Direction side)
    {
        if (side == null || primaryItems == null)
            return super.getPrimaryItems(side);
        else
            return primaryItems.getCapability();
    }

    @Override
    public @Nullable EnergyHandler getPrimaryEnergy(@Nullable Direction side)
    {
        if (side == null || primaryEnergy == null)
            return super.getPrimaryEnergy(side);
        else
            return primaryEnergy.getCapability();
    }

    @Override
    public @Nullable ResourceHandler<FluidResource> getPrimaryFluids(@Nullable Direction side)
    {
        if (side == null || primaryFluids == null)
            return super.getPrimaryFluids(side);
        else
            return primaryFluids.getCapability();
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        Direction facing = getFacing();

        SidedResourceUtil.pushEnergy(energyIOConfig, facing, primaryEnergy != null ? primaryEnergy.getCapability() : null, Integer.MAX_VALUE, this::getNeighborEnergyHandler);

        if (autoOutputTimer >= 100)
        {
            SidedResourceUtil.pushResources(itemsIOConfig, facing, primaryItems != null ? primaryItems.getCapability() : null, Item.DEFAULT_MAX_STACK_SIZE, Predicates.alwaysTrue(), this::getNeighborItemHandler);
            SidedResourceUtil.pushResources(fluidsIOConfig, facing, primaryFluids != null ? primaryFluids.getCapability() : null, Integer.MAX_VALUE, Predicates.alwaysTrue(), this::getNeighborFluidHandler);

            autoOutputTimer = 0;
        }
        else
        {
            autoOutputTimer++;
        }
    }

    @Override
    protected void onLoadServer(ServerLevel level)
    {
        MeshPosition mp = getMeshPos();
        BlockPos primaryPos = getPrimaryPos(getBlockPos(), getBlockState());

        if (mp != null && primaryPos != null)
        {
            IORules itemRules = mp.getRules(ResourceType.ITEMS);
            if (itemRules != null)
            {
                this.primaryItems = createPrimaryConnection(level, LimaCoreCapabilities.IO_ITEMS, itemRules.getDefaultAccess(), primaryPos);
                this.itemConnections = createConnections(level, Capabilities.Item.BLOCK, itemRules);
                this.itemsIOConfig = BlockIOConfiguration.create(itemRules);
            }

            IORules energyRules = mp.getRules(ResourceType.ENERGY);
            if (energyRules != null)
            {
                this.primaryEnergy = createPrimaryConnection(level, LimaCoreCapabilities.IO_ENERGY, energyRules.getDefaultAccess(), primaryPos);
                this.energyConnections = createConnections(level, Capabilities.Energy.BLOCK, energyRules);
                this.energyIOConfig = BlockIOConfiguration.create(energyRules);
            }

            IORules fluidRules = mp.getRules(ResourceType.FLUIDS);
            if (fluidRules != null)
            {
                this.primaryFluids = createPrimaryConnection(level, LimaCoreCapabilities.IO_FLUIDS, fluidRules.getDefaultAccess(), primaryPos);
                this.fluidConnections = createConnections(level, Capabilities.Fluid.BLOCK, fluidRules);
                this.fluidsIOConfig = BlockIOConfiguration.create(fluidRules);
            }
        }
    }

    private @Nullable ResourceHandler<ItemResource> getNeighborItemHandler(Direction side)
    {
        BlockCapabilityCache<ResourceHandler<ItemResource>, ?> cap = itemConnections.get(side);
        return cap != null ? cap.getCapability() : null;
    }

    private @Nullable EnergyHandler getNeighborEnergyHandler(Direction side)
    {
        BlockCapabilityCache<EnergyHandler, ?> cap = energyConnections.get(side);
        return cap != null ? cap.getCapability() : null;
    }

    private @Nullable ResourceHandler<FluidResource> getNeighborFluidHandler(Direction side)
    {
        BlockCapabilityCache<ResourceHandler<FluidResource>, ?> cap = fluidConnections.get(side);
        return cap != null ? cap.getCapability() : null;
    }

    private <T> BlockCapabilityCache<T, @Nullable IOAccess> createPrimaryConnection(ServerLevel level, BlockCapability<T, @Nullable IOAccess> capability, IOAccess access, BlockPos pos)
    {
        return BlockCapabilityCache.create(capability, level, pos, access, () -> !isRemoved(), this::invalidateCapabilities);
    }

    private <T> Map<Direction, BlockCapabilityCache<T, @Nullable Direction>> createConnections(ServerLevel level, BlockCapability<T, @Nullable Direction> capability, IORules rules)
    {
        Map<Direction, BlockCapabilityCache<T, @Nullable Direction>> map = new EnumMap<>(Direction.class);
        Direction facing = getFacing();

        for (RelativeHorizontalSide side : rules.getSides())
        {
            Direction absSide = side.resolveAbsoluteSide(facing);
            map.put(absSide, createCapabilityCache(capability, level, absSide));
        }

        return map;
    }
}