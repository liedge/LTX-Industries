package liedge.ltxindustries.client.gui;

import liedge.ltxindustries.menu.tooltip.ItemStacksTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

public final class ClientItemStacksTooltip extends SquareElementGridTooltip<ItemStack, ItemStacksTooltip>
{
    private final boolean decorate;

    public ClientItemStacksTooltip(ItemStacksTooltip data)
    {
        super(data, data.stacks(), 18);
        this.decorate = data.decorate();
    }

    @Override
    protected void extractElementImage(GuiGraphicsExtractor graphics, Font font, ItemStack element, int index, int x, int y)
    {
        int stackX = x + 1;
        int stackY = y + 1;

        graphics.fakeItem(element, stackX, stackY);
        if (decorate) graphics.itemDecorations(font, element, stackX, stackY);
    }
}