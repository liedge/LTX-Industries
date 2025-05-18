package liedge.limatech.integration.jei;

import liedge.limacore.recipe.LimaRecipeType;
import liedge.limatech.recipe.RecomposingRecipe;
import liedge.limatech.registry.game.LimaTechBlocks;
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
        return LimaTechBlocks.RECOMPOSER.toStack();
    }

    @Override
    public RecipeType<RecipeHolder<RecomposingRecipe>> getRecipeType()
    {
        return LimaTechJEIPlugin.RECOMPOSING_JEI;
    }
}