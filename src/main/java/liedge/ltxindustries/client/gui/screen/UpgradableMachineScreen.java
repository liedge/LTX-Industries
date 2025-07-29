package liedge.ltxindustries.client.gui.screen;

import liedge.limacore.client.gui.LimaRenderable;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.base.UpgradableMachineBlockEntity;
import liedge.ltxindustries.client.gui.widget.LimaSidebarButton;
import liedge.ltxindustries.client.gui.widget.MachineUpgradesButton;
import liedge.ltxindustries.menu.UpgradableMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class UpgradableMachineScreen<M extends UpgradableMachineMenu<?>> extends LTXIScreen<M>
{
    private static final ResourceLocation STATS_WIDGET_SPRITE = LTXIndustries.RESOURCES.location("widget/machine_stats");

    protected UpgradableMachineScreen(M menu, Inventory inventory, Component title, int primaryWidth, int primaryHeight)
    {
        super(menu, inventory, title, primaryWidth, primaryHeight);

        this.rightPadding = 18;
        if (menu.menuContext().hasStatsTooltips()) this.leftPadding = 18;

        this.inventoryLabelY = 73;
    }

    @Override
    protected void addWidgets()
    {
        addRenderableWidget(new MachineUpgradesButton(rightPos, topPos + 3, this));
        if (menu.menuContext().hasStatsTooltips()) addRenderableOnly(new StatsWidget(leftPos - leftPadding, bottomPos - 23, menu.menuContext()));
    }

    private static class StatsWidget implements LimaRenderable
    {
        private final int x;
        private final int y;
        private final UpgradableMachineBlockEntity blockEntity;

        private StatsWidget(int x, int y, UpgradableMachineBlockEntity blockEntity)
        {
            this.x = x;
            this.y = y;
            this.blockEntity = blockEntity;
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
            graphics.blitSprite(STATS_WIDGET_SPRITE, x, y, getWidth(), getHeight());
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