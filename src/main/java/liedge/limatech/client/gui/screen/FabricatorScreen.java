package liedge.limatech.client.gui.screen;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limacore.client.gui.UnmanagedSprite;
import liedge.limacore.client.gui.VariableBarWidget;
import liedge.limacore.lib.energy.LimaEnergyUtil;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.blockentity.FabricatorBlockEntity;
import liedge.limatech.client.gui.widget.EnergyGaugeWidget;
import liedge.limatech.menu.FabricatorMenu;
import liedge.limatech.network.packet.ServerboundFabricatorCraftPacket;
import liedge.limatech.recipe.FabricatingRecipe;
import liedge.limatech.registry.LimaTechCrafting;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static liedge.limacore.client.gui.LimaRenderable.checkMouseOver;
import static liedge.limatech.client.LimaTechLangKeys.*;

public class FabricatorScreen extends LimaMenuScreen<FabricatorMenu>
{
    private static final int SELECTOR_GRID_WIDTH = 5;
    private static final int SELECTOR_GRID_HEIGHT = 4;
    private static final int SELECTOR_GRID_SIZE = SELECTOR_GRID_WIDTH * SELECTOR_GRID_HEIGHT;
    private static final ResourceLocation TEXTURE = LimaTech.RESOURCES.textureLocation("gui", "fabricator");
    private static final UnmanagedSprite SCROLLER_DISABLED_SPRITE = new UnmanagedSprite(TEXTURE, 190, 0, 8, 13);
    private static final UnmanagedSprite SCROLLER_ACTIVE_SPRITE = new UnmanagedSprite(TEXTURE, 198, 0, 8, 13);
    private static final UnmanagedSprite SELECTOR_SPRITE = new UnmanagedSprite(TEXTURE, 190, 35, 18, 18);
    private static final UnmanagedSprite SELECTOR_SELECTED_SPRITE = new UnmanagedSprite(TEXTURE, 208, 35, 18, 18);
    private static final UnmanagedSprite SELECTOR_HOVERED_SPRITE = new UnmanagedSprite(TEXTURE, 226, 35, 18, 18);

    private final Level level;
    private final int recipeRows;
    private final int rowScrollDelta;
    private final List<RecipeHolder<FabricatingRecipe>> recipes;

    private boolean scrolling;
    private int scrollbarPos = 0;
    private int currentScrollRow;
    private int selectedRecipeIndex = -1;

