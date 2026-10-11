package liedge.ltxindustries.integration.jei;

import liedge.limacore.menu.slot.LimaFluidSlot;
import liedge.ltxindustries.client.gui.screen.RecipeLayoutScreen;
import liedge.ltxindustries.network.packet.ServerboundSetFilterPacket;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.List;

final class InputLockHandler implements IGhostIngredientHandler<RecipeLayoutScreen>
{
    @SuppressWarnings("unchecked")
    @Override
    public <I> List<Target<I>> getTargetsTyped(RecipeLayoutScreen gui, ITypedIngredient<I> ingredient, boolean doStart)
    {
        if (ingredient.getType() == VanillaTypes.ITEM_STACK)
        {
            return gui.getInputSlots().stream().map(s -> (Target<I>) new ItemTarget(gui, s)).toList();
        }
        else if (ingredient.getType() == NeoForgeTypes.FLUID_STACK)
        {
            return gui.getInputFluidSlots().stream().map(s -> (Target<I>) new FluidTarget(gui, s)).toList();
        }
        else
        {
            return List.of();
        }
    }

    @Override
    public void onComplete() { }

    private static class ItemTarget implements Target<ItemStack>
    {
        private final int containerId;
        private final int slot;
        private final Rect2i area;

        ItemTarget(AbstractContainerScreen<?> screen, Slot slot)
        {
            this.containerId = screen.getMenu().containerId;
            this.slot = slot.getSlotIndex();
            this.area = new Rect2i(screen.getLeftPos() + slot.x, screen.getTopPos() + slot.y, 16, 16);
        }

        @Override
        public Rect2i getArea()
        {
            return area;
        }

        @Override
        public void accept(ItemStack ingredient)
        {
            ClientPacketDistributor.sendToServer(new ServerboundSetFilterPacket.ForItems(containerId, slot, ItemResource.of(ingredient)));
        }
    }

    private static class FluidTarget implements Target<FluidStack>
    {
        private final int containerId;
        private final int slot;
        private final Rect2i area;

        FluidTarget(AbstractContainerScreen<?> screen, LimaFluidSlot slot)
        {
            this.containerId = screen.getMenu().containerId;
            this.slot = slot.getSlotIndex();
            this.area = new Rect2i(screen.getLeftPos() + slot.getX(), screen.getTopPos() + slot.getY(), 16, 16);
        }

        @Override
        public Rect2i getArea()
        {
            return area;
        }

        @Override
        public void accept(FluidStack ingredient)
        {
            ClientPacketDistributor.sendToServer(new ServerboundSetFilterPacket.ForFluids(containerId, slot, FluidResource.of(ingredient)));
        }
    }
}