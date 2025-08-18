package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.menu.EnergyCellArrayMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class EnergyCellArrayScreen extends LTXIMachineScreen<EnergyCellArrayMenu>
{
    public EnergyCellArrayScreen(EnergyCellArrayMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
    {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        blitInventoryAndHotbar(guiGraphics, 7, 83);
        blitPowerInSlot(guiGraphics, 7, 52);
        for (int i = 0; i < 4; i++)
        {
            blitSlotSprite(guiGraphics, POWER_OUT_SLOT, 55 + (i * 18), 36);
        }
    }
}