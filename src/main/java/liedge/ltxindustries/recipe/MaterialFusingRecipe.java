package liedge.ltxindustries.recipe;

import liedge.limacore.recipe.ItemResult;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.ltxindustries.registry.game.LTXIRecipeSerializers;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public class MaterialFusingRecipe extends LimaCustomRecipe<LimaRecipeInput>
{
    public MaterialFusingRecipe(List<SizedIngredient> ingredients, List<ItemResult> itemResults)
    {
        super(ingredients, itemResults);
    }

    @Override
    public boolean matches(LimaRecipeInput input, Level level)
    {
        return consumeIngredientsLenientSlots(input, true);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LTXIRecipeSerializers.MATERIAL_FUSING.get();
    }

    @Override
    public LimaRecipeType<?> getType()
    {
        return LTXIRecipeTypes.MATERIAL_FUSING.get();
    }
}