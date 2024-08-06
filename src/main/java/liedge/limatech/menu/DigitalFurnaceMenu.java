package liedge.limatech.menu;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limatech.blockentity.DigitalFurnaceBlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class DigitalFurnaceMenu extends SingleItemRecipeMenu<DigitalFurnaceBlockEntity>
{
    public DigitalFurnaceMenu(LimaMenuType<DigitalFurnaceBlockEntity, ?> type, int containerId, Inventory inventory, DigitalFurnaceBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);
    }
}