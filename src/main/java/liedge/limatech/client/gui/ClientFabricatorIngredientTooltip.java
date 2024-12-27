package liedge.limatech.client.gui;

import liedge.limatech.menu.tooltip.FabricatorIngredientTooltip;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public final class ClientFabricatorIngredientTooltip extends ClientGridTooltip<SizedIngredient>
{
    public ClientFabricatorIngredientTooltip(FabricatorIngredientTooltip tooltip)
    {
        super(tooltip);
    }

    @Override
    protected int elementSize()
    {
        return 18;
    }

    @Override
    protected void renderGridElement(SizedIngredient element, Font font, int rx, int ry, GuiGraphics graphics)
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