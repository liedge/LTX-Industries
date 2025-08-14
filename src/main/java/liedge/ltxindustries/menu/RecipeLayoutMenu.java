package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.capability.fluid.LimaBlockEntityFluidHandler;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.StateBlockRecipeMachineBlockEntity;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import liedge.ltxindustries.menu.layout.RecipeMenuLayout;
import net.minecraft.world.entity.player.Inventory;

public final class RecipeLayoutMenu<CTX extends StateBlockRecipeMachineBlockEntity<?, ?>> extends LTXIMachineMenu.EnergyMachineMenu<CTX>
{
    private final RecipeMenuLayout layout;

    public RecipeLayoutMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext, RecipeMenuLayout layout)
    {
        super(type, containerId, inventory, menuContext);
        this.layout = layout;

        for (int i = 0; i < layout.itemInputSlots().size(); i++)
        {
            LayoutSlot slot = layout.itemInputSlots().get(i);
            addSlot(BlockContentsType.INPUT, i, slot.x(), slot.y());
        }

        for (int i = 0; i < layout.itemOutputSlots().size(); i++)
        {
            LayoutSlot slot = layout.itemOutputSlots().get(i);
            addRecipeOutputSlot(i, slot.x(), slot.y(), menuContext.getRecipeCheck().getRecipeType());
        }

        for (int i = 0; i < layout.fluidInputSlots().size(); i++)
        {
            LayoutSlot slot = layout.fluidInputSlots().get(i);
            addFluidSlot(menuContext.getFluidHandlerOrThrow(BlockContentsType.INPUT), i, slot.x(), slot.y());
        }

        for (int i = 0; i < layout.fluidOutputSlots().size(); i++)
        {
            LayoutSlot slot = layout.fluidOutputSlots().get(i);
            addFluidSlot(menuContext.getFluidHandlerOrThrow(BlockContentsType.OUTPUT), i, slot.x(), slot.y(), false);
        }

        addDefaultPlayerInventoryAndHotbar();
    }

    public RecipeMenuLayout getLayout()
    {
        return layout;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
        menuContext.keepTimedProcessPropertiesSynced(collector);
        menuContext.keepEnergyConsumerPropertiesSynced(collector);

        LimaBlockEntityFluidHandler inputFluids = menuContext.getFluidHandler(BlockContentsType.INPUT);
        if (inputFluids != null) inputFluids.syncAllTanks(collector);

        LimaBlockEntityFluidHandler outputFluids = menuContext.getFluidHandler(BlockContentsType.OUTPUT);
        if (outputFluids != null) outputFluids.syncAllTanks(collector);
    }
}