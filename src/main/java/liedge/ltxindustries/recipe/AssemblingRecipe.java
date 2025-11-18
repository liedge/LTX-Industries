package liedge.ltxindustries.recipe;

import liedge.limacore.recipe.ingredient.LimaSizedFluidIngredient;
import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;
import liedge.limacore.recipe.result.ItemResult;
import liedge.ltxindustries.registry.game.LTXIRecipeSerializers;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class AssemblingRecipe extends LTXIRecipe
{
    public AssemblingRecipe(List<LimaSizedItemIngredient> itemIngredients, List<LimaSizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidStack> fluidResults, int craftTime, @Nullable Holder<RecipeMode> mode)
    {
        super(itemIngredients, fluidIngredients, itemResults, fluidResults, craftTime, mode);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LTXIRecipeSerializers.ASSEMBLING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return LTXIRecipeTypes.ASSEMBLING.get();
    }
}