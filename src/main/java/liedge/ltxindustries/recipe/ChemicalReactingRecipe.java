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

public final class ChemicalReactingRecipe extends LTXIRecipe
{
    public ChemicalReactingRecipe(List<RecipeItemInput> itemInputs, List<RecipeFluidInput> fluidInputs, List<ItemResult> itemResults, List<FluidResult> fluidResults, int craftTime, @Nullable Holder<RecipeMode> mode)
    {
        super(itemInputs, fluidInputs, itemResults, fluidResults, craftTime, mode);
    }

    @Override
    public RecipeSerializer<ChemicalReactingRecipe> getSerializer()
    {
        return LTXIRecipeSerializers.CHEMICAL_REACTING.get();
    }

    @Override
    public RecipeType<ChemicalReactingRecipe> getType()
    {
        return LTXIRecipeTypes.CHEMICAL_REACTING.get();
    }
}