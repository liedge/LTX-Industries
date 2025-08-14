package liedge.ltxindustries.menu.layout;

import net.minecraft.resources.ResourceLocation;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public record LayoutSlot(int x, int y, ResourceLocation sprite)
{
    public static LayoutSlot itemSlot(int x, int y)
    {
        return new LayoutSlot(x, y, ITEM_SLOT_SPRITE);
    }

    public static LayoutSlot fluidSlot(int x, int y)
    {
        return new LayoutSlot(x, y, FLUID_SLOT_SPRITE);
    }

    public static final ResourceLocation ITEM_SLOT_SPRITE = RESOURCES.location("slot/empty");
    public static final ResourceLocation FLUID_SLOT_SPRITE = RESOURCES.location("slot/fluid");
}