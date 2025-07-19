package liedge.ltxindustries.recipe;

import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.limacore.recipe.LimaSimpleSizedIngredientRecipe;
import liedge.ltxindustries.registry.game.LTXIRecipeSerializers;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public class GrindingRecipe extends LimaSimpleSizedIngredientRecipe<LimaRecipeInput>
{
    public GrindingRecipe(List<SizedIngredient> ingredients, ItemStack result)
    {
        super(ingredients, result);
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