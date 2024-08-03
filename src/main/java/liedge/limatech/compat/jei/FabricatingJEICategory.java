package liedge.limatech.compat.jei;

import liedge.limacore.client.gui.LimaRenderable;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.LimaTechLangKeys;
import liedge.limatech.recipe.FabricatingRecipe;
import liedge.limatech.registry.LimaTechBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public class FabricatingJEICategory extends LimaTechJEICategory<FabricatingRecipe>
{
    private static final ResourceLocation BACKGROUND = LimaTech.RESOURCES.textureLocation("gui", "fabricator_jei");

    private final IDrawableAnimated energyBarAnimation;

    FabricatingJEICategory(IGuiHelper helper, LimaRecipeType<FabricatingRecipe> limaRecipeType)
    {
        super(helper, limaRecipeType, BACKGROUND, 0, 0, 112, 82);
        this.energyBarAnimation = helper.drawableBuilder(BACKGROUND, 112, 0, 8, 46).buildAnimated(60, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    @Override
    protected ItemLike categoryIconItem()
    {
        return LimaTechBlocks.FABRICATOR;
    }

    @Override
    public RecipeType<FabricatingRecipe> getRecipeType()
    {
        return LimaTechJEIPlugin.FABRICATING_JEI;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FabricatingRecipe recipe, IFocusGroup focuses)
    {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 8, 58).addItemStack(recipe.getResultItem(localRegistryAccess()));

        List<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++)
        {
            Ingredient ingredient = ingredients.get(i);
            int x = 36 + (i % 4) * 18;
            int y = 6 + (i / 4) * 18;
            builder.addSlot(RecipeIngredientRole.INPUT, x, y).addIngredients(ingredient);
        }
    }

    @Override
    public void draw(FabricatingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
    {
        energyBarAnimation.draw(guiGraphics, 12, 6);
    }

    @Override
    public List<Component> getTooltipStrings(FabricatingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY)
    {
        if (LimaRenderable.checkMouseOver(mouseX, mouseY, 11, 5, 10, 48))
        {
            return List.of(LimaTechLangKeys.FABRICATOR_ENERGY_REQUIRED_TOOLTIP.translateArgs(LimaMathUtil.FORMAT_COMMA_INT.format(recipe.getEnergyRequired())).withStyle(LimaTechConstants.REM_BLUE::applyStyle));
        }
        else
        {
            return List.of();
        }
    }
}