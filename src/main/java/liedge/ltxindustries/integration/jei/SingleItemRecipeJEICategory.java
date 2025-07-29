package liedge.ltxindustries.integration.jei;

import liedge.limacore.recipe.LimaRecipeType;
import liedge.limacore.recipe.LimaSimpleSizedIngredientRecipe;
import liedge.ltxindustries.client.gui.screen.LTXIScreen;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.function.Supplier;

public abstract class SingleItemRecipeJEICategory<R extends LimaSimpleSizedIngredientRecipe<?>> extends LTXIJeiCategory<R>
{
    public SingleItemRecipeJEICategory(IGuiHelper helper, Supplier<? extends LimaRecipeType<R>> typeSupplier)
    {
        super(helper, typeSupplier, 76, 26);
    }

    @Override
    public void draw(RecipeHolder<R> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
    {
        LTXIScreen.blitEmptySlotSprite(guiGraphics, 2, 4);
        LTXIScreen.blitOutputSlotSprite(guiGraphics, 52, 2);

        machineProgressBackground.draw(guiGraphics, 24, 10);
        machineProgress.draw(guiGraphics, 25, 11);
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<R> holder, R recipe, IFocusGroup focuses, RegistryAccess registries)
    {
        sizedIngredientsSlot(builder, recipe, 0, 3, 5);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 55, 5).addItemStack(recipe.getResultItem(registries));
    }
}