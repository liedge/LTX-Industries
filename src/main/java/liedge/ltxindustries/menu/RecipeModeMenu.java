package liedge.ltxindustries.menu;

import liedge.limacore.menu.BlockEntityMenu;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.world.entity.player.Inventory;

public class RecipeModeMenu extends BlockEntityMenu<RecipeModeHolderBlockEntity>
{
    public static final int BACK_BUTTON_ID = 0;
    public static final int MODE_SWITCH_BUTTON_ID = 1;

    public RecipeModeMenu(LimaMenuType<RecipeModeHolderBlockEntity, ?> type, int containerId, Inventory inventory, RecipeModeHolderBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);
        addDefaultPlayerInventoryAndHotbar();
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(menuContext.keepRecipeModeSynced());
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleUnitAction(BACK_BUTTON_ID, menuContext::returnToPrimaryMenuScreen);
        builder.handleAction(MODE_SWITCH_BUTTON_ID, LTXINetworkSerializers.RECIPE_MODE, (sender, optional) -> menuContext.setMode(optional.orElse(null)));
    }
}