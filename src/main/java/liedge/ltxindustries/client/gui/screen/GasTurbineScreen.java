package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.menu.GasTurbineMenu;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GasTurbineScreen extends MachineBaseScreen<GasTurbineMenu>
{
    public GasTurbineScreen(GasTurbineMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, 148);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        blitSlotSprite(graphics, LayoutSlot.FLUID_SLOT_SPRITE, 79, 25);
    }
}