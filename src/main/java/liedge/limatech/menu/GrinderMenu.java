package liedge.limatech.menu;

import liedge.limacore.inventory.LimaItemStackHandler;
import liedge.limacore.inventory.menu.LimaMenu;
import liedge.limacore.inventory.slot.LimaMenuSlot;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.blockentity.GrinderBlockEntity;
import liedge.limatech.registry.LimaTechCrafting;
import liedge.limatech.registry.LimaTechMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class GrinderMenu extends LimaMenu<GrinderBlockEntity>
{
    public GrinderMenu(int containerId, Inventory inventory, GrinderBlockEntity menuContext)
    {
        super(LimaTechMenus.GRINDER.get(), containerId, inventory, menuContext);

        LimaItemStackHandler machineInventory = menuContext.getItemHandler();
        addSlot(new LimaMenuSlot(machineInventory, 0, 8, 62));
        addSlot(new LimaMenuSlot(machineInventory, 1, 54, 34));
        addRecipeResultSlot(machineInventory, 2, 106, 34, LimaTechCrafting.GRINDING_TYPE);

        addPlayerInventory(8, 84);
        addPlayerHotbar(8, 142);
    }

    @Override
    protected List<LimaDataWatcher<?>> defineDataWatchers(GrinderBlockEntity menuContext)
    {
        return List.of(
                menuContext.getEnergyStorage().createDataWatcher(),
                menuContext.keepMachineProgressSynced());
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        if (index < 2)
        {
            return quickMoveToAllInventory(stack, false);
        }
        else if (index == 2)
        {
            return quickMoveToAllInventory(stack, true);
        }
        else
        {
            if (LimaItemUtil.ENERGY_ITEMS.test(stack))
            {
                return quickMoveToSlot(stack, 0, false);
            }
            else
            {
                return quickMoveToSlot(stack, 1, false);
            }
        }
    }
}