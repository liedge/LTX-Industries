package liedge.limatech.lib.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.List;

public final class SimpleBlockMesh extends BaseBlockMesh
{
    SimpleBlockMesh(List<BlockMeshPosition> meshPositions, BlockMeshPosition primary)
    {
        super(meshPositions, primary);
    }

    @Override
    public boolean isMeshValid(Level level, BlockPos origin, BlockMeshPosition meshPosition, Direction zAxis)
    {
        final int buildLimit = level.getMaxBuildHeight() - 1;
        return meshStream(origin, meshPosition, zAxis).allMatch(mp -> mp.getY() < buildLimit && level.getBlockState(mp).canBeReplaced());
    }
}