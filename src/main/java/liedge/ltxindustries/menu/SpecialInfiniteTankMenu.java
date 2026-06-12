package liedge.ltxindustries.menu;

import liedge.limacore.menu.LimaMenuType;
import liedge.limacore.menu.slot.LimaFluidSlot;
import liedge.ltxindustries.blockentity.SpecialInfiniteTankBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public final class SpecialInfiniteTankMenu extends MachineBaseMenu<SpecialInfiniteTankBlockEntity>
{
    public SpecialInfiniteTankMenu(LimaMenuType<SpecialInfiniteTankBlockEntity, ?> type, int containerId, Inventory inventory, SpecialInfiniteTankBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);
        addDefaultPlayerInventoryAndHotbar();
        addFluidSlot(si -> new FluidSlot(80, 36, si, menuContext.getInfiniteFluids()));
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector) { }

    private static class FluidSlot extends LimaFluidSlot
    {
        private final FluidResource resource;

        FluidSlot(int x, int y, int slotIndex, ResourceHandler<FluidResource> handler)
        {
            super(x, y, slotIndex);
            this.resource = handler.getResource(0);
        }

        @Override
        public FluidStack getFluid()
        {
            return resource.toStack(Integer.MAX_VALUE);
        }

        @Override
        public void setFluid(FluidStack stack) { }

        @Override
        public FluidResource getFluidResource()
        {
            return resource;
        }

        @Override
        public int getAmount()
        {
            return Integer.MAX_VALUE;
        }

        @Override
        public int getCapacity()
        {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean mayPlace(FluidResource resource)
        {
            return false;
        }

        @Override
        public boolean fillSlotFromItem(ResourceHandler<FluidResource> carriedFluids)
        {
            return false;
        }

        @Override
        public boolean drainSlotIntoItem(ResourceHandler<FluidResource> carriedFluids)
        {
            int inserted = ResourceHandlerUtil.insertStacking(carriedFluids, resource, Integer.MAX_VALUE, null);
            return inserted > 0;
        }
    }
}