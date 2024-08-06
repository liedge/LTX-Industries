package liedge.limatech.client.gui.screen;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limacore.inventory.menu.LimaMenu;
import liedge.limatech.LimaTechConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class LimaTechSidebarScreen<M extends LimaMenu<?>> extends LimaMenuScreen<M>
{
    protected LimaTechSidebarScreen(M menu, Inventory inventory, Component title, int width, int height, int sidebarWidth)
    {
        super(menu, inventory, title, width + sidebarWidth, height, LimaTechConstants.LIME_GREEN.packedRGB());
        this.bgWidth = width;
    }
}