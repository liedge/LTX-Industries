package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.menu.UpgradeStationMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class UpgradeStationScreen extends UpgradesConfigScreen<UpgradeStationMenu>
{
    public UpgradeStationScreen(UpgradeStationMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 0);
    }

    @Override
    protected void blitSlotSprites(GuiGraphicsExtractor graphics)
    {
        super.blitSlotSprites(graphics);
        blitOutputSlot(graphics, 21, 62);
    }
}