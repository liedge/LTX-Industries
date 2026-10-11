package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.slot.FluidResourceSlot;
import liedge.limacore.transfer.fluid.LimaBlockEntityFluids;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.blockentity.template.BaseRecipeMachineBlockEntity;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import liedge.ltxindustries.menu.layout.RecipeLayout;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.List;

public final class RecipeLayoutMenu<BE extends BaseRecipeMachineBlockEntity<?, ?>> extends LTXIMachineMenu<BE>
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

        addDataWatcher(menuContext.syncItemFilters());
        addDataWatcher(menuContext.syncFluidFilters());

        handleUnitButton(SharedMenuButtons.OPEN_RECIPE_MODES, this::tryOpenModesMenu);
        handleUnitButton(0, _ -> toggleSlotLock());
    }

    public RecipeLayout getLayout()
    {
        return layout;
    }

    private void initLayout(RecipeLayout layout)
    {
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
                    case FLUID_INPUT -> addFluidIOSlot(contentsType, s.x(), s.y(), i, true);
                    case FLUID_OUTPUT -> addFluidIOSlot(contentsType, s.x(), s.y(), i, false);
                }
            }
        }
    }

    private void addFluidIOSlot(BlockContentsType contentsType, int x, int y, int resourceIndex, boolean allowPlace)
    {
        addFluidSlot(si -> new FluidIOSlot(x, y, si, menuContext.getFluidsOrThrow(contentsType), resourceIndex).setAllowPlace(allowPlace));
    }

    private void tryOpenModesMenu(ServerPlayer sender)
    {
        if (menuContext instanceof RecipeModeHolderBlockEntity modeHolder)
        {
            modeHolder.openModesSubMenu(sender);
        }
    }

    private void toggleSlotLock()
    {
        if (menuContext.isLocked())
        {
            menuContext.clearFilters();
        }
        else
        {
            menuContext.setFiltersFromInputs();
        }
    }

    private static class FluidIOSlot extends FluidResourceSlot
    {
        FluidIOSlot(int x, int y, int slotIndex, LimaBlockEntityFluids handler, int resourceIndex)
        {
            super(x, y, slotIndex, handler, resourceIndex);
        }

        @Override
        public boolean canClear(Player player, ItemStack carriedItem)
        {
            return carriedItem.is(Tags.Items.TOOLS_WRENCH) && getAmount() > 0;
        }

        @Override
        public boolean clearFluid(Player player)
        {
            if (getAmount() > 0)
            {
                LimaBlockEntityFluids fluids = (LimaBlockEntityFluids) getResourceHandler();
                fluids.set(getSlotIndex(), FluidResource.EMPTY, 0);

                return true;
            }

            return false;
        }
    }
}