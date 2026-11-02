package liedge.ltxindustries.block.mesh;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class BlockMesh
{
    public static BlockMesh.Builder builder(String name)
    {
        return new Builder(LTXIndustries.RESOURCES.id(name));
    }

    private final Identifier id;
    private final List<MeshPosition> meshPositions;
    private final MeshPosition primary;

    private BlockMesh(Identifier id, List<MeshPosition> meshPositions, MeshPosition primary)
    {
        this.id = id;
        this.meshPositions = meshPositions;
        this.primary = primary;
    }

    public Identifier getId()
    {
        return id;
    }

    public MeshPosition getPrimary()
    {
        return primary;
    }

    public List<MeshPosition> getMeshPositions()
    {
        return meshPositions;
    }

    public MeshPosition getMeshPosition(int index)
    {
        Preconditions.checkElementIndex(index, meshPositions.size(), "Mesh Positions");
        return meshPositions.get(index);
    }

    public boolean canPlaceMesh(Level level, BlockPos origin, MeshPosition meshPosition, Direction zAxis)
    {
        int buildLimit = level.getMaxY() - 1;
        return meshStream(origin, meshPosition, zAxis).allMatch(mp -> mp.getY() < buildLimit && level.getBlockState(mp).canBeReplaced());
    }

    public BlockPos getPrimaryBlockPos(BlockPos pos, MeshPosition from, Direction zAxis)
    {
        return getRelativeBlockPos(pos, from, primary, zAxis);
    }

    public BlockPos getRelativeBlockPos(BlockPos blockPos, MeshPosition from, MeshPosition to, Direction zAxis)
    {
        Vec3i offset = computeMeshOffset(from, to, zAxis);
        return blockPos.offset(offset);
    }

    public Vec3i computeMeshOffset(MeshPosition from, MeshPosition to, Direction zAxis)
    {
        Direction xAxis = zAxis.getCounterClockWise();
        Vec3i delta = to.subtract(from);

        int x = xAxis.getStepX() * delta.getX() + zAxis.getStepX() * delta.getZ();
        int y = delta.getY();
        int z = xAxis.getStepZ() * delta.getX() + zAxis.getStepZ() * delta.getZ();

        return new Vec3i(x, y, z);
    }

    public Stream<MeshBlockPos> meshStream(BlockPos blockPos, MeshPosition meshPosition, Direction zAxis)
    {
        return StreamSupport.stream(meshIterable(blockPos, meshPosition, zAxis).spliterator(), false);
    }

    public Iterable<MeshBlockPos> meshIterable(BlockPos blockPos, MeshPosition meshPosition, Direction zAxis)
    {
        final BlockPos primaryPos = getPrimaryBlockPos(blockPos, meshPosition, zAxis);
        Direction xAxis = zAxis.getCounterClockWise();

        return () -> new AbstractIterator<>()
        {
            private @Nullable MeshBlockPos cursor;
            private int current = 0;

            @Override
            protected @Nullable MeshBlockPos computeNext()
            {
                if (current >= meshPositions.size()) return endOfData();

                MeshPosition next = meshPositions.get(current);

                int x = primaryPos.getX() + (xAxis.getStepX() * next.pos().getX()) + (zAxis.getStepX() * next.pos().getZ());
                int y = primaryPos.getY() + next.pos().getY();
                int z = primaryPos.getZ() + (xAxis.getStepZ() * next.pos().getX()) + (zAxis.getStepZ() * next.pos().getZ());

                if (cursor == null)
                    cursor = new MeshBlockPos(next);
                else
                    cursor.setMeshPosition(next);

                cursor.set(x, y, z);
                current++;

                return cursor;
            }
        };
    }

    public static final class MeshBlockPos extends BlockPos.MutableBlockPos
    {
        private MeshPosition meshPosition;

        private MeshBlockPos(MeshPosition initial)
        {
            this.meshPosition = initial;
        }

        public void setMeshPosition(MeshPosition meshPosition)
        {
            this.meshPosition = meshPosition;
        }

        public MeshPosition getMeshPosition()
        {
            return meshPosition;
        }
    }

    public static final class Builder
    {
        private final Identifier id;
        private final ObjectList<MeshPosition> meshPositions = new ObjectArrayList<>();
        private final Set<Vec3i> usedPos = new ObjectOpenHashSet<>();

        private Builder(Identifier id)
        {
            this.id = id;
        }

        public Builder add(int x, int y, int z, BlockMeshPartType type)
        {
            Vec3i pos = new Vec3i(x, y, z);
            if (!usedPos.add(pos)) throw new IllegalArgumentException("Mesh position " + pos + " already used.");
            MeshPosition position = new MeshPosition(meshPositions.size(), pos, type);
            meshPositions.add(position);

            return this;
        }

        public Builder add(int x, int y, int z)
        {
            return add(x, y, z, BlockMeshPartType.DUMMY);
        }

        public BlockMesh build()
        {
            List<MeshPosition> primaries = meshPositions.stream().filter(o -> o.type() == BlockMeshPartType.PRIMARY).toList();
            Preconditions.checkState(primaries.size() == 1, "Block mesh contains invalid number of primary positions: " + primaries.size());

            return new BlockMesh(id, ObjectLists.unmodifiable(meshPositions), primaries.getFirst());
        }
    }
}