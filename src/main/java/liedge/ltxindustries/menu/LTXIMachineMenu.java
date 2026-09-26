package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.template.LTXIMachineBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public abstract class LTXIMachineMenu<CTX extends LTXIMachineBlockEntity> extends MachineBaseMenu<CTX>
{
    protected LTXIMachineMenu(MenuType<?> type, int containerId, Inventory inventory, CTX menuContext, boolean allowEnergySlotQuickTransfer)
    {
        super(type, containerId, inventory, menuContext);
        addSlot(BlockContentsType.AUXILIARY, LTXIMachineBlockEntity.AUX_ENERGY_ITEM_SLOT, 8, 53, slot -> slot.setQuickTransferTest(_ -> allowEnergySlotQuickTransfer));
    }

    protected LTXIMachineMenu(MenuType<?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        this(type, containerId, inventory, menuContext, true);
    }
}