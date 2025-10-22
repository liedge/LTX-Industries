package liedge.ltxindustries.recipe;

import liedge.limacore.recipe.result.ItemResult;
import liedge.ltxindustries.registry.game.LTXIRecipeSerializers;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

public final class GardenSimulatingRecipe extends LTXIRecipe
{
    public GardenSimulatingRecipe(List<SizedIngredient> itemIngredients, List<SizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidStack> fluidResults, int craftTime)
    {
        super(itemIngredients, fluidIngredients, itemResults, fluidResults, craftTime);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LTXIRecipeSerializers.GARDEN_SIMULATING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return LTXIRecipeTypes.GARDEN_SIMULATING.get();
    }
}