package liedge.limatech.client.gui;

import liedge.limatech.menu.tooltip.GridTooltip;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public final class ClientFabricatorIngredientTooltip extends ClientGridTooltip<Ingredient>
{
    public ClientFabricatorIngredientTooltip(GridTooltip<Ingredient> tooltip)
    {
        super(tooltip);
    }

    @Override
    protected int elementSize()
    {
        return 18;
    }

    @Override
    protected void renderGridElement(Ingredient element, Font font, int rx, int ry, GuiGraphics graphics)
    {
        ItemStack[] items = element.getItems();
        int itemCount = items.length;
        int index;

        if (itemCount == 1)
        {
            index = 0;
        }
        else
        {
            float f = (Util.getMillis() % (1000L * itemCount)) / (1000f * itemCount);
            index = Math.min(Mth.floor(f * itemCount), itemCount);
        }

        ItemStack stack = items[index];
        graphics.renderItem(stack, rx, ry);
        graphics.renderItemDecorations(font, stack, rx, ry);
    }
}