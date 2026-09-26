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

        menuContext.getEnergy().syncAllProperties(this);
        menuContext.keepEnergyConsumerPropertiesSynced(this);
        menuContext.keepTimedProcessSynced(this);

        menuContext.getFluidsOrThrow(BlockContentsType.OUTPUT).syncAllProperties(this);
        addDataWatcher(menuContext.keepRecipeModeSynced());
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        super.defineButtonEventHandlers(builder);
        builder.handleUnitAction(SharedMenuButtons.OPEN_RECIPE_MODES, menuContext::openModesSubMenu);
    }
}