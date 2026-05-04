package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.client.gui.widget.MachineProgressWidget;
import liedge.ltxindustries.menu.RepairStationMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class RepairStationScreen extends LTXIMachineScreen<RepairStationMenu>
{
    public RepairStationScreen(RepairStationMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();
        addRenderableOnly(new MachineProgressWidget(menu.menuContext(), leftPos + 75, topPos + 39));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        blitInventoryAndHotbar(graphics, 7, 83);
        blitPowerInSlot(graphics, 7, 52);
        blitEmptySlot(graphics, 55, 33);
        blitOutputSlot(graphics, 101, 31);
    }
}