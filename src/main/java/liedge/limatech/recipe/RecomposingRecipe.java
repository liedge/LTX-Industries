package liedge.limatech.recipe;

import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.limacore.recipe.LimaSimpleSizedIngredientRecipe;
import liedge.limatech.registry.LimaTechRecipeSerializers;
import liedge.limatech.registry.LimaTechRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public class RecomposingRecipe extends LimaSimpleSizedIngredientRecipe<LimaRecipeInput>
{
    public RecomposingRecipe(List<SizedIngredient> ingredients, ItemStack result)
    {
        super(ingredients, result);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LimaTechRecipeSerializers.RECOMPOSING.get();
    }

    @Override
    public LimaRecipeType<?> getType()
    {
        return LimaTechRecipeTypes.RECOMPOSING.get();
    }
}