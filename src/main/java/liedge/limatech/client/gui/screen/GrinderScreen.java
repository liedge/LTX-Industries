package liedge.limatech.client.gui.screen;

import liedge.limatech.blockentity.GrinderBlockEntity;
import liedge.limatech.menu.GrinderMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GrinderScreen extends SingleItemRecipeScreen<GrinderBlockEntity, GrinderMenu>
{
    public GrinderScreen(GrinderMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
    }
}