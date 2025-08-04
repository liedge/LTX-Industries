package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.menu.TurretMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TurretScreen extends LTXIMachineScreen<TurretMenu<?>>
{
    public TurretScreen(TurretMenu<?> menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, 188);
        this.inventoryLabelY = 95;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
    {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        blitInventoryAndHotbar(guiGraphics, 7, 105);
        blitSlotGrid(guiGraphics, 79, 22, 5, 4);
        blitPowerInSlot(guiGraphics, 7, 52);
    }
}