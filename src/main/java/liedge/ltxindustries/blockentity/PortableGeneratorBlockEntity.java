package liedge.ltxindustries.blockentity;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.network.sync.SimpleValueTracker;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.transfer.item.LimaBlockEntityItems;
import liedge.ltxindustries.blockentity.template.BaseGeneratorBlockEntity;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

public class PortableGeneratorBlockEntity extends BaseGeneratorBlockEntity
{
    public static final int MAX_FUEL_UNITS = 1024;

    private final LimaBlockEntityItems inputItems;
    private int fuelUnits;
    private int partialFuelEnergy;

    public PortableGeneratorBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.PORTABLE_GENERATOR.get(), pos, state);
        this.inputItems = new LimaBlockEntityItems(this, BlockContentsType.INPUT, 1);
    }

    public int getFuelUnits()
    {
        return fuelUnits;
    }

    private void setFuelUnits(int fuelUnits)
    {
        this.fuelUnits = fuelUnits;
    }

    public LimaDataWatcher<Integer> syncFuelUnits()
    {
        return SimpleValueTracker.create(LimaCoreNetworkSerializers.VAR_INT, this::getFuelUnits, this::setFuelUnits).setAutomatic();
    }

    @Override
    public int getBaseEnergyGeneration()
    {
        return LTXIMachinesConfig.PORTABLE_GENERATOR_GENERATION.getAsInt();
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.PORTABLE_GENERATOR_CAPACITY.getAsInt();
    }

    @Override
    public @Nullable LimaBlockEntityItems getItems(BlockContentsType contentsType)
    {
        return contentsType == BlockContentsType.INPUT ? inputItems : super.getItems(contentsType);
    }

    @Override
    public boolean isItemValid(BlockContentsType contentsType, int index, ItemResource resource)
    {
        return contentsType == BlockContentsType.INPUT ? resource.getData(NeoForgeDataMaps.FURNACE_FUELS) != null :
                super.isItemValid(contentsType, index, resource);
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        // Process fuels
        if (level.getGameTime() % 5L == 0 && fuelUnits < MAX_FUEL_UNITS)
        {
            ItemResource resource = inputItems.getResource(0);
            FurnaceFuel vanillaFuel = resource.getData(NeoForgeDataMaps.FURNACE_FUELS);

            if (vanillaFuel != null)
            {
                int fuelPerResource = Math.max(vanillaFuel.burnTime() / 100, 1);
                int consumableItems = (MAX_FUEL_UNITS - fuelUnits) / fuelPerResource;
                consumableItems = Math.min(consumableItems, inputItems.getAmountAsInt(0));

                try (Transaction tx = Transaction.openRoot())
                {
                    int extracted = inputItems.extract(0, resource, consumableItems, tx);
                    if (extracted > 0)
                    {
                        this.fuelUnits += extracted * fuelPerResource;
                        tx.commit();
                    }
                }
            }
        }

        // Generate power and save state
        boolean wasActiveThisTick = false;
        EnergyHandler energy = getEnergy();
        if (!EnergyHandlerUtil.isFull(energy))
        {
            int energyNeeded = Math.min(energy.getCapacityAsInt() - energy.getAmountAsInt(), getEnergyGeneration());

            int fuelNeeded = getNeededFuel(energyNeeded);
            int fuelToBurn = Math.min(fuelNeeded, fuelUnits);

            if (energyNeeded <= partialFuelEnergy || fuelToBurn > 0)
            {
                try (Transaction tx = Transaction.openRoot())
                {
                    int totalEnergy = partialFuelEnergy + (fuelToBurn * LTXIMachinesConfig.PORTABLE_GENERATOR_ENERGY_PER_FUEL.getAsInt());
                    int inserted = Math.min(energyNeeded, totalEnergy);
                    inserted = energy.insert(inserted, tx);

                    if (inserted > 0)
                    {
                        tx.commit();

                        fuelUnits = Math.max(0, fuelUnits - fuelToBurn);
                        partialFuelEnergy = Math.max(0, totalEnergy - inserted);
                        wasActiveThisTick = true;
                    }
                }
            }
        }
        setActive(level, pos, state, wasActiveThisTick);

        pushEnergyToSides();
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        this.fuelUnits = input.getIntOr("fuel_units", 0);
        this.partialFuelEnergy = input.getIntOr("partial_fuel_energy", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        output.putInt("fuel_units", this.fuelUnits);
        output.putInt("partial_fuel_energy", this.partialFuelEnergy);
    }

    private int getNeededFuel(int energyNeeded)
    {
        int extraNeeded = (energyNeeded - partialFuelEnergy);

        if (extraNeeded <= 0)
        {
            return 0;
        }
        else
        {
            return Mth.ceil(extraNeeded / (double) LTXIMachinesConfig.PORTABLE_GENERATOR_ENERGY_PER_FUEL.getAsInt());
        }
    }
}