package liedge.ltxindustries.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.ItemLikeIconsRenderer;
import liedge.ltxindustries.client.gui.widget.BaseGridRenderable;
import liedge.ltxindustries.client.gui.widget.SubMenuBackButton;
import liedge.ltxindustries.lib.icon.ItemIcon;
import liedge.ltxindustries.lib.icon.ItemLikeIcon;
import liedge.ltxindustries.menu.RecipeModeMenu;
import liedge.ltxindustries.recipe.RecipeMode;
import liedge.ltxindustries.registry.LTXIDataMaps;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class RecipeModeScreen extends LTXIScreen<RecipeModeMenu>
{
    private final List<SelectorOption> options;
    private @Nullable SelectorGrid selectorGrid;

    public RecipeModeScreen(RecipeModeMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT, 18, 0, 0);

        // Options init
        this.options = new ObjectArrayList<>();
        options.add(new SelectorOption(null));

        final Holder<RecipeType<?>> machineRecipeType = menu.menuContext().getRecipeTypeHolder();
        HolderSet<RecipeMode> defaultModes = machineRecipeType.getData(LTXIDataMaps.DEFAULT_RECIPE_MODES);
        if (defaultModes != null)
        {
            defaultModes.stream().limit(23).forEach(holder -> options.add(new SelectorOption(holder)));
        }
    }

    @Override
    protected void addWidgets()
    {
        addRenderableWidget(new SubMenuBackButton(leftPos - leftPadding, topPos + 3, this, RecipeModeMenu.BACK_BUTTON_ID));
        this.selectorGrid = addRenderableOnly(new SelectorGrid(leftPos + 15, topPos + 12, this));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        blitDarkPanel(graphics, 14, 11, 146, 56);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick)
    {
        if (menu.getCarried().isEmpty() && event.button() == InputConstants.MOUSE_BUTTON_LEFT && selectorGrid != null)
        {
            if (selectorGrid.onGridClicked(event.x(), event.y(), 0)) return true;
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int x, int y)
    {
        if (menu.getCarried().isEmpty() && selectorGrid != null)
        {
            selectorGrid.renderTooltips(graphics, x, y);
        }

        super.extractTooltip(graphics, x, y);
    }

    private record SelectorOption(@Nullable Holder<RecipeMode> mode, ItemLikeIcon icon)
    {
        private SelectorOption(@Nullable Holder<RecipeMode> mode)
        {
            this(mode, mode != null ? mode.value().icon() : ItemIcon.of(Items.BARRIER));
        }
    }

    private static class SelectorGrid extends BaseGridRenderable.FixedElements<SelectorOption>
    {
        private final RecipeModeScreen parent;
        private final RecipeModeHolderBlockEntity blockEntity;

        SelectorGrid(int x, int y, RecipeModeScreen parent)
        {
            super(x, y, 18, 18, 8, 3, parent.options);
            this.parent = parent;
            this.blockEntity = parent.menu.menuContext();
        }

        @Override
        public void renderElement(GuiGraphicsExtractor graphics, SelectorOption element, int posX, int posY, int gridIndex, int elementIndex, int mouseX, int mouseY)
        {
            Identifier sprite;
            if (blockEntity.getMode() == element.mode)
            {
                sprite = GRID_UNIT_SELECTED;
            }
            else if (LimaGuiUtil.isMouseWithinArea(mouseX, mouseY, posX, posY, 18, 18))
            {
                sprite = GRID_UNIT_FOCUSED;
            }
            else
            {
                sprite = GRID_UNIT;
            }
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, posX, posY, 18, 18);
            ItemLikeIconsRenderer.render(graphics, element.icon, posX + 1, posY + 1);
        }

        @Override
        public void renderElementTooltip(GuiGraphicsExtractor graphics, SelectorOption element, int mouseX, int mouseY, int gridIndex, int elementIndex)
        {
            Holder<RecipeMode> mode = element.mode;
            Component tooltip = mode != null ? mode.value().title() : LTXILangKeys.NONE_UNIVERSAL_TOOLTIP.translate();
            graphics.setTooltipForNextFrame(tooltip, mouseX, mouseY);
        }

        @Override
        public void onElementClicked(SelectorOption element, double mouseX, double mouseY, int button, int gridIndex, int elementIndex)
        {
            if (blockEntity.getMode() != element.mode)
            {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                parent.sendCustomButtonData(RecipeModeMenu.MODE_SWITCH_BUTTON_ID, Optional.ofNullable(element.mode), LTXINetworkSerializers.RECIPE_MODE);
            }
        }
    }
}