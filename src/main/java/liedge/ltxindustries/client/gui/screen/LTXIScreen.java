package liedge.ltxindustries.client.gui.screen;

import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limacore.menu.LimaMenu;
import liedge.ltxindustries.LTXIConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public abstract class LTXIScreen<M extends LimaMenu<?>> extends LimaMenuScreen<M>
{
    // Textures
    private static final ResourceLocation CONTAINER_BASE_TEXTURE = RESOURCES.textureLocation("gui", "container_base");
    private static final ResourceLocation SLOT_TILE_TEXTURE = RESOURCES.textureLocation("gui", "slots");
    private static final ResourceLocation LIGHT_PANEL_TEXTURE = RESOURCES.textureLocation("gui", "light_panel");
    private static final ResourceLocation DARK_PANEL_TEXTURE = RESOURCES.textureLocation("gui", "dark_panel");

    // Common sprites
    protected static final ResourceLocation EMPTY_SLOT_SPRITE = RESOURCES.location("slot/empty");
    private static final ResourceLocation FLUID_SLOT_SPRITE = RESOURCES.location("slot/fluid");
    private static final ResourceLocation POWER_IN_SLOT = RESOURCES.location("slot/power_in");
    protected static final ResourceLocation POWER_OUT_SLOT = RESOURCES.location("slot/power_out");
    private static final ResourceLocation BIG_OUTPUT_SLOT = RESOURCES.location("slot/big_output");

    // Dimensions
    private static final int BG_CORNER_SIZE = 4;
    private static final int TITLE_BAR_HEIGHT = 15;

    private int titleBarWidth;
    private int titleBarX;

    protected LTXIScreen(M menu, Inventory inventory, Component title, int primaryWidth, int primaryHeight)
    {
        super(menu, inventory, title, primaryWidth, primaryHeight, LTXIConstants.LIME_GREEN.argb32());
        this.topPadding = TITLE_BAR_HEIGHT;
        this.titleLabelY = -9;
    }

    @Override
    protected void positionLabels()
    {
        super.positionLabels();

        this.titleBarWidth = Math.min(primaryWidth - 8, font.width(title) + 18);
        this.titleBarX = (primaryWidth - titleBarWidth) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
    {
        nineSliceBlit(guiGraphics, CONTAINER_BASE_TEXTURE, BG_CORNER_SIZE, 0, 0, primaryWidth, primaryHeight, 16, 16);
        LimaGuiUtil.nineSliceNoBottomBlit(guiGraphics, CONTAINER_BASE_TEXTURE, BG_CORNER_SIZE, leftPos + titleBarX, topPos - topPadding, titleBarWidth, TITLE_BAR_HEIGHT, 16, 16);
    }

    protected void blitSlotGrid(GuiGraphics graphics, int x, int y, int width, int height)
    {
        graphics.blit(SLOT_TILE_TEXTURE, leftPos + x, topPos + y, 0, 0, width * 18, height * 18);
    }

    protected void blitInventoryAndHotbar(GuiGraphics graphics, int x, int y)
    {
        blitSlotGrid(graphics, x, y, 9, 3);
        blitSlotGrid(graphics, x, y + LimaMenu.DEFAULT_INV_HOTBAR_OFFSET, 9, 1);
    }

    protected void blitSprite(GuiGraphics graphics, ResourceLocation spriteLocation, int x, int y, int width, int height)
    {
        graphics.blitSprite(spriteLocation, leftPos + x, topPos + y, width, height);
    }

    protected void blitSlotSprite(GuiGraphics graphics, ResourceLocation slotSprite, int x, int y)
    {
        blitSprite(graphics, slotSprite, x, y, 18, 18);
    }

    protected void blitEmptySlot(GuiGraphics graphics, int x, int y)
    {
        blitSlotSprite(graphics, EMPTY_SLOT_SPRITE, x, y);
    }

    protected void blitFluidSlot(GuiGraphics graphics, int x, int y)
    {
        blitSlotSprite(graphics, FLUID_SLOT_SPRITE, x, y);
    }

    protected void blitPowerInSlot(GuiGraphics graphics, int x, int y)
    {
        blitSlotSprite(graphics, POWER_IN_SLOT, x, y);
    }

    protected void blitOutputSlot(GuiGraphics graphics, int x, int y)
    {
        blitOutputSlotSprite(graphics, leftPos + x, topPos + y);
    }

    protected void blitLightPanel(GuiGraphics graphics, int x, int y, int width, int height)
    {
        nineSliceBlit(graphics, LIGHT_PANEL_TEXTURE, 1, x, y, width, height, 18, 18);
    }

    protected void blitDarkPanel(GuiGraphics graphics, int x, int y, int width, int height)
    {
        nineSliceBlit(graphics, DARK_PANEL_TEXTURE, 1, x, y, width, height, 18, 18);
    }

    protected void nineSliceBlit(GuiGraphics graphics, ResourceLocation textureLocation, int cornerSize, int x, int y, int width, int height, int textureWidth, int textureHeight)
    {
        LimaGuiUtil.nineSliceBlit(graphics, textureLocation, cornerSize, leftPos + x, topPos + y, width, height, textureWidth, textureHeight);
    }

    // Blit helpers (public for JEI use)
    public static void blitEmptySlotSprite(GuiGraphics graphics, int x, int y)
    {
        graphics.blitSprite(EMPTY_SLOT_SPRITE, x, y, 18, 18);
    }

    public static void blitFluidSlotSprite(GuiGraphics graphics, int x, int y)
    {
        graphics.blitSprite(FLUID_SLOT_SPRITE, x, y, 18, 18);
    }

    public static void blitOutputSlotSprite(GuiGraphics graphics, int x, int y)
    {
        graphics.blitSprite(BIG_OUTPUT_SLOT, x, y, 22, 22);
    }

    public static void blitEmptySlotGrid(GuiGraphics graphics, int x, int y, int width, int height)
    {
        graphics.blit(SLOT_TILE_TEXTURE, x, y, 0, 0, width * 18, height * 18);
    }
}