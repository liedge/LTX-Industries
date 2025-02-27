package liedge.limatech.lib.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class BaseBlockMesh
{
    private final List<BlockMeshPosition> meshPositions;
    private final BlockMeshPosition primary;

    BaseBlockMesh(List<BlockMeshPosition> meshPositions, BlockMeshPosition primary)
    {
        this.meshPositions = meshPositions;
        this.primary = primary;
    }

    public BlockMeshPosition getPrimary()
    {
        return primary;
    }

    public abstract boolean isMeshValid(Level level, BlockPos origin, BlockMeshPosition meshPosition, Direction zAxis);

    protected BlockPos getRelativeBlockPos(BlockPos pos, Vec3i delta, Direction xAxis, Direction yAxis, Direction zAxis)
    {
        int x = pos.getX() + (xAxis.getStepX() * delta.getX()) + (zAxis.getStepX() * delta.getZ());
        int y = pos.getY() + (yAxis.getStepY() * delta.getY());
        int z = pos.getZ() + (xAxis.getStepZ() * delta.getX()) + (zAxis.getStepZ() * delta.getZ());
        return new BlockPos(x, y, z);
    }

    protected Stream<MeshBlockPos> meshStream(BlockPos blockPos, BlockMeshPosition meshPosition, Direction zAxis)
    {
        return StreamSupport.stream(meshIterable(blockPos, meshPosition, zAxis).spliterator(), false);
    }

    private Iterable<MeshBlockPos> meshIterable(BlockPos blockPos, BlockMeshPosition meshPosition, Direction zAxis)
    {
        BlockPos start = getRelativeBlockPos(blockPos, meshPosition.pos(), zAxis.getClockWise(), Direction.DOWN, zAxis.getOpposite());
        Direction xAxis = zAxis.getCounterClockWise();

        return () -> new Iterator<>()
        {
            private final MeshBlockPos cursor = new MeshBlockPos();
            private int current = 0;

            @Override
            public boolean hasNext()
            {
                return current < meshPositions.size();
            }

            @Override
            public MeshBlockPos next()
            {
                BlockMeshPosition next = meshPositions.get(current);

                int x = start.getX() + (xAxis.getStepX() * next.pos().getX()) + (zAxis.getStepX() * next.pos().getZ());
                int y = start.getY() + next.pos().getY();
                int z = start.getZ() + (xAxis.getStepZ() * next.pos().getX()) + (zAxis.getStepZ() * next.pos().getZ());

                cursor.setMeshPosition(next);
                cursor.set(x, y, z);
                current++;

                return cursor;
            }
        };
    }

    public static final class MeshBlockPos extends BlockPos.MutableBlockPos
    {
        private BlockMeshPosition meshPosition = BlockMeshPosition.INVALID;

        public void setMeshPosition(BlockMeshPosition meshPosition)
        {
            this.meshPosition = meshPosition;
        }

        public BlockMeshPosition getMeshPosition()
        {
            return meshPosition;
        }
    }
}