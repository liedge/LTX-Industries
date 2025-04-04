package liedge.limatech.compat.jei;

import liedge.limacore.recipe.LimaRecipeType;
import liedge.limatech.recipe.GrindingRecipe;
import liedge.limatech.registry.game.LimaTechBlocks;
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
        return LimaTechBlocks.GRINDER.toStack();
    }

    @Override
    public RecipeType<RecipeHolder<GrindingRecipe>> getRecipeType()
    {
        return LimaTechJEIPlugin.GRINDING_JEI;
    }
}