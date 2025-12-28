package liedge.ltxindustries.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;
import liedge.limacore.util.LimaTextUtil;
import liedge.ltxindustries.menu.tooltip.RecipeIngredientsTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public final class ClientRecipeIngredientsTooltip extends ClientGridTooltip<LimaSizedItemIngredient>
{
    public ClientRecipeIngredientsTooltip(RecipeIngredientsTooltip tooltip)
    {
        super(tooltip);
    }

    @Override
    protected int elementSize()
    {
        return 18;
    }

    @Override
    protected void renderGridElement(LimaSizedItemIngredient element, Font font, int rx, int ry, GuiGraphics graphics)
    {
        ItemStack[] items = element.getCachedValues();
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
        int x = rx + 1;
        int y = ry + 1;
        graphics.renderItem(stack, x, y);
        graphics.renderItemDecorations(font, stack, x, y);

        if (element.isDeterministic()) drawConsumeChance(graphics, element, x, y);
    }

    private void drawConsumeChance(GuiGraphics graphics, LimaSizedItemIngredient element, int x, int y)
    {
        float chance = element.getConsumeChance();
        Component overlay;

        if (chance == 0)
        {
            overlay = Component.literal("NC").withStyle(ChatFormatting.GREEN);
        }
        else
        {
            overlay = Component.literal(LimaTextUtil.format1PlacePercentage(chance)).withStyle(ChatFormatting.YELLOW);
        }

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();

        poseStack.translate(x + 1, y + 1, 200);
        poseStack.scale(0.5f, 0.5f, 1f);

        graphics.drawString(Minecraft.getInstance().font, overlay, 0, 0, -1, true);

        poseStack.popPose();
    }
}