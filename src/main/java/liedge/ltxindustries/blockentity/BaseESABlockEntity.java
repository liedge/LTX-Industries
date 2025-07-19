package liedge.ltxindustries.blockentity;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.IOAccessSets;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.blockentity.base.IOController;
import liedge.ltxindustries.blockentity.base.SidedAccessBlockEntityType;
import liedge.ltxindustries.blockentity.base.SidedAccessRules;
import liedge.ltxindustries.registry.game.LTXIMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class BaseESABlockEntity extends SidedItemEnergyMachineBlockEntity
{
    public static final SidedAccessRules ITEM_ACCESS_RULES = SidedAccessRules.allSides(IOAccessSets.ALL_ALLOWED, IOAccess.DISABLED, false, false);

    private final Map<Direction, BlockCapabilityCache<IEnergyStorage, Direction>> energyConnections = new EnumMap<>(Direction.class);

    public BaseESABlockEntity(SidedAccessBlockEntityType<?> type, BlockPos pos, BlockState state, @Nullable LimaEnergyStorage energyStorage)
    {
        super(type, pos, state, 5, energyStorage);
    }

    public abstract LimaColor getRemoteEnergyFillColor();

    public abstract float getRemoteEnergyFill();

    @Override
    public boolean hasStatsTooltips()
    {
        return false;
    }

    @Override
    public IOAccess getPrimaryHandlerItemSlotIO(int slot)
    {
        if (slot > 0 && slot < 5)
        {
            ItemStack slotItem = getItemHandler().getStackInSlot(slot);
            if (slotItem.isEmpty()) return IOAccess.INPUT_ONLY;

            IEnergyStorage storage = slotItem.getCapability(Capabilities.EnergyStorage.ITEM);
            if (storage != null && storage.getEnergyStored() == storage.getMaxEnergyStored()) return IOAccess.OUTPUT_ONLY;
        }

        return IOAccess.DISABLED;
    }

    @Override
    public @Nullable IEnergyStorage getAdjacentEnergyStorage(Direction side)
    {
        return energyConnections.get(side).getCapability();
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        // Fill buffer from input slot
        fillEnergyBuffer();

        // Charge item in output slot
        LimaEnergyStorage machineEnergy = getEnergyStorage();
        if (machineEnergy.getEnergyStored() > 0)
        {
            IntStream.rangeClosed(1, 4)
                    .mapToObj(i -> getItemHandler().getStackInSlot(i).getCapability(Capabilities.EnergyStorage.ITEM))
                    .flatMap(Stream::ofNullable)
                    .forEach(itemEnergy -> LimaEnergyUtil.transferEnergyBetween(machineEnergy, itemEnergy, machineEnergy.getEnergyStored(), false));
        }

        // Auto output energy if option enabled
        autoOutputEnergy();
    }

    @Override
    protected void createConnectionCaches(ServerLevel level, Direction side)
    {
        super.createConnectionCaches(level, side);
        energyConnections.put(side, createCapabilityCache(Capabilities.EnergyStorage.BLOCK, level, side));
    }

    @Override
    protected void onLoadServer(ServerLevel level)
    {
        super.onLoadServer(level);
        updateAndSyncBlockState(level);
    }

    @Override
    protected boolean isItemValidForPrimaryHandler(int slot, ItemStack stack)
    {
        return LimaItemUtil.hasEnergyCapability(stack);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector) {}

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LTXIMenus.ENERGY_STORAGE_ARRAY.get();
    }

    @Override
    protected void onIOControlsChangedInternal(BlockEntityInputType inputType, Level level)
    {
        updateAndSyncBlockState(level);
    }

    private void updateAndSyncBlockState(Level level)
    {
        IOController energyControl = getIOController(BlockEntityInputType.ENERGY);
        BlockState state = getBlockState();
        for (Direction side : Direction.values())
        {
            state = state.setValue(LTXIBlockProperties.getESASideIOProperty(side), energyControl.getSideIOState(side));
        }
        level.setBlockAndUpdate(getBlockPos(), state);
    }
}