package liedge.limatech.recipe;

import liedge.limatech.registry.LimaTechCrafting;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class FusingRecipe extends BasicMachineRecipe
{
    public FusingRecipe(NonNullList<Ingredient> ingredients, ItemStack result)
    {
        super(ingredients, result);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LimaTechCrafting.FUSING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return LimaTechCrafting.FUSING_TYPE.get();
    }
}