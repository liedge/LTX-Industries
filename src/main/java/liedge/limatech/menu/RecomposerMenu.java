package liedge.limatech.menu;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limatech.blockentity.RecomposerBlockEntity;
import net.minecraft.world.entity.player.Inventory;

public class RecomposerMenu extends SingleItemRecipeMenu<RecomposerBlockEntity>
{
    public RecomposerMenu(LimaMenuType<RecomposerBlockEntity, ?> type, int containerId, Inventory inventory, RecomposerBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);
    }
}