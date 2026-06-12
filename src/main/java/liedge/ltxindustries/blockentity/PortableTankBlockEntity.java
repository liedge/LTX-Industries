package liedge.ltxindustries.blockentity;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.network.sync.SimpleValueTracker;
import liedge.limacore.network.sync.ValueTracker;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.transfer.LimaTransferUtil;
import liedge.limacore.transfer.fluid.FluidHolderBlockEntity;
import liedge.limacore.transfer.fluid.LimaBlockEntityFluids;
import liedge.ltxindustries.blockentity.template.MachineBaseBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jspecify.annotations.Nullable;

public class PortableTankBlockEntity extends MachineBaseBlockEntity implements FluidHolderBlockEntity
{
    public static final int FLUID_VISUAL_LEVELS = 11;

    public static int getFluidVisualLevel(int stored, int capacity)
    {
        if (stored == 0) return 0;

        float fill = Math.clamp(LimaCoreMath.divideFloat(stored, capacity), 0f, 1f);
        return Math.max(LimaCoreMath.round(fill * FLUID_VISUAL_LEVELS), 1);
    }

    // Class def
    private final LimaBlockEntityFluids tank;

    // Client properties
    public FluidResource clientFluid = FluidResource.EMPTY;
    public int clientFluidLevel;

    private final ValueTracker<FluidResource> fluidTracker;
    private final ValueTracker<Integer> fluidLevelTracker;

    public PortableTankBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.PORTABLE_TANK.get(), pos, state, 1);
        this.tank = new LimaBlockEntityFluids(this, BlockContentsType.GENERAL, 1);

        this.fluidTracker = SimpleValueTracker.create(LimaCoreNetworkSerializers.FLUID_RESOURCE, () -> tank.getResource(0), resource -> this.clientFluid = resource);
        this.fluidLevelTracker = SimpleValueTracker.create(LimaCoreNetworkSerializers.VAR_INT, () -> getFluidVisualLevel(tank.getAmountAsInt(0), tank.getCapacity()), fill -> this.clientFluidLevel = fill);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(fluidTracker);
        collector.register(fluidLevelTracker);
    }

    @Override
    public void onFluidChanged(BlockContentsType contentsType, int index, FluidStack previousContents)
    {
        setChanged();

        if (checkServerSide())
        {
            fluidTracker.checkForChanges();
            fluidLevelTracker.checkForChanges();
        }
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        tickAutoResourceInput(20, null, tank);
        tickAutoResourceOutput(20, null, tank);
    }

    @Override
    public @Nullable LimaBlockEntityFluids getFluids(BlockContentsType contentsType)
    {
        return contentsType == BlockContentsType.GENERAL ? tank : null;
    }

    @Override
    public @Nullable ResourceHandler<FluidResource> createExternalFluids(@Nullable Direction side)
    {
        IOAccess topLevelAccess = getTopLevelFluidIO(side);
        return topLevelAccess != IOAccess.DISABLED ? tank.createIOWrapper(topLevelAccess) : null;
    }

    @Override
    public int getBaseFluidCapacity(BlockContentsType contentsType)
    {
        return LTXIMachinesConfig.PORTABLE_TANK_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseFluidTransferRate(BlockContentsType contentsType)
    {
        return getBaseFluidCapacity(contentsType) / 10;
    }

    @Override
    public void onUpgradeRefresh(LootContext context, Upgrades upgrades)
    {
        super.onUpgradeRefresh(context, upgrades);

        double upgradeCapacity = upgrades.runValueOps(LTXIUpgradeEffectComponents.FLUID_CAPACITY, context, LTXIMachinesConfig.PORTABLE_TANK_CAPACITY.getAsInt());
        int newCapacity = Math.max(LimaCoreMath.round(upgradeCapacity), 1);
        int newTransferRate = Math.max(newCapacity / 10, 1);

        tank.setCapacity(newCapacity);
        tank.setTransferRate(newTransferRate);

        // Dump fluid if upgrade removed
        if (tank.getAmountAsInt(0) > newCapacity)
        {
            tank.set(0, tank.getResource(0), newCapacity);
        }

        fluidLevelTracker.checkForChanges();
    }

    @Override
    public void appendStatsTooltips(TooltipLineConsumer consumer)
    {
        consumer.accept(LTXILangKeys.INLINE_FLUID_TRANSFER_RATE.translateArgs(LimaTransferUtil.formatFullFluidAmount(tank.getTransferRate())));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        super.collectImplicitComponents(components);

        FluidStack stack = tank.getResource(0).toStack(tank.getAmountAsInt(0));
        components.set(LimaCoreDataComponents.FLUID_CONTENT, SimpleFluidContent.copyOf(stack));
        components.set(LimaCoreDataComponents.FLUID_CAPACITY, tank.getCapacity());
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components)
    {
        super.applyImplicitComponents(components);

        SimpleFluidContent content = components.get(LimaCoreDataComponents.FLUID_CONTENT);
        if (content != null)
        {
            FluidResource resource = FluidResource.of(content.copy());
            tank.set(0, resource, content.getAmount());
        }
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output)
    {
        super.removeComponentsFromTag(output);

        output.discard(LimaCommonConstants.KEY_SINGLE_FLUID);
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        input.child(LimaCommonConstants.KEY_SINGLE_FLUID).ifPresent(tank::deserialize);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        tank.serialize(output.child(LimaCommonConstants.KEY_SINGLE_FLUID));
    }
}