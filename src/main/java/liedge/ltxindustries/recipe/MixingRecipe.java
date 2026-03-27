package liedge.ltxindustries.recipe;

import liedge.limacore.recipe.input.RecipeFluidInput;
import liedge.limacore.recipe.input.RecipeItemInput;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import liedge.ltxindustries.registry.game.LTXIRecipeSerializers;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class MixingRecipe extends LTXIRecipe
{
    public MixingRecipe(List<RecipeItemInput> itemInputs, List<RecipeFluidInput> fluidInputs, List<ItemResult> itemResults, List<FluidResult> fluidResults, int craftTime, @Nullable Holder<RecipeMode> mode)
    {
        super(itemInputs, fluidInputs, itemResults, fluidResults, craftTime, mode);
    }

    @Override
    public RecipeSerializer<MixingRecipe> getSerializer()
    {
        return LTXIRecipeSerializers.MIXING.get();
    }

    @Override
    public RecipeType<MixingRecipe> getType()
    {
        return LTXIRecipeTypes.MIXING.get();
    }
}