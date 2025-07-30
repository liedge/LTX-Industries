package liedge.ltxindustries.integration.jei;

import liedge.limacore.recipe.LimaRecipeType;
import liedge.ltxindustries.client.gui.screen.LTXIScreen;
import liedge.ltxindustries.recipe.MaterialFusingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;
import java.util.function.Supplier;

public class MaterialFusingJEICategory extends LTXIJeiCategory<MaterialFusingRecipe>
{
    MaterialFusingJEICategory(IGuiHelper helper, Supplier<LimaRecipeType<MaterialFusingRecipe>> typeSupplier)
    {
        super(helper, typeSupplier, 92, 38);
    }

    @Override
    public RecipeType<RecipeHolder<MaterialFusingRecipe>> getRecipeType()
    {
        return LTXIJeiPlugin.MATERIAL_FUSING_JEI;
    }

    @Override
    public void draw(RecipeHolder<MaterialFusingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
    {
        LTXIScreen.blitEmptySlotSprite(guiGraphics, 1, 1);
        LTXIScreen.blitEmptySlotSprite(guiGraphics, 19, 1);
        LTXIScreen.blitEmptySlotSprite(guiGraphics, 10, 19);

        LTXIScreen.blitOutputSlotSprite(guiGraphics, 69, 8);

        machineProgressBackground.draw(guiGraphics, 41, 16);
        machineProgress.draw(guiGraphics, 42, 17);
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<MaterialFusingRecipe> holder, MaterialFusingRecipe recipe, IFocusGroup focuses, RegistryAccess registries)
    {
        List<SizedIngredient> recipeIngredients = recipe.getItemIngredients();
        for (int i = 0; i < recipeIngredients.size(); i++)
        {
            switch (i)
            {
                case 0 -> sizedIngredientsSlot(builder, recipe, i, 2, 2);
                case 1 -> sizedIngredientsSlot(builder, recipe, i, 20, 2);
                case 2 -> sizedIngredientsSlot(builder, recipe, i, 11, 20);
            }
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 72, 11).addItemStack(recipe.getFirstResult().item());
    }
}