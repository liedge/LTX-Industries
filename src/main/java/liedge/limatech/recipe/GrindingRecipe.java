package liedge.limatech.recipe;

import liedge.limatech.registry.LimaTechCrafting;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class GrindingRecipe extends BasicMachineRecipe
{
    public GrindingRecipe(NonNullList<Ingredient> ingredients, ItemStack result)
    {
        super(ingredients, result);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LimaTechCrafting.GRINDING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return LimaTechCrafting.GRINDING_TYPE.get();
    }
}