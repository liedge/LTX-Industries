package liedge.limatech.menu;

import liedge.limacore.inventory.menu.LimaItemHandlerMenu;
import liedge.limacore.inventory.menu.LimaMenuProvider;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limatech.blockentity.base.UpgradableMachineBlockEntity;
import liedge.limatech.registry.game.LimaTechMenus;
import net.minecraft.world.entity.player.Inventory;

public abstract class UpgradableMachineMenu<CTX extends UpgradableMachineBlockEntity> extends LimaItemHandlerMenu<CTX>
{
    public static final int UPGRADES_BUTTON_ID = 0;

    protected UpgradableMachineMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleUnitAction(UPGRADES_BUTTON_ID, sender -> LimaMenuProvider.openStandaloneMenu(sender, LimaTechMenus.MACHINE_UPGRADES.get(), menuContext));
    }
}