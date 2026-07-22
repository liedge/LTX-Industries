package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.client.gui.widget.MachineProgressWidget;
import liedge.ltxindustries.client.gui.widget.OpenRecipeModesButton;
import liedge.ltxindustries.menu.AirScrubberMenu;
import liedge.ltxindustries.menu.layout.RecipeLayout;
import liedge.ltxindustries.menu.layout.RecipeLayouts;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AirScrubberScreen extends MachineBaseScreen<AirScrubberMenu>
{
    private final RecipeLayout layout = RecipeLayouts.AIR_SCRUBBING;

    public AirScrubberScreen(AirScrubberMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();

        addRenderableOnly(new MachineProgressWidget(menu.menuContext(), leftPos + layout.progressBarX(), topPos + layout.progressBarY()));
        addRenderableWidget(new OpenRecipeModesButton(leftPos - leftPadding, bottomPos - 43, this, menu.menuContext()));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        blitPowerInSlot(graphics, 7, 52);
        RecipeLayoutScreen.renderLayout(graphics, leftPos, topPos, layout);
    }
}