package liedge.ltxindustries.blockentity;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.menu.BlockEntityMenuType;
import liedge.limacore.transfer.energy.VariableEnergyHandler;
import liedge.limacore.transfer.item.LimaBlockEntityItems;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import liedge.ltxindustries.blockentity.template.LTXIMachineBlockEntity;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public abstract class BaseECABlockEntity extends LTXIMachineBlockEntity
{
    private final LimaBlockEntityItems chargingInventory;
    private final Map<Direction, BlockCapabilityCache<EnergyHandler, @Nullable Direction>> energyConnections = new EnumMap<>(Direction.class);

    public BaseECABlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state, @Nullable VariableEnergyHandler energyStorage)
    {
        super(type, pos, state, energyStorage);
        this.chargingInventory = new LimaBlockEntityItems(this, BlockContentsType.GENERAL, 4);
    }

    public abstract LimaColor getRemoteEnergyFillColor();

    public abstract float getRemoteEnergyFill();

    @Override
    public boolean hasStatsTooltips()
    {
        return false;
    }

    @Override
    public @Nullable LimaBlockEntityItems getItems(BlockContentsType contentsType)
    {
        return switch (contentsType)
        {
            case GENERAL -> chargingInventory;
            case AUXILIARY -> auxInventory;
            default -> null;
        };
    }

    @Override
    public boolean isItemValid(BlockContentsType contentsType, int index, ItemResource resource)
    {
        return contentsType == BlockContentsType.GENERAL ? LimaItemUtil.hasEnergyCapability(ItemAccess.forHandlerIndexStrict(chargingInventory, index)) : super.isItemValid(contentsType, index, resource);
    }

    @Override
    public IOAccess getResourceLevelItemIO(BlockContentsType contentsType, int index, ItemResource resource)
    {
        return contentsType == BlockContentsType.GENERAL ? checkResourceEnergy(resource) : IOAccess.DISABLED;
    }

    private IOAccess checkResourceEnergy(ItemResource resource)
    {
        if (resource.isEmpty()) return IOAccess.INPUT_ONLY;

        ItemAccess access = ItemAccess.forStack(resource.toStack());
        EnergyHandler itemEnergy = access.getCapability(Capabilities.Energy.ITEM);

        if (itemEnergy == null || EnergyHandlerUtil.isFull(itemEnergy)) return IOAccess.OUTPUT_ONLY;

        return IOAccess.DISABLED;
    }

    @Override
    public @Nullable ResourceHandler<ItemResource> createExternalItems(@Nullable Direction side)
    {
        return chargingInventory.createIOWrapper(getTopLevelItemIO(side));
    }

    @Override
    public @Nullable EnergyHandler getNeighborEnergyStorage(Direction side)
    {
        return energyConnections.get(side).getCapability();
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        // Fill buffer from input slot
        pullEnergyFromAux();

        // Charge items in the charging inventory
        VariableEnergyHandler machineEnergy = getEnergy();
        if (machineEnergy.getAmountAsInt() > 0)
        {
            for (int i = 0; i < chargingInventory.size(); i++)
            {
                ItemAccess access = ItemAccess.forHandlerIndexStrict(chargingInventory, i);
                EnergyHandler itemEnergy = access.getCapability(Capabilities.Energy.ITEM);
                if (itemEnergy != null) EnergyHandlerUtil.move(machineEnergy, itemEnergy, machineEnergy.getTransferRate(), null);
            }
        }

        // Auto output energy if option enabled
        tickItemAutoOutput(100, chargingInventory, resource -> checkResourceEnergy(resource).allowsOutput());
        pushEnergyToSides();
    }

    @Override
    protected void createConnectionCaches(ServerLevel level, Direction side)
    {
        super.createConnectionCaches(level, side);
        energyConnections.put(side, createCapabilityCache(Capabilities.Energy.BLOCK, level, side));
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector) {}

    @Override
    public Component getMenuTitle(BlockEntityMenuType<?, ?> menuType)
    {
        return LTXIBlocks.ENERGY_CELL_ARRAY.get().getName();
    }
}