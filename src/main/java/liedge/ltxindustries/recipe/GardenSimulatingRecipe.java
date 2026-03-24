package liedge.ltxindustries.recipe;

import liedge.limacore.recipe.ingredient.LimaSizedFluidIngredient;
import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXIRecipeSerializers;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class GardenSimulatingRecipe extends LTXIRecipe
{
    public GardenSimulatingRecipe(List<LimaSizedItemIngredient> itemIngredients, List<LimaSizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidResult> fluidResults, int craftTime, @Nullable Holder<RecipeMode> mode)
    {
        super(itemIngredients, fluidIngredients, itemResults, fluidResults, craftTime, mode);
    }

    @Override
    public RecipeSerializer<GardenSimulatingRecipe> getSerializer()
    {
        return LTXIRecipeSerializers.GARDEN_SIMULATING.get();
    }

    @Override
    public RecipeType<GardenSimulatingRecipe> getType()
    {
        return LTXIRecipeTypes.GARDEN_SIMULATING.get();
    }

    @Override
    protected ItemLike getWorkstation()
    {
        return LTXIItems.DIGITAL_GARDEN;
    }
}