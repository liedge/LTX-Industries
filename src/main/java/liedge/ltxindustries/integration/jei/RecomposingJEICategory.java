package liedge.ltxindustries.integration.jei;

import liedge.limacore.recipe.LimaRecipeType;
import liedge.ltxindustries.recipe.RecomposingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.function.Supplier;

public class RecomposingJEICategory extends SingleItemRecipeJEICategory<RecomposingRecipe>
{
    public RecomposingJEICategory(IGuiHelper helper, Supplier<LimaRecipeType<RecomposingRecipe>> typeSupplier)
    {
        super(helper, typeSupplier);
    }

    @Override
    protected ItemStack categoryIconItemStack()
    {
        return LTXIBlocks.RECOMPOSER.toStack();
    }

    @Override
    public RecipeType<RecipeHolder<RecomposingRecipe>> getRecipeType()
    {
        return LTXIJeiPlugin.RECOMPOSING_JEI;
    }
}