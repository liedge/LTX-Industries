package liedge.ltxindustries.integration.jei;

import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.ItemLikeIconsRenderer;
import liedge.ltxindustries.recipe.RecipeMode;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

record RecipeModeWidget(ScreenPosition position, IDrawable background, IDrawable overlay, @Nullable Holder<RecipeMode> mode) implements IRecipeWidget
{
    @Override
    public ScreenPosition getPosition()
    {
        return position;
    }

    @Override
    public void drawWidget(GuiGraphicsExtractor graphics, double mouseX, double mouseY)
    {
        background.draw(graphics, -1, -1);

        if (mode == null || ItemLikeIconsRenderer.render(graphics, mode.value().icon(), 0, 0) == 0)
            overlay.draw(graphics, 0, 0);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, double mouseX, double mouseY)
    {
        if (LimaGuiUtil.isMouseWithinArea(mouseX, mouseY, 0, 0, 16, 16))
        {
            if (mode != null)
                tooltip.add(LTXILangKeys.JEI_RECIPE_MODE_NEEDED.translateArgs(mode.value().title()));
            else
                tooltip.add(LTXILangKeys.JEI_NO_RECIPE_MODE_NEEDED.translate().withStyle(ChatFormatting.GRAY));
        }
    }
}