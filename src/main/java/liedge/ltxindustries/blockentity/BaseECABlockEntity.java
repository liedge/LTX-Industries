package liedge.ltxindustries.blockentity;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.menu.BlockEntityMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import liedge.ltxindustries.blockentity.template.EnergyMachineBlockEntity;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public abstract class BaseECABlockEntity extends EnergyMachineBlockEntity
{
    private final LimaBlockEntityItemHandler chargingInventory;
    private final Map<Direction, BlockCapabilityCache<IEnergyStorage, Direction>> energyConnections = new EnumMap<>(Direction.class);

    public BaseECABlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state, @Nullable LimaEnergyStorage energyStorage)
    {
        super(type, pos, state, energyStorage);
        this.chargingInventory = new LimaBlockEntityItemHandler(this, 4, BlockContentsType.GENERAL);
    }

    public abstract LimaColor getRemoteEnergyFillColor();

    public abstract float getRemoteEnergyFill();

    @Override
    public boolean hasStatsTooltips()
    {
        return false;
    }

    @Override
    public @Nullable LimaBlockEntityItemHandler getItemHandler(BlockContentsType contentsType)
    {
        return switch (contentsType)
        {
            case GENERAL -> chargingInventory;
            case AUXILIARY -> getAuxInventory();
            default -> null;
        };
    }

    @Override
    public boolean isItemValid(BlockContentsType contentsType, int slot, ItemStack stack)
    {
        return contentsType == BlockContentsType.GENERAL ? LimaItemUtil.hasEnergyCapability(stack) : super.isItemValid(contentsType, slot, stack);
    }

    @Override
    public IOAccess getItemHandlerSlotIO(BlockContentsType contentsType, int slot)
    {
        return contentsType == BlockContentsType.GENERAL ? checkStackEnergy(chargingInventory.getStackInSlot(slot)) : IOAccess.DISABLED;
    }

    private IOAccess checkStackEnergy(ItemStack stack)
    {
        if (stack.isEmpty()) return IOAccess.INPUT_ONLY;

        IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (storage == null || storage.getEnergyStored() >= storage.getMaxEnergyStored()) return IOAccess.OUTPUT_ONLY;

        return IOAccess.DISABLED;
    }

    @Override
    public @Nullable IItemHandler createItemIOWrapper(@Nullable Direction side)
    {
        return chargingInventory.createIOWrapper(getSideIOForItems(side));
    }

    @Override
    public @Nullable IEnergyStorage getNeighborEnergyStorage(Direction side)
    {
        return energyConnections.get(side).getCapability();
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        // Fill buffer from input slot
        fillEnergyBuffer();

        // Charge items in the charging inventory
        LimaEnergyStorage machineEnergy = getEnergyStorage();
        if (machineEnergy.getEnergyStored() > 0)
        {
            for (int i = 0; i < chargingInventory.getSlots(); i++)
            {
                ItemStack stack = chargingInventory.getStackInSlot(i);
                IEnergyStorage stackEnergy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
                if (stackEnergy != null)
                {
                    LimaEnergyUtil.transferEnergyBetween(machineEnergy, stackEnergy, machineEnergy.getEnergyStored(), false);
                }
            }
        }

        // Auto output energy if option enabled
        tickItemAutoOutput(100, chargingInventory, stack -> checkStackEnergy(stack).allowsOutput());
        autoOutputEnergy();
    }

    @Override
    protected void createConnectionCaches(ServerLevel level, Direction side)
    {
        super.createConnectionCaches(level, side);
        energyConnections.put(side, createCapabilityCache(Capabilities.EnergyStorage.BLOCK, level, side));
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector) {}

    @Override
    public Component getMenuTitle(BlockEntityMenuType<?, ?> menuType)
    {
        return LTXIBlocks.ENERGY_CELL_ARRAY.get().getName();
    }
}