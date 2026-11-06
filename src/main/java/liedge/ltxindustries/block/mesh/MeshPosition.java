package liedge.ltxindustries.block.mesh;

import com.google.common.base.Preconditions;
import liedge.ltxindustries.blockentity.base.IORuleSet;
import liedge.ltxindustries.blockentity.base.IORules;
import liedge.ltxindustries.blockentity.base.ResourceType;
import net.minecraft.core.Vec3i;
import org.jspecify.annotations.Nullable;

public interface MeshPosition
{
    MeshPosition PRIMARY = new PrimaryPos();

    int index();

    Vec3i pos();

    default Vec3i subtract(MeshPosition other)
    {
        return pos().subtract(other.pos());
    }

    default boolean isPort()
    {
        return false;
    }

    default @Nullable IORules getRules(ResourceType type)
    {
        return null;
    }

    final class PrimaryPos implements MeshPosition
    {
        private PrimaryPos() { }

        @Override
        public int index()
        {
            return 0;
        }

        @Override
        public Vec3i pos()
        {
            return Vec3i.ZERO;
        }
    }

    record SecondaryPos(int index, Vec3i pos, @Nullable IORuleSet portRules) implements MeshPosition
    {
        public SecondaryPos
        {
            Preconditions.checkArgument(index > 0, "Invalid index");
            Preconditions.checkArgument(!pos.equals(Vec3i.ZERO), "Invalid pos");
        }

        @Override
        public boolean isPort()
        {
            return portRules != null;
        }

        @Override
        public @Nullable IORules getRules(ResourceType type)
        {
            return portRules != null ? portRules.get(type) : null;
        }
    }
}