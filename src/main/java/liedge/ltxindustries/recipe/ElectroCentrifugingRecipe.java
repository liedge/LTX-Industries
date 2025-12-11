package liedge.ltxindustries.recipe;

import liedge.limacore.recipe.ingredient.LimaSizedFluidIngredient;
import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import liedge.ltxindustries.registry.game.LTXIRecipeSerializers;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ElectroCentrifugingRecipe extends LTXIRecipe
{
    public ElectroCentrifugingRecipe(List<LimaSizedItemIngredient> itemIngredients, List<LimaSizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidResult> fluidResults, int craftTime, @Nullable Holder<RecipeMode> mode)
    {
        super(itemIngredients, fluidIngredients, itemResults, fluidResults, craftTime, mode);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LTXIRecipeSerializers.ELECTRO_CENTRIFUGING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return LTXIRecipeTypes.ELECTRO_CENTRIFUGING.get();
    }
}