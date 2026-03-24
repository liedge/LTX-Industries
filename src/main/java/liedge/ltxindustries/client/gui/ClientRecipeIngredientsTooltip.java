package liedge.ltxindustries.client.gui;

import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;
import liedge.limacore.util.LimaTextUtil;
import liedge.ltxindustries.menu.tooltip.RecipeIngredientsTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;
import org.joml.Matrix3x2fStack;

import java.util.List;
import java.util.Objects;

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
        Level level = Objects.requireNonNull(Minecraft.getInstance().level);
        List<ItemStack> items = element.display().resolveForStacks(SlotDisplayContext.fromLevel(level));

        int itemCount = items.size();
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

        ItemStack stack = items.get(index);
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

        Matrix3x2fStack matrixStack = graphics.pose();
        matrixStack.pushMatrix();

        matrixStack.translate(x + 1, y + 1);
        matrixStack.scale(0.5f);
        graphics.drawString(Minecraft.getInstance().font, overlay, 0, 0, -1, true);

        matrixStack.popMatrix();
    }
}