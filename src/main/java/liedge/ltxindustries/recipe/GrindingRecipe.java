package liedge.ltxindustries.recipe;

import liedge.limacore.recipe.ItemResult;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.ltxindustries.registry.game.LTXIRecipeSerializers;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

public class GrindingRecipe extends LTXIRecipe
{
    public GrindingRecipe(List<SizedIngredient> itemIngredients, List<SizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidStack> fluidResults, int craftTime)
    {
        super(itemIngredients, fluidIngredients, itemResults, fluidResults, craftTime);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LTXIRecipeSerializers.GRINDING.get();
    }

    @Override
    public LimaRecipeType<?> getType()
    {
        return LTXIRecipeTypes.GRINDING.get();
    }
}