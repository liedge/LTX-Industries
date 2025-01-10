package liedge.limatech.compat.jei;

import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.client.gui.widget.ScreenWidgetSprites;
import liedge.limatech.recipe.FabricatingRecipe;
import liedge.limatech.registry.LimaTechBlocks;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;
import java.util.function.Supplier;

public class FabricatingJEICategory extends LimaJEICategory<FabricatingRecipe>
{
    private static final ResourceLocation BACKGROUND = LimaTech.RESOURCES.textureLocation("gui", "fabricator_jei");

    private final IDrawableStatic energyBarBackground;
    private final IDrawableAnimated energyBarForeground;

    FabricatingJEICategory(IGuiHelper helper, Supplier<LimaRecipeType<FabricatingRecipe>> typeSupplier)
    {
        super(helper, typeSupplier, BACKGROUND, 0, 0, 112, 82);
        this.energyBarBackground = unmanagedSpriteDrawable(helper, ScreenWidgetSprites.ENERGY_GAUGE_BACKGROUND).build();
        this.energyBarForeground = unmanagedSpriteDrawable(helper, ScreenWidgetSprites.ENERGY_GAUGE_FOREGROUND).buildAnimated(80, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    @Override
    public RecipeType<RecipeHolder<FabricatingRecipe>> getRecipeType()
    {
        return LimaTechJEIPlugin.FABRICATING_JEI;
    }

    @Override
    public void draw(RecipeHolder<FabricatingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
    {
        energyBarBackground.draw(guiGraphics, 11, 5);
        energyBarForeground.draw(guiGraphics, 12, 6);
    }

    @Override
    public List<Component> getTooltipStrings(RecipeHolder<FabricatingRecipe> holder, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY)
    {
        if (LimaGuiUtil.isMouseWithinArea(mouseX, mouseY, 11, 5, 10, 48))
        {
            return List.of(LimaTechLang.FABRICATOR_ENERGY_REQUIRED_TOOLTIP.translateArgs(LimaMathUtil.FORMAT_COMMA_INT.format(holder.value().getEnergyRequired())).withStyle(LimaTechConstants.REM_BLUE.chatStyle()));
        }
        else
        {
            return List.of();
        }
    }

    @Override
    protected ItemStack categoryIconItemStack()
    {
        return LimaTechBlocks.FABRICATOR.toStack();
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<FabricatingRecipe> holder, FabricatingRecipe recipe, IFocusGroup focuses, RegistryAccess registries)
    {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 8, 58).addItemStack(recipe.getResultItem(null));

        List<SizedIngredient> recipeIngredients = recipe.getRecipeIngredients();
        for (int i = 0; i < recipeIngredients.size(); i++)
        {
            int x = 36 + (i % 4) * 18;
            int y = 6 + (i / 4) * 18;
            sizedIngredientsSlot(builder, recipe, i, x, y);
        }
    }
}