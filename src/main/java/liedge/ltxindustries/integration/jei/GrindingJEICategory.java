package liedge.ltxindustries.integration.jei;

import liedge.limacore.recipe.LimaRecipeType;
import liedge.ltxindustries.client.gui.screen.LTXIScreen;
import liedge.ltxindustries.recipe.GrindingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.function.Supplier;

public class GrindingJEICategory extends LTXIJeiCategory<GrindingRecipe>
{
    GrindingJEICategory(IGuiHelper helper, Supplier<LimaRecipeType<GrindingRecipe>> typeSupplier)
    {
        super(helper, typeSupplier, 118, 24);
    }

    @Override
    public void draw(RecipeHolder<GrindingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
    {
        LTXIScreen.blitEmptySlotSprite(guiGraphics, 3, 3);
        LTXIScreen.blitEmptySlotGrid(guiGraphics, 61, 3, 3, 1);
        machineProgressBackground.draw(guiGraphics, 29, 9);
        machineProgress.draw(guiGraphics, 30, 10);
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<GrindingRecipe> holder, GrindingRecipe recipe, IFocusGroup focuses, RegistryAccess registries)
    {
        sizedIngredientsSlot(builder, recipe, 0, 4, 4);

        for (int i = 0; i < recipe.getItemResults().size(); i++)
        {
            itemResultSlot(builder, recipe, i, 62 + i * 18, 4);
        }
    }

    @Override
    public RecipeType<RecipeHolder<GrindingRecipe>> getRecipeType()
    {
        return LTXIJeiPlugin.GRINDING_JEI;
    }
}