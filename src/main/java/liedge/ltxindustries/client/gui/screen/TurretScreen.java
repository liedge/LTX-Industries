package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.menu.TurretMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TurretScreen extends MachineBaseScreen<TurretMenu<?>>
{
    public TurretScreen(TurretMenu<?> menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, 188);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        blitSlotGrid(graphics, 79, 22, 5, 4);
        blitPowerInSlot(graphics, 7, 52);
    }
}