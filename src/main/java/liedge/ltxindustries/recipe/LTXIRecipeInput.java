package liedge.ltxindustries.recipe;

import liedge.limacore.recipe.RecipeInputAccess;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

public record LTXIRecipeInput(@Nullable ResourceHandler<ItemResource> items, @Nullable ResourceHandler<FluidResource> fluids,
                              @Nullable Holder<RecipeMode> mode) implements RecipeInputAccess
{ }