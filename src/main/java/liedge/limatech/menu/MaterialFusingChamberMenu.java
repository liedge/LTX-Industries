package liedge.limatech.menu;

import liedge.limacore.inventory.LimaItemStackHandler;
import liedge.limacore.inventory.menu.LimaMenu;
import liedge.limacore.inventory.slot.LimaMenuSlot;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.blockentity.MaterialFusingChamberBlockEntity;
import liedge.limatech.registry.LimaTechCrafting;
import liedge.limatech.registry.LimaTechMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class MaterialFusingChamberMenu extends LimaMenu<MaterialFusingChamberBlockEntity>
{
    public MaterialFusingChamberMenu(int containerId, Inventory inventory, MaterialFusingChamberBlockEntity menuContext)
    {
        super(LimaTechMenus.MATERIAL_FUSING_CHAMBER.get(), containerId, inventory, menuContext);

        LimaItemStackHandler machineInventory = menuContext.getItemHandler();
        addSlot(new LimaMenuSlot(machineInventory, 0, 8, 62));
        addSlot(new LimaMenuSlot(machineInventory, 1, 42, 27));
        addSlot(new LimaMenuSlot(machineInventory, 2, 60, 27));
        addSlot(new LimaMenuSlot(machineInventory, 3, 51, 45));
        addRecipeResultSlot(machineInventory, 4, 112, 36, LimaTechCrafting.FUSING_TYPE);

        addPlayerInventory(8, 84);
        addPlayerHotbar(8, 142);
    }

    @Override
    protected List<LimaDataWatcher<?>> defineDataWatchers(MaterialFusingChamberBlockEntity menuContext)
    {
        return List.of(
                menuContext.getEnergyStorage().createDataWatcher(),
                menuContext.keepMachineProgressSynced());
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        if (index >= 0 && index < 4)
        {
            return quickMoveToAllInventory(stack, false);
        }
        else if (index == 4)
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
                return moveItemStackTo(stack, 1, 4, false);
            }
        }
    }
}