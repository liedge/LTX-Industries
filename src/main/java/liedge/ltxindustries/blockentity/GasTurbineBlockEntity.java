package liedge.ltxindustries.blockentity;

import com.google.common.primitives.Ints;
import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.transfer.energy.EnergyHolderBlockEntity;
import liedge.limacore.transfer.energy.LimaBlockEntityEnergy;
import liedge.limacore.transfer.energy.LimaEnergyHandler;
import liedge.limacore.transfer.fluid.FluidHolderBlockEntity;
import liedge.limacore.transfer.fluid.LimaBlockEntityFluids;
import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.blockentity.base.EnergyGeneratorBlockEntity;
import liedge.ltxindustries.blockentity.template.UpgradableBaseBlockEntity;
import liedge.ltxindustries.data.TurbineFuel;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.registry.LTXIDataMaps;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.util.LTXITooltipUtil;
import liedge.ltxindustries.util.LTXIUpgradeUtil;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

public class GasTurbineBlockEntity extends UpgradableBaseBlockEntity implements EnergyHolderBlockEntity, FluidHolderBlockEntity, EnergyGeneratorBlockEntity
{
    private final LimaBlockEntityEnergy energyHandler;
    private final LimaBlockEntityFluids fuelTank;
    private int energyGeneration = getBaseEnergyGeneration();
    private int partialFuelEnergy;

    // Client properties
    private int spinSpeed;
    private float bladesYRot0;
    private float bladesYRot;

    public GasTurbineBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.GAS_TURBINE.get(), pos, state, 1);

        this.energyHandler = new LimaBlockEntityEnergy(this);
        this.fuelTank = new LimaBlockEntityFluids(this, BlockContentsType.INPUT, 1);
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        boolean wasActiveThisTick = false;

        if (!EnergyHandlerUtil.isFull(energyHandler))
        {
            int energyNeeded = Math.min(energyHandler.getCapacityAsInt() - energyHandler.getAmountAsInt(), energyGeneration);
            FluidResource fuelResource = fuelTank.getResource(0);
            int energyDensity = getEnergyDensity(fuelResource);
            int fuelToBurn = getFuelToBurn(energyNeeded, fuelTank.getAmountAsInt(0), energyDensity);

            if (partialFuelEnergy > 0 || fuelToBurn > 0)
            {
                try (Transaction tx = Transaction.openRoot())
                {
                    int totalEnergy = partialFuelEnergy + (fuelToBurn * energyDensity);
                    int toInsert = Math.min(energyNeeded, totalEnergy);
                    int inserted = energyHandler.insert(toInsert, tx);

                    if (inserted == toInsert)
                    {
                        int fuelUsed = fuelToBurn > 0 ? fuelTank.extract(0, fuelResource, fuelToBurn, tx) : 0;

                        if (fuelUsed == fuelToBurn)
                        {
                            tx.commit();

                            partialFuelEnergy = Math.max(0, totalEnergy - inserted);
                            wasActiveThisTick = true;
                        }
                    }
                }
            }
        }

        LTXIBlockProperties.updateBinaryState(level, pos, state, wasActiveThisTick);
    }

    private int getEnergyDensity(FluidResource resource)
    {
        TurbineFuel data = resource.getData(LTXIDataMaps.TURBINE_FUELS);
        return data != null ? data.energy() : 0;
    }

    private int getFuelToBurn(int energyNeeded, int fuelStored, int energyDensity)
    {
        int extraEnergyNeed = energyNeeded - partialFuelEnergy;
        if (extraEnergyNeed <= 0 || fuelStored <= 0 || energyDensity <= 0) return 0;

        int fuelToBurn = Mth.ceil(extraEnergyNeed / (double) energyDensity);
        return Math.min(fuelToBurn, fuelStored);
    }

    // Energy generator impl

    @Override
    public int getBaseEnergyGeneration()
    {
        return LTXIMachinesConfig.GAS_TURBINE_GENERATION.getAsInt();
    }

    @Override
    public int getEnergyGeneration()
    {
        return energyGeneration;
    }

    @Override
    public void setEnergyGeneration(int energyGeneration)
    {
        this.energyGeneration = energyGeneration;
    }

    // Energy holder impl

    @Override
    public LimaEnergyHandler getEnergy()
    {
        return energyHandler;
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.GAS_TURBINE_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyTransferRate()
    {
        return Ints.saturatedCast(energyGeneration * 4L);
    }

    // Fluid holder impl

    @Override
    public @Nullable LimaBlockEntityFluids getFluids(BlockContentsType contentsType)
    {
        return contentsType == BlockContentsType.INPUT ? fuelTank : null;
    }

    @Override
    public boolean isFluidValid(BlockContentsType contentsType, int index, FluidResource resource)
    {
        return contentsType == BlockContentsType.INPUT && getEnergyDensity(resource) > 0;
    }

    @Override
    public int getBaseFluidCapacity(BlockContentsType contentsType)
    {
        return 32_000;
    }

    @Override
    public int getBaseFluidTransferRate(BlockContentsType contentsType)
    {
        return getBaseFluidCapacity(contentsType) / 10;
    }

    // Save/load stuff

    @Override
    public void onUpgradeRefresh(LootContext context, Upgrades upgrades)
    {
        EnergyGeneratorBlockEntity.onUpgradeRefresh(this, context, upgrades);
        super.onUpgradeRefresh(context, upgrades);
        LTXIUpgradeUtil.applyFluidMachineUpgrades(this, upgrades, context, i -> i / 10);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components)
    {
        super.applyImplicitComponents(components);
        energyHandler.readComponents(components);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        super.collectImplicitComponents(components);
        energyHandler.writeComponents(components);
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output)
    {
        super.removeComponentsFromTag(output);
        output.discard(LimaCommonConstants.KEY_ENERGY_CONTAINER);
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);

        loadEnergyStorage(input);
        loadFluidResources(input);
        this.partialFuelEnergy = input.getIntOr("partial_fuel_energy", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);

        saveEnergyStorage(output);
        saveFluidResources(output);
        output.putInt("partial_fuel_energy", partialFuelEnergy);
    }

    // Client stuff

    public float lerpRotation(float partialTick)
    {
        return Mth.rotLerp(partialTick, bladesYRot0, bladesYRot);
    }

    @Override
    protected void tickClient(Level level, BlockPos pos, BlockState state)
    {
        if (LTXIBlockProperties.isMachineActive(state))
        {
            if (spinSpeed < 25) spinSpeed += 1;
        }
        else
        {
            if (spinSpeed > 0) spinSpeed = Math.max(0, spinSpeed - 1);
        }

        bladesYRot0 = bladesYRot;
        bladesYRot = (bladesYRot + spinSpeed) % 360;
    }

    @Override
    public void appendStatsTooltips(TooltipLineConsumer consumer)
    {
        LTXITooltipUtil.appendEnergyGenerationTooltip(consumer, energyGeneration);
    }
}