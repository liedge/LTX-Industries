package liedge.ltxindustries.recipe;

import liedge.limacore.recipe.input.RecipeFluidInput;
import liedge.limacore.recipe.input.RecipeItemInput;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import liedge.ltxindustries.registry.game.LTXIRecipeSerializers;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class GeoSynthesisRecipe extends LTXIRecipe
{
    public GeoSynthesisRecipe(List<RecipeItemInput> itemInputs, List<RecipeFluidInput> fluidInputs, List<ItemResult> itemResults, List<FluidResult> fluidResults, int craftTime, @Nullable Holder<RecipeMode> mode)
    {
        super(itemInputs, fluidInputs, itemResults, fluidResults, craftTime, mode);
    }

    @Override
    public RecipeSerializer<GeoSynthesisRecipe> getSerializer()
    {
        return LTXIRecipeSerializers.GEO_SYNTHESIS.get();
    }

    @Override
    public RecipeType<GeoSynthesisRecipe> getType()
    {
        return LTXIRecipeTypes.GEO_SYNTHESIS.get();
    }
}