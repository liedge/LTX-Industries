package liedge.ltxindustries.client.gui.screen;

import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limacore.menu.LimaMenu;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public abstract class LTXIScreen<M extends LimaMenu<?>> extends LimaMenuScreen<M>
{
    // Textures
    private static final Identifier CONTAINER_BASE_TEXTURE = RESOURCES.textureLocation("gui", "container_base");
    private static final Identifier SLOT_TILE_TEXTURE = RESOURCES.textureLocation("gui", "slots");
    private static final Identifier LIGHT_PANEL_TEXTURE = RESOURCES.textureLocation("gui", "light_panel");
    private static final Identifier DARK_PANEL_TEXTURE = RESOURCES.textureLocation("gui", "dark_panel");

    // Common sprites
    private static final Identifier POWER_IN_SLOT = RESOURCES.id("slot/power_in");
    protected static final Identifier POWER_OUT_SLOT = RESOURCES.id("slot/power_out");
    private static final Identifier BIG_OUTPUT_SLOT = RESOURCES.id("slot/big_output");
    static final Identifier GRID_UNIT = RESOURCES.id("widget/grid_unit");
    static final Identifier GRID_UNIT_FOCUSED = RESOURCES.id("widget/grid_unit_focus");
    static final Identifier GRID_UNIT_SELECTED = RESOURCES.id("widget/grid_unit_selected");

    // Dimensions
    private static final int BG_CORNER_SIZE = 4;
    private static final int TITLE_BAR_HEIGHT = 15;

    private int titleBarWidth;
    private int titleBarX;

    protected LTXIScreen(M menu, Inventory inventory, Component title, int primaryWidth, int primaryHeight, int leftPadding, int rightPadding, int bottomPadding)
    {
        super(menu, inventory, title, primaryWidth, primaryHeight, leftPadding, rightPadding, TITLE_BAR_HEIGHT, bottomPadding, LTXIConstants.LIME_GREEN.argb32());

        this.titleLabelY = -9;
        this.inventoryLabelY = primaryHeight - 93;
    }

    @Override
    protected void positionLabels()
    {
        super.positionLabels();

        this.titleBarWidth = Math.min(primaryWidth - 8, font.width(title) + 18);
        this.titleBarX = (primaryWidth - titleBarWidth) / 2;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        nineSliceBlit(graphics, CONTAINER_BASE_TEXTURE, BG_CORNER_SIZE, 0, 0, primaryWidth, primaryHeight, 16, 16);
        LimaGuiUtil.nineSliceNoBottomBlit(graphics, RenderPipelines.GUI_TEXTURED, CONTAINER_BASE_TEXTURE, BG_CORNER_SIZE, leftPos + titleBarX, topPos - topPadding, titleBarWidth, TITLE_BAR_HEIGHT, 16, 16);
    }

    protected void blitSlotGrid(GuiGraphicsExtractor graphics, int x, int y, int width, int height)
    {
        blitEmptySlotGrid(graphics, leftPos + x, topPos + y, width, height);
    }

    protected void blitInventoryAndHotbar(GuiGraphicsExtractor graphics, int x, int y)
    {
        blitSlotGrid(graphics, x, y, 9, 3);
        blitSlotGrid(graphics, x, y + LimaMenu.DEFAULT_INV_HOTBAR_OFFSET, 9, 1);
    }

    protected void blitSprite(GuiGraphicsExtractor graphics, Identifier spriteLocation, int x, int y, int width, int height)
    {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, spriteLocation, leftPos + x, topPos + y, width, height);
    }

    protected void blitSlotSprite(GuiGraphicsExtractor graphics, Identifier slotSprite, int x, int y)
    {
        blitSprite(graphics, slotSprite, x, y, 18, 18);
    }

    protected void blitEmptySlot(GuiGraphicsExtractor graphics, int x, int y)
    {
        blitSlotSprite(graphics, LayoutSlot.ITEM_SLOT_SPRITE, x, y);
    }

    protected void blitPowerInSlot(GuiGraphicsExtractor graphics, int x, int y)
    {
        blitSlotSprite(graphics, POWER_IN_SLOT, x, y);
    }

    protected void blitOutputSlot(GuiGraphicsExtractor graphics, int x, int y)
    {
        blitOutputSlotSprite(graphics, leftPos + x, topPos + y);
    }

    protected void blitLightPanel(GuiGraphicsExtractor graphics, int x, int y, int width, int height)
    {
        nineSliceBlit(graphics, LIGHT_PANEL_TEXTURE, 1, x, y, width, height, 18, 18);
    }

    protected void blitDarkPanel(GuiGraphicsExtractor graphics, int x, int y, int width, int height)
    {
        nineSliceBlit(graphics, DARK_PANEL_TEXTURE, 1, x, y, width, height, 18, 18);
    }

    protected void nineSliceBlit(GuiGraphicsExtractor graphics, Identifier textureLocation, int cornerSize, int x, int y, int width, int height, int textureWidth, int textureHeight)
    {
        LimaGuiUtil.nineSliceBlit(graphics, RenderPipelines.GUI_TEXTURED, textureLocation, cornerSize, leftPos + x, topPos + y, width, height, textureWidth, textureHeight);
    }

    // Blit helpers (public for JEI use)
    public static void blitOutputSlotSprite(GuiGraphicsExtractor graphics, int x, int y)
    {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BIG_OUTPUT_SLOT, x, y, 22, 22);
    }

    public static void blitEmptySlotGrid(GuiGraphicsExtractor graphics, int x, int y, int width, int height)
    {
        graphics.blit(RenderPipelines.GUI_TEXTURED, SLOT_TILE_TEXTURE, x, y, 0f, 0f, width * 18, height * 18, 256, 256);
    }
}