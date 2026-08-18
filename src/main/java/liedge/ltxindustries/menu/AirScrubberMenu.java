package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.AirScrubberBlockEntity;
import liedge.ltxindustries.menu.layout.RecipeLayouts;
import net.minecraft.world.entity.player.Inventory;

public class AirScrubberMenu extends LTXIMachineMenu<AirScrubberBlockEntity>
{
    public AirScrubberMenu(LimaMenuType<AirScrubberBlockEntity, ?> type, int containerId, Inventory inventory, AirScrubberBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext, true);

        initLayout(RecipeLayouts.AIR_SCRUBBING);
        addDefaultPlayerInventoryAndHotbar();
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergy().syncAllProperties(collector);
        menuContext.keepEnergyConsumerPropertiesSynced(collector);
        menuContext.keepTimedProcessSynced(collector);

        menuContext.getFluidsOrThrow(BlockContentsType.OUTPUT).syncAllProperties(collector);
        collector.register(menuContext.keepRecipeModeSynced());
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        super.defineButtonEventHandlers(builder);
        builder.handleUnitAction(SharedMenuButtons.OPEN_RECIPE_MODES, sender -> SharedMenuButtons.openModesSubMenu(sender, menuContext));
    }
}