package liedge.ltxindustries.recipe;

import liedge.limacore.recipe.RecipeInputAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

public record AirScrubbingInput(@Nullable Holder<RecipeMode> mode, BlockPos pos) implements RecipeInputAccess
{
    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public @Nullable ResourceHandler<ItemResource> items()
    {
        return null;
    }

    @Override
    public @Nullable ResourceHandler<FluidResource> fluids()
    {
        return null;
    }
}