package liedge.limatech.client.gui.screen;

import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.client.gui.UnmanagedSprite;
import liedge.limacore.client.gui.VariableBarWidget;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limacore.util.LimaRecipesUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.blockentity.FabricatorBlockEntity;
import liedge.limatech.blockentity.io.MachineInputType;
import liedge.limatech.client.gui.widget.EnergyGaugeWidget;
import liedge.limatech.client.gui.widget.OpenIOControlButton;
import liedge.limatech.client.gui.widget.ScrollableGUIElement;
import liedge.limatech.client.gui.widget.ScrollbarWidget;
import liedge.limatech.menu.FabricatorMenu;
import liedge.limatech.recipe.BaseFabricatingRecipe;
import liedge.limatech.registry.LimaTechRecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static liedge.limacore.client.gui.LimaGuiUtil.isMouseWithinArea;
import static liedge.limatech.client.LimaTechLang.*;

public class FabricatorScreen extends LimaMenuScreen<FabricatorMenu> implements ScrollableGUIElement
{
    private static final int SELECTOR_GRID_WIDTH = 5;
    private static final int SELECTOR_GRID_HEIGHT = 4;
    private static final int SELECTOR_GRID_SIZE = SELECTOR_GRID_WIDTH * SELECTOR_GRID_HEIGHT;
    private static final ResourceLocation TEXTURE = LimaTech.RESOURCES.textureLocation("gui", "fabricator");
    private static final UnmanagedSprite SELECTOR_SPRITE = new UnmanagedSprite(TEXTURE, 190, 22, 18, 18);
    private static final UnmanagedSprite SELECTOR_SELECTED_SPRITE = new UnmanagedSprite(TEXTURE, 208, 22, 18, 18);
    private static final UnmanagedSprite SELECTOR_HOVERED_SPRITE = new UnmanagedSprite(TEXTURE, 226, 22, 18, 18);

    private final int recipeRows;
    private final int scrollWheelDelta;
    private final List<RecipeHolder<BaseFabricatingRecipe>> recipes;

    private int currentScrollRow;
    private int selectedRecipeIndex = -1;

    private @Nullable ScrollbarWidget scrollbar;

