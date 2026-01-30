package liedge.ltxindustries.blockentity.turret;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class TurretClipContext extends ClipContext
{
    private final AABB boundingBox;

    public TurretClipContext(Vec3 from, Vec3 to, Entity entity, AABB boundingBox)
    {
        super(from, to, Block.COLLIDER, Fluid.NONE, entity);
        this.boundingBox = boundingBox;
    }

    @Override
    public VoxelShape getBlockShape(BlockState blockState, BlockGetter level, BlockPos pos)
    {
        if (boundingBox.contains(pos.getX() + 0.1d, pos.getY() + 0.1d, pos.getZ() + 0.1d))
        {
            return Shapes.empty();
        }
        else
        {
            return super.getBlockShape(blockState, level, pos);
        }
    }
}