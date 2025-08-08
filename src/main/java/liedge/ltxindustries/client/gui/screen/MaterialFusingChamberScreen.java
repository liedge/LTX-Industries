package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.client.gui.widget.MachineProgressWidget;
import liedge.ltxindustries.menu.MaterialFusingChamberMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MaterialFusingChamberScreen extends LTXIMachineScreen<MaterialFusingChamberMenu>
{
    public MaterialFusingChamberScreen(MaterialFusingChamberMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();
        addRenderableOnly(new MachineProgressWidget(menu.menuContext(), leftPos + 101, topPos + 40));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
    {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        blitInventoryAndHotbar(guiGraphics, 7, 83);
        blitPowerInSlot(guiGraphics, 7, 52);
        blitSlotGrid(guiGraphics, 39, 26, 3, 1);
        blitFluidSlot(guiGraphics, 39, 44);
        blitEmptySlot(guiGraphics, 133, 35);
    }
}