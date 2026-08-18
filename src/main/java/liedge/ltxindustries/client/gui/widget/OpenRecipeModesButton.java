package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.ItemLikeIconsRenderer;
import liedge.ltxindustries.menu.SharedMenuButtons;
import liedge.ltxindustries.recipe.RecipeMode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class OpenRecipeModesButton extends LTXISidebarButton.LeftSided
{
    public static final Identifier MODE_OVERLAY_SPRITE = LTXIndustries.RESOURCES.id("widget/recipe_modes");

    private final LimaMenuScreen<?> parent;
    private final RecipeModeHolderBlockEntity blockEntity;

    public OpenRecipeModesButton(int x, int y, LimaMenuScreen<?> parent, RecipeModeHolderBlockEntity blockEntity)
    {
        super(x, y, Component.empty());
        this.parent = parent;
        this.blockEntity = blockEntity;
    }

    @Override
    protected void onPress()
    {
        parent.sendUnitButtonData(SharedMenuButtons.OPEN_RECIPE_MODES);
    }

    @Override
    protected void extractInnerContents(GuiGraphicsExtractor graphics, int guiX, int guiY)
    {
        Holder<RecipeMode> mode = blockEntity.getMode();
        if (mode == null || ItemLikeIconsRenderer.render(graphics, mode.value().icon(), guiX, guiY) == 0)
        {
            renderSprite(graphics, MODE_OVERLAY_SPRITE, guiX, guiY);
        }
    }

    @Override
    public boolean hasTooltip()
    {
        return true;
    }

    @Override
    public void createWidgetTooltip(TooltipLineConsumer consumer)
    {
        consumer.accept(LTXILangKeys.RECIPE_MODES_TITLE_OR_TOOLTIP.translate());

        Holder<RecipeMode> mode = blockEntity.getMode();
        Component modeComponent = mode != null ? mode.value().title() : LTXILangKeys.NONE_UNIVERSAL_TOOLTIP.translate().withStyle(ChatFormatting.GRAY);
        consumer.accept(LTXILangKeys.RECIPE_MODE_CURRENT_MODE.translateArgs(modeComponent));
    }
}