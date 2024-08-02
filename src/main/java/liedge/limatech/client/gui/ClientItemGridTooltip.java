package liedge.limatech.client.gui;

import liedge.limatech.menu.ItemGridTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class ClientItemGridTooltip implements ClientTooltipComponent
{
    private final List<ItemStack> itemStacks;
    private final int maxColumns;
    private final int width;
    private final int height;

    public ClientItemGridTooltip(ItemGridTooltip tooltipData)
    {
        this.itemStacks = tooltipData.itemStacks();
        this.maxColumns = tooltipData.maxColumns();
        this.width = Math.min(maxColumns, itemStacks.size()) * 18;
        this.height = (int) Math.ceil(itemStacks.size() / (double) maxColumns) * 18;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public int getWidth(Font font)
    {
        return width;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics)
    {
        int count = itemStacks.size();

        for (int i = 0; i < count; i++)
        {
            int rx = x + (i % maxColumns) * 18;
            int ry = y + (i / maxColumns) * 18;

            ItemStack stack = itemStacks.get(i);
            graphics.renderItem(stack, rx, ry);
            graphics.renderItemDecorations(font, stack, rx, ry);
        }
    }
}