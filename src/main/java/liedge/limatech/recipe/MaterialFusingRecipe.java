package liedge.limatech.recipe;

import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.limacore.recipe.LimaSimpleSizedIngredientRecipe;
import liedge.limatech.registry.LimaTechRecipeSerializers;
import liedge.limatech.registry.LimaTechRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public class MaterialFusingRecipe extends LimaSimpleSizedIngredientRecipe<LimaRecipeInput>
{
    public MaterialFusingRecipe(List<SizedIngredient> ingredients, ItemStack result)
    {
        super(ingredients, result);
    }

    @Override
    public boolean matches(LimaRecipeInput input, Level level)
    {
        return consumeIngredientsLenientSlots(input, true);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LimaTechRecipeSerializers.MATERIAL_FUSING.get();
    }

    @Override
    public LimaRecipeType<?> getType()
    {
        return LimaTechRecipeTypes.MATERIAL_FUSING.get();
    }
}