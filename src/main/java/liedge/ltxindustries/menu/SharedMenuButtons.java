package liedge.ltxindustries.menu;

import liedge.limacore.menu.LimaMenuProvider;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.registry.game.LTXIMenus;
import net.minecraft.server.level.ServerPlayer;

public final class SharedMenuButtons
{
    private SharedMenuButtons() { }

    public static final int EXIT_SUB_MENU = 100;
    public static final int OPEN_UPGRADES = 101;
    public static final int OPEN_IO_CONTROLS = 102;
    public static final int OPEN_RECIPE_MODES = 103;


    static void openModesSubMenu(ServerPlayer sender, RecipeModeHolderBlockEntity blockEntity)
    {
        LimaMenuProvider.create(LTXIMenus.RECIPE_MODE_SELECT.get(), blockEntity, LTXILangKeys.RECIPE_MODES_TITLE_OR_TOOLTIP.translate(), false)
                .openMenuScreen(sender);
    }
}