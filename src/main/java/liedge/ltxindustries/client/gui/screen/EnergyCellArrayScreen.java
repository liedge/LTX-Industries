package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.menu.EnergyCellArrayMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class EnergyCellArrayScreen extends LTXIMachineScreen<EnergyCellArrayMenu>
{
    public EnergyCellArrayScreen(EnergyCellArrayMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        blitInventoryAndHotbar(graphics, 7, 83);
        blitPowerInSlot(graphics, 7, 52);
        for (int i = 0; i < 4; i++)
        {
            blitSlotSprite(graphics, POWER_OUT_SLOT, 55 + (i * 18), 36);
        }
    }
}