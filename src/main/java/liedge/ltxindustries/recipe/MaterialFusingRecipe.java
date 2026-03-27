package liedge.ltxindustries.recipe;

import liedge.limacore.recipe.LimaRecipeType;
import liedge.limacore.recipe.input.RecipeFluidInput;
import liedge.limacore.recipe.input.RecipeItemInput;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import liedge.ltxindustries.registry.game.LTXIRecipeSerializers;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class MaterialFusingRecipe extends LTXIRecipe
{
    public MaterialFusingRecipe(List<RecipeItemInput> itemInputs, List<RecipeFluidInput> fluidInputs, List<ItemResult> itemResults, List<FluidResult> fluidResults, int craftTime, @Nullable Holder<RecipeMode> mode)
    {
        super(itemInputs, fluidInputs, itemResults, fluidResults, craftTime, mode);
    }

    @Override
    public RecipeSerializer<MaterialFusingRecipe> getSerializer()
    {
        return LTXIRecipeSerializers.MATERIAL_FUSING.get();
    }

    @Override
    public LimaRecipeType<MaterialFusingRecipe> getType()
    {
        return LTXIRecipeTypes.MATERIAL_FUSING.get();
    }
}