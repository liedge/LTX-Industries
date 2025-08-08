package liedge.ltxindustries.integration.jei;

import liedge.limacore.recipe.LimaRecipeType;
import liedge.ltxindustries.client.gui.screen.LTXIScreen;
import liedge.ltxindustries.recipe.MaterialFusingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.function.Supplier;

public class MaterialFusingJEICategory extends LTXIJeiCategory<MaterialFusingRecipe>
{
    MaterialFusingJEICategory(IGuiHelper helper, Supplier<LimaRecipeType<MaterialFusingRecipe>> typeSupplier)
    {
        super(helper, typeSupplier, 118, 42);
    }

    @Override
    public RecipeType<RecipeHolder<MaterialFusingRecipe>> getRecipeType()
    {
        return LTXIJeiPlugin.MATERIAL_FUSING_JEI;
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<MaterialFusingRecipe> holder, MaterialFusingRecipe recipe, IFocusGroup focuses, RegistryAccess registries)
    {
        sizedIngredientSlotsGrid(builder, recipe, 4, 4, 3);
        if (!recipe.getFluidIngredients().isEmpty()) fluidIngredientSlot(builder, recipe, 0, 4, 22);
        itemResultSlot(builder, recipe, 0, 98, 13);
    }

    @Override
    protected void drawRecipe(RecipeHolder<MaterialFusingRecipe> recipeHolder, IRecipeSlotsView view, GuiGraphics graphics, double mouseX, double mouseY)
    {
        LTXIScreen.blitEmptySlotGrid(graphics, 3, 3, 3, 1);
        LTXIScreen.blitFluidSlotSprite(graphics, 3, 21);
        LTXIScreen.blitEmptySlotSprite(graphics, 97, 12);

        machineProgressBackground.draw(graphics, 65, 18);
        machineProgress.draw(graphics, 66, 19);
    }
}