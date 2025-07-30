package liedge.ltxindustries.integration.jei;

import liedge.limacore.recipe.LimaRecipeType;
import liedge.limacore.util.LimaTextUtil;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.screen.LTXIScreen;
import liedge.ltxindustries.client.gui.widget.FabricatorProgressWidget;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;
import java.util.function.Supplier;

import static liedge.ltxindustries.LTXIConstants.HOSTILE_ORANGE;
import static liedge.ltxindustries.LTXIConstants.REM_BLUE;

public class FabricatingJEICategory extends LTXIJeiCategory<FabricatingRecipe>
{
    private final IDrawableStatic progressBackground;
    private final IDrawableAnimated progressForeground;

    FabricatingJEICategory(IGuiHelper helper, Supplier<LimaRecipeType<FabricatingRecipe>> typeSupplier)
    {
        super(helper, typeSupplier, 140, 76);
        this.progressBackground = guiSpriteDrawable(helper, FabricatorProgressWidget.BACKGROUND_SPRITE, FabricatorProgressWidget.BACKGROUND_WIDTH, FabricatorProgressWidget.BACKGROUND_HEIGHT).build();
        this.progressForeground = guiSpriteDrawable(helper, FabricatorProgressWidget.FILL_SPRITE, FabricatorProgressWidget.FILL_WIDTH, FabricatorProgressWidget.FILL_HEIGHT).buildAnimated(80, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    @Override
    public RecipeType<RecipeHolder<FabricatingRecipe>> getRecipeType()
    {
        return LTXIJeiPlugin.FABRICATING_JEI;
    }

    @Override
    public void draw(RecipeHolder<FabricatingRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
    {
        LTXIScreen.blitEmptySlotGrid(guiGraphics, 1, 1, 6, 2);
        LTXIScreen.blitEmptySlotGrid(guiGraphics, 1, 37, 4, 1);

        LTXIScreen.blitOutputSlotSprite(guiGraphics, 111, 33);
        progressBackground.draw(guiGraphics, 133, 33);
        progressForeground.draw(guiGraphics, 134, 34);

        FabricatingRecipe recipe = recipeHolder.value();
        Component energyText = LTXILangKeys.INLINE_ENERGY.translateArgs(LimaTextUtil.formatWholeNumber(recipe.getEnergyRequired()));
        guiGraphics.drawString(Minecraft.getInstance().font, energyText, 2, 57, REM_BLUE.argb32(), false);

        if (recipe.isAdvancementLocked())
            guiGraphics.drawString(Minecraft.getInstance().font, LTXILangKeys.ADVANCEMENT_LOCKED_TOOLTIP.translate(), 2, 67, HOSTILE_ORANGE.argb32(), false);
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<FabricatingRecipe> holder, FabricatingRecipe recipe, IFocusGroup focuses, RegistryAccess registries)
    {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 114, 36).addItemStack(recipe.getFabricatingResultItem());

        List<SizedIngredient> recipeIngredients = recipe.getItemIngredients();
        for (int i = 0; i < recipeIngredients.size(); i++)
        {
            int x = 1 + (i % 6) * 18;
            int y = 1 + (i / 6) * 18;
            sizedIngredientsSlot(builder, recipe, i, x, y);
        }
    }
}