package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.capability.fluid.LimaBlockEntityFluidHandler;
import liedge.limacore.menu.LimaMenuProvider;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.blockentity.template.BaseRecipeMachineBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import liedge.ltxindustries.menu.layout.RecipeLayout;
import liedge.ltxindustries.registry.game.LTXIMenus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public final class RecipeLayoutMenu<CTX extends BaseRecipeMachineBlockEntity<?, ?>> extends LTXIMachineMenu.EnergyMachineMenu<CTX>
{
    public static final int MODES_OPEN_BUTTON_ID = 2;

    private final RecipeLayout layout;

    public RecipeLayoutMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext, RecipeLayout layout)
    {
        super(type, containerId, inventory, menuContext);
        this.layout = layout;

        for (LayoutSlot.Type slotType : LayoutSlot.Type.values())
        {
            BlockContentsType contentsType = slotType.getContentsType();
            if (contentsType == null) continue;

            List<LayoutSlot> layoutSlots = layout.getSlotsForType(slotType);
            for (int i = 0; i < layoutSlots.size(); i++)
            {
                LayoutSlot s = layoutSlots.get(i);

                switch (slotType)
                {
                    case ITEM_INPUT -> addSlot(contentsType, i, s.x(), s.y());
                    case ITEM_OUTPUT -> addRecipeOutputSlot(i, s.x(), s.y(), menuContext.getRecipeCheck().getRecipeType());
                    case FLUID_INPUT -> addFluidSlot(menuContext.getFluidHandlerOrThrow(contentsType), i, s.x(), s.y(), true);
                    case FLUID_OUTPUT -> addFluidSlot(menuContext.getFluidHandlerOrThrow(contentsType), i, s.x(), s.y(), false);
                }
            }
        }

        addDefaultPlayerInventoryAndHotbar();
    }

    public RecipeLayout getLayout()
    {
        return layout;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
        menuContext.keepTimedProcessSynced(collector);
        menuContext.keepEnergyConsumerPropertiesSynced(collector);

        LimaBlockEntityFluidHandler inputFluids = menuContext.getFluidHandler(BlockContentsType.INPUT);
        if (inputFluids != null) inputFluids.syncAllTanks(collector);

        LimaBlockEntityFluidHandler outputFluids = menuContext.getFluidHandler(BlockContentsType.OUTPUT);
        if (outputFluids != null) outputFluids.syncAllTanks(collector);

        if (menuContext instanceof RecipeModeHolderBlockEntity modeHolder)
        {
            collector.register(modeHolder.keepRecipeModeSynced());
        }
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        super.defineButtonEventHandlers(builder);
        builder.handleUnitAction(MODES_OPEN_BUTTON_ID, this::tryOpenModesMenu);
    }

    private void tryOpenModesMenu(ServerPlayer sender)
    {
        if (menuContext instanceof RecipeModeHolderBlockEntity modeHolder)
        {
            LimaMenuProvider.create(LTXIMenus.RECIPE_MODE_SELECT.get(), modeHolder, LTXILangKeys.RECIPE_MODES_TITLE_OR_TOOLTIP.translate(), false).openMenuScreen(sender);
        }
    }
}