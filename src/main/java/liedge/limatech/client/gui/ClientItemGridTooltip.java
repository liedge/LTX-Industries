package liedge.limatech.client.gui;

import liedge.limatech.menu.tooltip.GridTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public final class ClientItemGridTooltip extends ClientGridTooltip<ItemStack>
{
    public ClientItemGridTooltip(GridTooltip<ItemStack> tooltip)
    {
        super(tooltip);
    }

    @Override
    protected int elementSize()
    {
        return 18;
    }

    @Override
    protected void renderGridElement(ItemStack element, Font font, int rx, int ry, GuiGraphics graphics)
    {
        graphics.renderItem(element, rx, ry);
        graphics.renderItemDecorations(font, element, rx, ry);
    }
}