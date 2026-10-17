package liedge.ltxindustries.world.level;

import liedge.limacore.util.LimaShapesUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public final class DynamicClipContext extends ClipContext
{
    private final FluidCollision fluidCollision;
    private final MutableDouble pierce;
    private final @Nullable AABB noCheckArea;

    public DynamicClipContext(Vec3 from, Vec3 to, Entity entity, FluidCollision fluidCollision, double pierceAmount, @Nullable AABB noCheckArea)
    {
        super(from, to, Block.COLLIDER, Fluid.NONE, entity);
        this.fluidCollision = fluidCollision;
        this.pierce = new MutableDouble(pierceAmount);
        this.noCheckArea = noCheckArea;
    }

    public DynamicClipContext(Vec3 from, Vec3 to, Entity entity, FluidCollision fluidCollision, double pierceAmount)
    {
        this(from, to, entity, fluidCollision, pierceAmount, null);
    }

    @Override
    public VoxelShape getBlockShape(BlockState state, BlockGetter level, BlockPos pos)
    {
        if (noCheckArea != null && noCheckArea.contains(pos.getX(), pos.getY(), pos.getZ())) return Shapes.empty();

        VoxelShape shape = super.getBlockShape(state, level, pos);
        if (shape.isEmpty() || pierce.doubleValue() <= 0d) return shape;

        boolean pierced = true;

        List<AABB> boundingBoxes = LimaShapesUtil.toLevelBoundingBoxes(shape, pos);
        for (AABB bb : boundingBoxes)
        {
            Optional<Vec3> entry = bb.contains(getFrom()) ? Optional.of(getFrom()) : bb.clip(getFrom(), getTo());
            Optional<Vec3> exit = bb.contains(getTo()) ? Optional.of(getTo()) : bb.clip(getTo(), getFrom());

            if (entry.isPresent() && exit.isPresent())
            {
                double clipDistance = entry.get().distanceTo(exit.get());

                if (clipDistance <= pierce.doubleValue())
                {
                    pierce.subtract(clipDistance);
                }
                else
                {
                    pierced = false;
                    break;
                }
            }
        }

        return pierced ? Shapes.empty() : shape;
    }

    @Override
    public VoxelShape getFluidShape(FluidState state, BlockGetter level, BlockPos pos)
    {
        return fluidCollision.test(state, level, pos) ? state.getShape(level, pos) : Shapes.empty();
    }
}