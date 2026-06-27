package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.menu.PortableTankMenu;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PortableTankScreen extends MachineBaseScreen<PortableTankMenu>
{
    public PortableTankScreen(PortableTankMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        blitSlotSprite(graphics, LayoutSlot.FLUID_SLOT_SPRITE, 79, 35);
    }
}