package liedge.ltxindustries.menu;

import liedge.limacore.menu.BlockEntityMenu;
import liedge.limacore.menu.BlockEntityMenuProvider;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntity;
import liedge.ltxindustries.blockentity.template.UpgradableBaseBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.registry.game.LTXIMenus;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public abstract class MachineBaseMenu<CTX extends UpgradableBaseBlockEntity> extends BlockEntityMenu<CTX>
{
    protected MachineBaseMenu(MenuType<?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);

        handleUnitButton(SharedMenuButtons.OPEN_UPGRADES, sender -> {
            MenuProvider provider = new BlockEntityMenuProvider(LTXIMenus.MACHINE_UPGRADES, getMenuContext(), LTXILangKeys.GUI_UPGRADES.translate(), false);
            sender.openMenu(provider);
        });

        if (menuContext instanceof ConfigurableIOBlockEntity ioConfigurable)
        {
            handleButton(SharedMenuButtons.OPEN_IO_CONTROLS, LTXINetworkSerializers.MACHINE_INPUT_TYPE, ioConfigurable::openIOControlsSubMenu);
        }
    }
}