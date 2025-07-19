package liedge.ltxindustries.block.mesh;

import net.minecraft.core.Vec3i;

public record MeshPosition(int index, Vec3i pos, BlockMeshPartType type)
{
    public Vec3i subtract(MeshPosition other)
    {
        return this.pos.subtract(other.pos);
    }
}