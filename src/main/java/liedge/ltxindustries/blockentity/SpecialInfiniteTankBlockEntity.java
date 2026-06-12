package liedge.ltxindustries.blockentity;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.transfer.LimaInfiniteResources;
import liedge.limacore.transfer.fluid.FluidHolderBlockEntity;
import liedge.limacore.transfer.fluid.LimaBlockEntityFluids;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import liedge.ltxindustries.blockentity.template.MachineBaseBlockEntity;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jspecify.annotations.Nullable;

public final class SpecialInfiniteTankBlockEntity extends MachineBaseBlockEntity implements FluidHolderBlockEntity
{
    public static SpecialInfiniteTankBlockEntity createWaterTank(BlockPos pos, BlockState state)
    {
        return new SpecialInfiniteTankBlockEntity(LTXIBlockEntities.INFINITE_WATER_TANK.get(), pos, state, FluidResource.of(Fluids.WATER));
    }

    public static SpecialInfiniteTankBlockEntity createLavaTank(BlockPos pos, BlockState state)
    {
        return new SpecialInfiniteTankBlockEntity(LTXIBlockEntities.INFINITE_LAVA_TANK.get(), pos, state, FluidResource.of(Fluids.LAVA));
    }

    private final ResourceHandler<FluidResource> infiniteFluids;

    private SpecialInfiniteTankBlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state, FluidResource resource)
    {
        super(type, pos, state, 1);
        this.infiniteFluids = new LimaInfiniteResources<>(resource);
    }

    public ResourceHandler<FluidResource> getInfiniteFluids()
    {
        return infiniteFluids;
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        tickAutoResourceOutput(5, null, infiniteFluids);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector) { }

    @Override
    public @Nullable LimaBlockEntityFluids getFluids(BlockContentsType contentsType)
    {
        return null;
    }

    @Override
    public @Nullable ResourceHandler<FluidResource> createExternalFluids(@Nullable Direction side)
    {
        IOAccess access = getTopLevelFluidIO(side);
        return access == IOAccess.OUTPUT_ONLY ? infiniteFluids : null;
    }

    @Override
    public int getBaseFluidCapacity(BlockContentsType contentsType)
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getBaseFluidTransferRate(BlockContentsType contentsType)
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean hasStatsTooltips()
    {
        return false;
    }
}