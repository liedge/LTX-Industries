package liedge.ltxindustries.integration.jei;

import liedge.limacore.recipe.LimaRecipeType;
import liedge.ltxindustries.recipe.GrindingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.function.Supplier;

public class GrindingJEICategory extends SingleItemRecipeJEICategory<GrindingRecipe>
{
    public GrindingJEICategory(IGuiHelper helper, Supplier<LimaRecipeType<GrindingRecipe>> typeSupplier)
    {
        super(helper, typeSupplier);
    }

    @Override
    protected ItemStack categoryIconItemStack()
    {
        return LTXIBlocks.GRINDER.toStack();
    }

    @Override
    public RecipeType<RecipeHolder<GrindingRecipe>> getRecipeType()
    {
        return LTXIJeiPlugin.GRINDING_JEI;
    }
}