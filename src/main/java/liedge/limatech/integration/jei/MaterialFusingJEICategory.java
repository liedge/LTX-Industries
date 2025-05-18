package liedge.limatech.integration.jei;

import liedge.limacore.recipe.LimaRecipeType;
import liedge.limatech.client.gui.screen.MaterialFusingChamberScreen;
import liedge.limatech.recipe.MaterialFusingRecipe;
import liedge.limatech.registry.game.LimaTechBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;
import java.util.function.Supplier;

public class MaterialFusingJEICategory extends LimaJEICategory<MaterialFusingRecipe>
{
    private final IDrawableStatic progressBackground;
    private final IDrawableAnimated progressForeground;

    MaterialFusingJEICategory(IGuiHelper helper, Supplier<LimaRecipeType<MaterialFusingRecipe>> typeSupplier)
    {
        super(helper, typeSupplier, MaterialFusingChamberScreen.SCREEN_TEXTURE, 40, 25, 92, 38);
        this.progressBackground = widgetDrawable(helper, "crafting_progress_bg", 24, 6).build();
        this.progressForeground = widgetDrawable(helper, "crafting_progress", 22, 4).buildAnimated(100, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<RecipeHolder<MaterialFusingRecipe>> getRecipeType()
    {
        return LimaTechJEIPlugin.MATERIAL_FUSING_JEI;
    }

    @Override
    public void draw(RecipeHolder<MaterialFusingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
    {
        progressBackground.draw(guiGraphics, 41, 16);
        progressForeground.draw(guiGraphics, 42, 17);
    }

    @Override
    protected ItemStack categoryIconItemStack()
    {
        return LimaTechBlocks.MATERIAL_FUSING_CHAMBER.toStack();
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<MaterialFusingRecipe> holder, MaterialFusingRecipe recipe, IFocusGroup focuses, RegistryAccess registries)
    {
        List<SizedIngredient> recipeIngredients = recipe.getRecipeIngredients();
        for (int i = 0; i < recipeIngredients.size(); i++)
        {
            switch (i)
            {
                case 0 -> sizedIngredientsSlot(builder, recipe, i, 2, 2);
                case 1 -> sizedIngredientsSlot(builder, recipe, i, 20, 2);
                case 2 -> sizedIngredientsSlot(builder, recipe, i, 11, 20);
            }
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 72, 11).addItemStack(recipe.getResultItem(registries));
    }
}