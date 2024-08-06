package liedge.limatech.client.gui.screen;

import liedge.limatech.blockentity.DigitalFurnaceBlockEntity;
import liedge.limatech.menu.DigitalFurnaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class DigitalFurnaceScreen extends SingleItemRecipeScreen<DigitalFurnaceBlockEntity, DigitalFurnaceMenu>
{
    public DigitalFurnaceScreen(DigitalFurnaceMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
    }
}