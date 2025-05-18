package liedge.limatech.integration.jei;

import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.client.gui.screen.AutoFabricatorScreen;
import liedge.limatech.recipe.FabricatingRecipe;
import liedge.limatech.registry.game.LimaTechBlocks;
import liedge.limatech.registry.game.LimaTechItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;
import java.util.function.Supplier;

public class FabricatingJEICategory extends LimaJEICategory<FabricatingRecipe>
{
    private final IDrawable bpIcon;
    private final IDrawableStatic progressBackground;
    private final IDrawableAnimated progressForeground;

    FabricatingJEICategory(IGuiHelper helper, Supplier<LimaRecipeType<FabricatingRecipe>> typeSupplier)
    {
        super(helper, typeSupplier, AutoFabricatorScreen.TEXTURE, 30, 27, 148, 67);
        this.progressBackground = widgetDrawable(helper, "fabricator_progress_bg", 5, 22).build();
        this.progressForeground = widgetDrawable(helper, "fabricator_progress", 3, 20).buildAnimated(80, IDrawableAnimated.StartDirection.BOTTOM, false);
        this.bpIcon = helper.createDrawableItemStack(LimaTechItems.FABRICATION_BLUEPRINT.toStack());
    }

    @Override
    public RecipeType<RecipeHolder<FabricatingRecipe>> getRecipeType()
    {
        return LimaTechJEIPlugin.FABRICATING_JEI;
    }

    @Override
    public void draw(RecipeHolder<FabricatingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
    {
        progressBackground.draw(guiGraphics, 141, 43);
        progressForeground.draw(guiGraphics, 142, 44);

        if (recipe.value().isAdvancementLocked()) bpIcon.draw(guiGraphics, 90, 46);
    }

    @Override
    public List<Component> getTooltipStrings(RecipeHolder<FabricatingRecipe> holder, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY)
    {
        if (LimaGuiUtil.isMouseWithinArea(mouseX, mouseY, 141, 43, 5, 22))
        {
            return List.of(LimaTechLang.INLINE_ENERGY_REQUIRED_TOOLTIP.translateArgs(LimaMathUtil.FORMAT_COMMA_INT.format(holder.value().getEnergyRequired())).withStyle(LimaTechConstants.REM_BLUE.chatStyle()));
        }
        else if (LimaGuiUtil.isMouseWithinArea(mouseX, mouseY, 90, 46, 16, 16) && holder.value().isAdvancementLocked())
        {
            return List.of(LimaTechLang.FABRICATOR_LOCKED_TOOLTIP.translate().withStyle(LimaTechConstants.HOSTILE_ORANGE.chatStyle()));
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
        builder.addSlot(RecipeIngredientRole.OUTPUT, 123, 46).addItemStack(recipe.getResultItem(null));

        List<SizedIngredient> recipeIngredients = recipe.getRecipeIngredients();
        for (int i = 0; i < recipeIngredients.size(); i++)
        {
            int x = 3 + (i % 8) * 18;
            int y = 3 + (i / 8) * 18;
            sizedIngredientsSlot(builder, recipe, i, x, y);
        }
    }
}