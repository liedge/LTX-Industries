package liedge.limatech.menu;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limatech.blockentity.GrinderBlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class GrinderMenu extends SingleItemRecipeMenu<GrinderBlockEntity>
{
    public GrinderMenu(LimaMenuType<GrinderBlockEntity, ?> type, int containerId, Inventory inventory, GrinderBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);
    }
}