    public FabricatorScreen(FabricatorMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 190, 200, LimaTechConstants.LIME_GREEN.rgb());
        this.level = menu.level();
        this.recipes = level.getRecipeManager().getAllRecipesFor(LimaTechCrafting.FABRICATING_TYPE.get())
                .stream()
                .sorted(Comparator.comparing(t -> t.value().getGroup()))
                .collect(LimaCollectionsUtil.toUnmodifiableObjectArrayList());
        this.recipeRows = LimaCollectionsUtil.splitCollectionToSegments(recipes, SELECTOR_GRID_WIDTH);
        this.rowScrollDelta = 59 / recipeRows;
        this.inventoryLabelX = 14;
        this.inventoryLabelY = imageHeight - 92;
    }

    private int selectorLeft()
    {
        return leftPos + 76;
    }

    private int scrollbarLeft()
    {
        return leftPos + 168;
    }

    private int scrollbarAndSelectorTop()
    {
        return topPos + 32;
    }

    private boolean canScroll()
    {
        return recipeRows > 4;
    }

    private boolean isMouseOverScrollbar(double mouseX, double mouseY)
    {
        return checkMouseOver(mouseX, mouseY, scrollbarLeft(), scrollbarAndSelectorTop(), 8, 72);
    }

    private boolean isMouseOverSelector(double mouseX, double mouseY)
    {
        return checkMouseOver(mouseX, mouseY, selectorLeft(), scrollbarAndSelectorTop(), 90, 72);
    }

    @Override
    protected void addWidgets()
    {
        addRenderableOnly(new EnergyGaugeWidget(menu.menuContext(), leftPos + 10, topPos + 10));
        addRenderableOnly(new ProgressWidget(leftPos + 61, topPos + 83, menu.menuContext()));
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

        // Render scrollbar
        if (canScroll())
        {
            SCROLLER_ACTIVE_SPRITE.singleBlit(graphics, scrollbarLeft(), scrollbarAndSelectorTop() + scrollbarPos);
        }
        else
        {
            SCROLLER_DISABLED_SPRITE.singleBlit(graphics, scrollbarLeft(), scrollbarAndSelectorTop());
        }

        // Render recipe selector grid
        int min = currentScrollRow * SELECTOR_GRID_WIDTH;
        int max = Math.min(min + SELECTOR_GRID_SIZE, recipes.size());

        for (int i = min; i < max; i++)
        {
            int gridIndex = i - min;
            int rx = selectorLeft() + (gridIndex % SELECTOR_GRID_WIDTH) * 18;
            int ry = scrollbarAndSelectorTop() + (gridIndex / SELECTOR_GRID_WIDTH) * 18;

            UnmanagedSprite sprite;
            if (gridIndex == selectedRecipeIndex)
            {
                sprite = SELECTOR_SELECTED_SPRITE;
            }
            else if (checkMouseOver(mouseX, mouseY, rx, ry, 18, 18))
            {
                sprite = SELECTOR_HOVERED_SPRITE;
            }
            else
            {
                sprite = SELECTOR_SPRITE;
            }
            sprite.singleBlit(graphics, rx, ry);

            RecipeHolder<FabricatingRecipe> holder = recipes.get(i);
            ItemStack resultStack = holder.value().getResultItem(level.registryAccess());
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
                int ry = scrollbarAndSelectorTop() + (gridIndex / SELECTOR_GRID_WIDTH) * 18;

                if (checkMouseOver(x, y, rx, ry, 18, 18))
                {
                    RecipeHolder<FabricatingRecipe> holder = recipes.get(i);
                    List<Component> lines = getTooltipFromItem(Minecraft.getInstance(), holder.value().getResultItem(level.registryAccess()));

                    if (gridIndex == selectedRecipeIndex) lines.add(FABRICATOR_CLICK_TO_CRAFT_TOOLTIP.translate().withStyle(LimaTechConstants.LIME_GREEN::applyStyle));

                    lines.add(FABRICATOR_ENERGY_REQUIRED_TOOLTIP.translateArgs(LimaEnergyUtil.formatEnergyWithSuffix(holder.value().getEnergyRequired())).withStyle(LimaTechConstants.REM_BLUE::applyStyle));
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

    // Scrollbar Stuff
    private void tryScrollGrid()
    {
        int newScrollRow = Math.min(scrollbarPos / rowScrollDelta, recipeRows - 1);
        if (newScrollRow != currentScrollRow)
        {
            currentScrollRow = newScrollRow;
            selectedRecipeIndex = -1;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
    {
        boolean result = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);

        if (!result && canScroll() && (isMouseOverSelector(mouseX, mouseY) || isMouseOverScrollbar(mouseX, mouseY)))
        {
            int delta = rowScrollDelta * (int) -Math.signum(scrollY);
            this.scrollbarPos = Mth.clamp(scrollbarPos + delta, 0, 59);
            tryScrollGrid();
        }

        return result;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY)
    {
        super.mouseMoved(mouseX, mouseY);
        if (scrolling)
        {
            int scrollCursorPos = ((int) mouseY - scrollbarAndSelectorTop()) - (13 / 2);
            this.scrollbarPos = Mth.clamp(scrollCursorPos, 0, 59);
            tryScrollGrid();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        if (isMouseOverScrollbar(mouseX, mouseY) && canScroll() && mouseButton == 0)
        {
            scrolling = true;
            return true;
        }
        else if (isMouseOverSelector(mouseX, mouseY) && menu.getCarried().isEmpty() && mouseButton == 0)
        {
            int min = currentScrollRow * SELECTOR_GRID_WIDTH;
            int max = Math.min(min + SELECTOR_GRID_SIZE, recipes.size());

            for (int i = min; i < max; i++)
            {
                int gridIndex = i - min;
                int rx = selectorLeft() + (gridIndex % SELECTOR_GRID_WIDTH) * 18;
                int ry = scrollbarAndSelectorTop() + (gridIndex / SELECTOR_GRID_WIDTH) * 18;

                if (checkMouseOver(mouseX, mouseY, rx, ry, 18, 18))
                {
                    if (selectedRecipeIndex != gridIndex)
                    {
                        selectedRecipeIndex = gridIndex;
                    }
                    else
                    {
                        PacketDistributor.sendToServer(new ServerboundFabricatorCraftPacket(recipes.get(i).id()));
                    }

                    return true;
                }
            }

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        scrolling = false;
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    private static class ProgressWidget extends VariableBarWidget.VerticalBar
    {
        private static final UnmanagedSprite BACKGROUND = new UnmanagedSprite(TEXTURE, 190, 13, 5, 22);
        private static final UnmanagedSprite FOREGROUND = new UnmanagedSprite(TEXTURE, 195, 13, 3, 20);

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
            return blockEntity.getClientProgress() / 100f;
        }

        @Override
        public List<Component> getTooltipLines()
        {
            if (blockEntity.isCrafting())
            {
                return List.of(CRAFTING_PROGRESS_TOOLTIP.translateArgs(blockEntity.getClientProgress()).withStyle(ChatFormatting.GRAY));
            }
            else
            {
                return List.of();
            }
        }
    }
}