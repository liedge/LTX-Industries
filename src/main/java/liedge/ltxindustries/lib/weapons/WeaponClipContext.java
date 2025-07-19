package liedge.ltxindustries.lib.weapons;

import liedge.limacore.util.LimaBlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Optional;

public final class WeaponClipContext extends ClipContext
{
    private final double[] collisionBypass;
    private final FluidCollisionPredicate fluidCollision;

    public WeaponClipContext(Vec3 from, Vec3 to, Entity entity, FluidCollisionPredicate fluidCollision, double[] collisionBypass)
    {
        super(from, to, Block.COLLIDER, Fluid.NONE, entity);
        this.fluidCollision = fluidCollision;
        this.collisionBypass = collisionBypass;
    }

    public WeaponClipContext(Vec3 from, Vec3 to, Entity entity, double bypassAmount)
    {
        this(from, to, entity, FluidCollisionPredicate.NONE, new double[]{bypassAmount});
    }

    public WeaponClipContext(Vec3 from, Vec3 to, Entity entity)
    {
        this(from, to, entity, 0d);
    }

    @Override
    public VoxelShape getBlockShape(BlockState blockState, BlockGetter level, BlockPos pos)
    {
        VoxelShape shape = super.getBlockShape(blockState, level, pos);

        if (shape.isEmpty() || collisionBypass[0] <= 0d) return shape;

        boolean pierced = true;

        for (AABB bb : LimaBlockUtil.blockPosShiftedAABBs(shape, pos))
        {
            Optional<Vec3> entry = bb.contains(getFrom()) ? Optional.of(getFrom()) : bb.clip(getFrom(), getTo());
            Optional<Vec3> exit = bb.contains(getTo()) ? Optional.of(getTo()) : bb.clip(getTo(), getFrom());

            if (entry.isPresent() && exit.isPresent())
            {
                double remainingBypass = collisionBypass[0];
                double clipDistance = entry.get().distanceTo(exit.get());

                if (clipDistance <= remainingBypass)
                {
                    collisionBypass[0] = remainingBypass - clipDistance;
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
        return fluidCollision.apply(state, level, pos) ? state.getShape(level, pos) : Shapes.empty();
    }

    @FunctionalInterface
    public interface FluidCollisionPredicate
    {
        FluidCollisionPredicate NONE = (state, level, pos) -> false;
        FluidCollisionPredicate ANY = (state, level, pos) -> true;
        FluidCollisionPredicate SOURCE_ONLY = (state, level, pos) -> state.isSource();

        static FluidCollisionPredicate matchType(FluidType type)
        {
            return (state, level, pos) -> state.getFluidType().equals(type);
        }

        static FluidCollisionPredicate matchTypeSourceOnly(FluidType type)
        {
            return (state, level, pos) -> state.isSource() && state.getFluidType().equals(type);
        }

        static FluidCollisionPredicate matchTag(TagKey<net.minecraft.world.level.material.Fluid> tagKey)
        {
            return (state, level, pos) -> state.is(tagKey);
        }

        static FluidCollisionPredicate matchTagSourceOnly(TagKey<net.minecraft.world.level.material.Fluid> tagKey)
        {
            return (state, level, pos) -> state.isSource() && state.is(tagKey);
        }

        boolean apply(FluidState state, BlockGetter level, BlockPos pos);
    }
}