package liedge.limatech.client.gui.screen;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limacore.client.gui.LimaRenderable;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.blockentity.base.UpgradableMachineBlockEntity;
import liedge.limatech.client.gui.widget.LimaSidebarButton;
import liedge.limatech.client.gui.widget.LimaWidgetSprites;
import liedge.limatech.client.gui.widget.MachineUpgradesButton;
import liedge.limatech.menu.UpgradableMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class UpgradableMachineScreen<M extends UpgradableMachineMenu<?>> extends LimaMenuScreen<M>
{
    private static final ResourceLocation STATS_WIDGET_SPRITE = LimaTech.RESOURCES.location("machine_stats");

    protected UpgradableMachineScreen(M menu, Inventory inventory, Component title, int primaryWidth, int height)
    {
        super(menu, inventory, title, primaryWidth, height, LimaTechConstants.LIME_GREEN.argb32());
        this.rightPadding = 18;
        if (menu.menuContext().hasStatsTooltips()) this.leftPadding = 18;
    }

    @Override
    protected void addWidgets()
    {
        addRenderableWidget(new MachineUpgradesButton(rightPos, topPos + 3, this));
        if (menu.menuContext().hasStatsTooltips()) addRenderableOnly(new StatsWidget(leftPos - leftPadding, bottomPos - 23, menu.menuContext()));
    }

    @Override
    protected void positionLabels()
    {
        super.positionLabels();
        if (alignInventoryLabelRight) this.inventoryLabelX = primaryWidth - 6 - font.width(playerInventoryTitle);
    }

    private static class StatsWidget implements LimaRenderable
    {
        private final int x;
        private final int y;
        private final UpgradableMachineBlockEntity blockEntity;
        private final TextureAtlasSprite sprite;

        private StatsWidget(int x, int y, UpgradableMachineBlockEntity blockEntity)
        {
            this.x = x;
            this.y = y;
            this.blockEntity = blockEntity;
            this.sprite = LimaWidgetSprites.sprite(STATS_WIDGET_SPRITE);
        }

        @Override
        public int getX()
        {
            return x;
        }

        @Override
        public int getY()
        {
            return y;
        }

        @Override
        public int getWidth()
        {
            return LimaSidebarButton.SIDEBAR_BUTTON_WIDTH;
        }

        @Override
        public int getHeight()
        {
            return LimaSidebarButton.SIDEBAR_BUTTON_HEIGHT;
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
        {
            graphics.blit(x, y, 0, getWidth(), getHeight(), sprite);
        }

        @Override
        public boolean hasTooltip()
        {
            return true;
        }

        @Override
        public void createWidgetTooltip(TooltipLineConsumer consumer)
        {
            blockEntity.appendStatsTooltips(consumer);
        }
    }
}