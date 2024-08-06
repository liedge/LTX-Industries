package liedge.limatech.lib;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

public class FluidClipContext extends ClipContext
{
    private final @Nullable FluidType fluidType;

    public FluidClipContext(Vec3 from, Vec3 to, Block block, Entity entity, @Nullable FluidType fluidType)
    {
        super(from, to, block, Fluid.NONE, entity);
        this.fluidType = fluidType;
    }

    @Override
    public VoxelShape getFluidShape(FluidState state, BlockGetter level, BlockPos pos)
    {
        return fluidType != null && state.getFluidType().equals(fluidType) ? state.getShape(level, pos) : Shapes.empty();
    }
}