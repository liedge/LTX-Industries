package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.client.gui.widget.MachineProgressWidget;
import liedge.ltxindustries.menu.CookingMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BaseCookingMenuScreen extends LTXIMachineScreen<CookingMachineMenu<?>>
{
    public BaseCookingMenuScreen(CookingMachineMenu<?> menu, Inventory inventory, Component title)
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
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
    {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        blitInventoryAndHotbar(guiGraphics, 7, 83);
        blitPowerInSlot(guiGraphics, 7, 52);
        blitEmptySlot(guiGraphics, 49, 33);
        blitEmptySlot(guiGraphics, 107, 33);
    }
}