    public FabricatorScreen(FabricatorMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 205, 200, LimaTechConstants.LIME_GREEN.packedRGB());
        this.bgWidth = 190;
        this.recipes = LimaRecipesUtil.getSortedRecipesForType(menu.level(), LimaTechRecipeTypes.FABRICATING, Comparator.comparing(holder -> holder.value().getGroup()), Comparator.comparing(holder -> holder.id().getPath()));
        this.recipeRows = LimaCollectionsUtil.splitCollectionToSegments(recipes, SELECTOR_GRID_WIDTH);
        this.scrollWheelDelta = 59 / recipeRows;
        this.inventoryLabelX = 14;
        this.inventoryLabelY = 108;
    }

    private int selectorLeft()
    {
        return leftPos + 76;
    }

    private int selectorTop()
    {
        return topPos + 32;
    }

    private boolean isMouseOverSelector(double mouseX, double mouseY)
    {
        return isMouseWithinArea(mouseX, mouseY, selectorLeft(), selectorTop(), 90, 72);
    }

    @Override
    public boolean canScroll()
    {
        return recipeRows > 4;
    }

    @Override
    public void scrollUpdated(int scrollPosition)
    {
        int newScrollRow = Math.min(scrollPosition / scrollWheelDelta, recipeRows - 1);
        if (newScrollRow != currentScrollRow)
        {
            currentScrollRow = newScrollRow;
            selectedRecipeIndex = -1;
        }
    }

    @Override
    protected void addWidgets()
    {
        addRenderableOnly(new EnergyGaugeWidget(menu.menuContext().getEnergyStorage(), leftPos + 10, topPos + 10));
        addRenderableOnly(new ProgressWidget(leftPos + 61, topPos + 83, menu.menuContext()));
        this.scrollbar = addRenderableWidget(new ScrollbarWidget(leftPos + 168, topPos + 32, 72, this));
        addRenderableWidget(new OpenIOControlButton(leftPos + bgWidth, topPos + 3, this, 1, MachineInputType.ITEMS));
    }

    @Override
    public ResourceLocation getBgTexture()
    {
        return TEXTURE;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        super.renderBg(graphics, partialTick, mouseX, mouseY);

        // Render recipe selector grid
        int min = currentScrollRow * SELECTOR_GRID_WIDTH;
        int max = Math.min(min + SELECTOR_GRID_SIZE, recipes.size());

        for (int i = min; i < max; i++)
        {
            int gridIndex = i - min;
            int rx = selectorLeft() + (gridIndex % SELECTOR_GRID_WIDTH) * 18;
            int ry = selectorTop() + (gridIndex / SELECTOR_GRID_WIDTH) * 18;

            UnmanagedSprite sprite;
            if (gridIndex == selectedRecipeIndex)
            {
                sprite = SELECTOR_SELECTED_SPRITE;
            }
            else if (isMouseWithinArea(mouseX, mouseY, rx, ry, 18, 18))
            {
                sprite = SELECTOR_HOVERED_SPRITE;
            }
            else
            {
                sprite = SELECTOR_SPRITE;
            }
            sprite.singleBlit(graphics, rx, ry);

            RecipeHolder<BaseFabricatingRecipe> holder = recipes.get(i);
            ItemStack resultStack = holder.value().getResultItem(null);
            graphics.renderFakeItem(resultStack, rx + 1, ry + 1);
            graphics.renderItemDecorations(font, resultStack, rx + 1, ry + 1);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int x, int y)
    {
        if (menu.getCarried().isEmpty() && isMouseOverSelector(x, y))
        {
            int min = currentScrollRow * SELECTOR_GRID_WIDTH;
            int max = Math.min(min + SELECTOR_GRID_SIZE, recipes.size());

            for (int i = min; i < max; i++)
            {
                int gridIndex = i - min;
                int rx = selectorLeft() + (gridIndex % SELECTOR_GRID_WIDTH) * 18;
                int ry = selectorTop() + (gridIndex / SELECTOR_GRID_WIDTH) * 18;

                if (isMouseWithinArea(x, y, rx, ry, 18, 18))
                {
                    RecipeHolder<BaseFabricatingRecipe> holder = recipes.get(i);
                    List<Component> lines = getTooltipFromItem(Minecraft.getInstance(), holder.value().getResultItem(null));

                    if (gridIndex == selectedRecipeIndex) lines.add(FABRICATOR_CLICK_TO_CRAFT_TOOLTIP.translate().withStyle(LimaTechConstants.LIME_GREEN.chatStyle()));

                    lines.add(FABRICATOR_ENERGY_REQUIRED_TOOLTIP.translateArgs(LimaEnergyUtil.formatEnergyWithSuffix(holder.value().getEnergyRequired())).withStyle(LimaTechConstants.REM_BLUE.chatStyle()));
                    graphics.renderTooltip(font, lines, Optional.of(holder.value().createIngredientTooltip()), x, y);

                    return;
                }
            }
        }
        else
        {
            super.renderTooltip(graphics, x, y);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
    {
        if (isMouseOverSelector(mouseX, mouseY) && scrollbar != null)
        {
            int delta = scrollWheelDelta * (int) -Math.signum(scrollY);
            scrollbar.moveScrollbar(delta);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        if (isMouseOverSelector(mouseX, mouseY) && menu.getCarried().isEmpty() && mouseButton == 0)
        {
            int min = currentScrollRow * SELECTOR_GRID_WIDTH;
            int max = Math.min(min + SELECTOR_GRID_SIZE, recipes.size());

            for (int i = min; i < max; i++)
            {
                int gridIndex = i - min;
                int rx = selectorLeft() + (gridIndex % SELECTOR_GRID_WIDTH) * 18;
                int ry = selectorTop() + (gridIndex / SELECTOR_GRID_WIDTH) * 18;

                if (isMouseWithinArea(mouseX, mouseY, rx, ry, 18, 18))
                {
                    if (selectedRecipeIndex != gridIndex)
                    {
                        selectedRecipeIndex = gridIndex;
                    }
                    else
                    {
                        sendCustomButtonData(0, recipes.get(i).id(), LimaCoreNetworkSerializers.RESOURCE_LOCATION);
                    }

                    return true;
                }
            }

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private static class ProgressWidget extends VariableBarWidget.VerticalBar
    {
        private static final UnmanagedSprite BACKGROUND = new UnmanagedSprite(TEXTURE, 190, 0, 5, 22);
        private static final UnmanagedSprite FOREGROUND = new UnmanagedSprite(TEXTURE, 195, 0, 3, 20);

        private final FabricatorBlockEntity blockEntity;

        ProgressWidget(int x, int y, FabricatorBlockEntity blockEntity)
        {
            super(x, y);
            this.blockEntity = blockEntity;
        }

        @Override
        protected UnmanagedSprite backgroundSprite()
        {
            return BACKGROUND;
        }

        @Override
        protected UnmanagedSprite foregroundSprite()
        {
            return FOREGROUND;
        }

        @Override
        protected float fillPercent()
        {
            return blockEntity.getProcessTimePercent();
        }

        @Override
        public boolean hasTooltip()
        {
            return fillPercent() > 0;
        }

        @Override
        public void createWidgetTooltip(TooltipLineConsumer consumer)
        {
            int fill = (int) (fillPercent() * 100f);
            consumer.accept(CRAFTING_PROGRESS_TOOLTIP.translateArgs(fill).withStyle(ChatFormatting.GRAY));
        }
    }
}