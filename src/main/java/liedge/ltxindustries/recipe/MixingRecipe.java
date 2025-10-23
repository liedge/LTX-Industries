package liedge.ltxindustries.recipe;

import liedge.limacore.recipe.result.ItemResult;
import liedge.ltxindustries.registry.game.LTXIRecipeSerializers;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class MixingRecipe extends LTXIRecipe
{
    public MixingRecipe(List<SizedIngredient> itemIngredients, List<SizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidStack> fluidResults, int craftTime, @Nullable Holder<RecipeMode> mode)
    {
        super(itemIngredients, fluidIngredients, itemResults, fluidResults, craftTime, mode);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LTXIRecipeSerializers.MIXING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return LTXIRecipeTypes.MIXING.get();
    }
}