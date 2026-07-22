package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.limacore.transfer.fluid.LimaBlockEntityFluids;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.blockentity.template.BaseRecipeMachineBlockEntity;
import liedge.ltxindustries.menu.layout.RecipeLayout;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;

public final class RecipeLayoutMenu<CTX extends BaseRecipeMachineBlockEntity<?, ?>> extends LTXIMachineMenu<CTX>
{
    private final RecipeLayout layout;

    public RecipeLayoutMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext, RecipeLayout layout)
    {
        super(type, containerId, inventory, menuContext);
        this.layout = layout;

        initLayout(layout);
        addDefaultPlayerInventoryAndHotbar();
    }

    public RecipeLayout getLayout()
    {
        return layout;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergy().syncAllProperties(collector);
        menuContext.keepTimedProcessSynced(collector);
        menuContext.keepEnergyConsumerPropertiesSynced(collector);

        LimaBlockEntityFluids inputFluids = menuContext.getFluids(BlockContentsType.INPUT);
        if (inputFluids != null) inputFluids.syncTanks(collector);

        LimaBlockEntityFluids outputFluids = menuContext.getFluids(BlockContentsType.OUTPUT);
        if (outputFluids != null) outputFluids.syncTanks(collector);

        if (menuContext instanceof RecipeModeHolderBlockEntity modeHolder)
        {
            collector.register(modeHolder.keepRecipeModeSynced());
        }
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        super.defineButtonEventHandlers(builder);
        builder.handleUnitAction(SharedMenuButtons.OPEN_RECIPE_MODES, this::tryOpenModesMenu);
    }

    private void tryOpenModesMenu(ServerPlayer sender)
    {
        if (menuContext instanceof RecipeModeHolderBlockEntity modeHolder)
        {
            SharedMenuButtons.openModesSubMenu(sender, modeHolder);
        }
    }
}