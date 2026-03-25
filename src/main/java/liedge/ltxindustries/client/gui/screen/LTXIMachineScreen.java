package liedge.ltxindustries.client.gui.screen;

import liedge.limacore.client.gui.LimaRenderable;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.blockentity.base.UpgradesHolderBlockEntity;
import liedge.ltxindustries.client.gui.widget.EnergyGaugeWidget;
import liedge.ltxindustries.client.gui.widget.LimaSidebarButton;
import liedge.ltxindustries.client.gui.widget.MachineUpgradesButton;
import liedge.ltxindustries.client.gui.widget.OpenIOControlButton;
import liedge.ltxindustries.menu.LTXIMachineMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public abstract class LTXIMachineScreen<M extends LTXIMachineMenu<?>> extends LTXIScreen<M>
{
    private static final Identifier STATS_WIDGET_SPRITE = LTXIndustries.RESOURCES.id("widget/machine_stats");

    protected LTXIMachineScreen(M menu, Inventory inventory, Component title, int primaryWidth, int primaryHeight)
    {
        super(menu, inventory, title, primaryWidth, primaryHeight, menu.menuContext().hasStatsTooltips() ? 18 : 0, 18, 0);
        this.inventoryLabelY = 73;
    }

    @Override
    protected void addWidgets()
    {
        // Energy bar is in the same place every time, for now
        addRenderableOnly(new EnergyGaugeWidget(menu.menuContext(), leftPos + 10, topPos + 9));

        // Left sidebar widgets
        addRenderableWidget(new MachineUpgradesButton(rightPos, topPos + 3, this));
        if (menu.menuContext().hasStatsTooltips()) addRenderableOnly(new StatsWidget(leftPos - leftPadding, bottomPos - 23, menu.menuContext()));

        // Right sidebar widgets
        int sidebarY = 23;
        for (BlockEntityInputType type : menu.menuContext().getConfigurableInputTypes())
        {
            addRenderableWidget(new OpenIOControlButton(rightPos, topPos + sidebarY, this, LTXIMachineMenu.IO_CONTROLS_BUTTON_ID, type));
            sidebarY += LimaSidebarButton.SIDEBAR_BUTTON_HEIGHT;
        }
    }

    private record StatsWidget(int x, int y, UpgradesHolderBlockEntity blockEntity) implements LimaRenderable
    {
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
        public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
        {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, STATS_WIDGET_SPRITE, x, y, getWidth(), getHeight());
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