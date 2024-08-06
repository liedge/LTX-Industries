package liedge.limatech.recipe;

import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.limacore.recipe.LimaSimpleRecipe;
import liedge.limatech.registry.LimaTechRecipeSerializers;
import liedge.limatech.registry.LimaTechRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class GrindingRecipe extends LimaSimpleRecipe<LimaRecipeInput>
{
    public GrindingRecipe(NonNullList<Ingredient> ingredients, ItemStack result)
    {
        super(ingredients, result);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LimaTechRecipeSerializers.GRINDING.get();
    }

    @Override
    public LimaRecipeType<?> getType()
    {
        return LimaTechRecipeTypes.GRINDING.get();
    }
}