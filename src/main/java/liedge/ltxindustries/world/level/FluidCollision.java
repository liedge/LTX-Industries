package liedge.ltxindustries.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidType;

@FunctionalInterface
public interface FluidCollision
{
    FluidCollision NONE = (_, _, _) -> false;
    FluidCollision ANY = (_, _, _) -> true;
    FluidCollision ANY_SOURCE = (state, _, _) -> state.isSource();
    FluidCollision WATER = any(FluidTags.WATER);

    static FluidCollision any(FluidType type)
    {
        return (state, _, _) -> state.getFluidType() == type;
    }

    static FluidCollision any(TagKey<Fluid> tag)
    {
        return (state, _, _) -> state.is(tag);
    }

    static FluidCollision source(FluidType type)
    {
        return (state, _, _) -> state.isSource() && state.getFluidType() == type;
    }

    static FluidCollision source(TagKey<Fluid> tag)
    {
        return (state, _, _) -> state.isSource() && state.is(tag);
    }

    boolean test(FluidState state, BlockGetter level, BlockPos pos);
}