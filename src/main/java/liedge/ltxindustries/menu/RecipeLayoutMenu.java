package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.transfer.fluid.LimaBlockEntityFluids;
import liedge.ltxindustries.blockentity.base.EnergyConsumerBlockEntity;
import liedge.ltxindustries.blockentity.base.RecipeMachineBlockEntity;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.blockentity.template.ProductionMachineBlockEntity;
import liedge.ltxindustries.menu.layout.RecipeLayout;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public final class RecipeLayoutMenu<BE extends ProductionMachineBlockEntity & RecipeMachineBlockEntity.TimedRecipe<?,?> & EnergyConsumerBlockEntity> extends LTXIMachineMenu<BE>
{
    private final RecipeLayout layout;

    public RecipeLayoutMenu(MenuType<?> type, int containerId, Inventory inventory, BE menuContext, RecipeLayout layout)
    {
        super(type, containerId, inventory, menuContext);
        this.layout = layout;

        initLayout(layout);

        int playerInvX = (layout.getWidth() - 162) / 2 + 1;
        int playerInvY = layout.getHeight() - 82;
        addPlayerInventoryAndHotbar(playerInvX, playerInvY);

        menuContext.getEnergy().syncAllProperties(this);
        menuContext.keepTimedProcessSynced(this);
        menuContext.keepEnergyConsumerPropertiesSynced(this);

        LimaBlockEntityFluids inputFluids = menuContext.getFluids(BlockContentsType.INPUT);
        if (inputFluids != null) inputFluids.syncAllProperties(this);

        LimaBlockEntityFluids outputFluids = menuContext.getFluids(BlockContentsType.OUTPUT);
        if (outputFluids != null) outputFluids.syncAllProperties(this);

        if (menuContext instanceof RecipeModeHolderBlockEntity modeHolder)
        {
            addDataWatcher(modeHolder.keepRecipeModeSynced());
        }

        handleUnitButton(SharedMenuButtons.OPEN_RECIPE_MODES, this::tryOpenModesMenu);
    }

    public RecipeLayout getLayout()
    {
        return layout;
    }

    private void tryOpenModesMenu(ServerPlayer sender)
    {
        if (menuContext instanceof RecipeModeHolderBlockEntity modeHolder)
        {
            modeHolder.openModesSubMenu(sender);
        }
    }
}