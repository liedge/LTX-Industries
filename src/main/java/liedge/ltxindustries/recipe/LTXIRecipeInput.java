package liedge.ltxindustries.recipe;

import liedge.limacore.capability.fluid.LimaFluidHandler;
import liedge.limacore.recipe.LimaRecipeInput;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public record LTXIRecipeInput(@Nullable IItemHandler itemContainer, @Nullable LimaFluidHandler fluidContainer, @Nullable Holder<RecipeMode> mode) implements LimaRecipeInput.ContainerWrapper { }