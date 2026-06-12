package liedge.ltxindustries.item;

import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.util.LimaCoreObjects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.ItemAccessFluidHandler;
import org.jspecify.annotations.Nullable;

public interface FluidHolderItem extends ItemLike
{
    static @Nullable ResourceHandler<FluidResource> createItemFluids(ItemStack stack, ItemAccess context)
    {
        FluidHolderItem item = LimaCoreObjects.cast(FluidHolderItem.class, stack.getItem(), "Not a fluid holder item");
        return item.getFluids(stack, context);
    }

    int getFluidCapacity(ItemStack stack);

    default @Nullable ResourceHandler<FluidResource> getFluids(ItemStack stack, ItemAccess access)
    {
        int capacity = getFluidCapacity(stack);
        if (capacity <= 0) return null;

        return new ItemAccessFluidHandler(access, LimaCoreDataComponents.FLUID_CONTENT.get(), capacity);
    }
}