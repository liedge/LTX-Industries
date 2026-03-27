package liedge.ltxindustries.client.gui;

import liedge.limacore.client.LimaClientRecipes;
import liedge.limacore.client.LimaCoreClient;
import liedge.limacore.util.LimaTextUtil;
import liedge.ltxindustries.menu.tooltip.FabricatingInputsTooltip;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.joml.Matrix3x2fStack;

import java.util.List;

public final class ClientFabricatingInputsTooltip extends SquareElementGridTooltip<LimaClientRecipes.CachedDisplay, FabricatingInputsTooltip>
{
    private static List<LimaClientRecipes.CachedDisplay> extractElements(ResourceKey<Recipe<?>> key)
    {
        return LimaCoreClient.getClientRecipes().getDisplaysForTooltips(LTXIRecipeTypes.FABRICATING, key);
    }

    public ClientFabricatingInputsTooltip(FabricatingInputsTooltip data)
    {
        super(data, extractElements(data.key()), 18);
    }

    @Override
    protected void extractElementImage(GuiGraphicsExtractor graphics, Font font, LimaClientRecipes.CachedDisplay element, int index, int x, int y)
    {
        List<ItemStack> stacks = element.stacks();

        int itemCount = stacks.size();
        int stackIndex;

        if (itemCount == 1)
        {
            stackIndex = 0;
        }
        else
        {
            float delta = (Util.getMillis() % (1000L * itemCount)) / (1000f * itemCount);
            stackIndex = Math.clamp(Mth.floor(delta * itemCount), 0, itemCount);
        }

        ItemStack stack = stacks.get(stackIndex);

        int stackX = x + 1;
        int stackY = y + 1;

        graphics.fakeItem(stack, stackX, stackY);
        graphics.itemDecorations(font, stack, stackX, stackY);

        float chance = element.consumeChance();
        if (chance < 1f) extractChanceOverlay(graphics, font, stackX, stackY, chance);
    }

    private void extractChanceOverlay(GuiGraphicsExtractor graphics, Font font, int stackX, int stackY, float chance)
    {
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

        matrixStack.translate(stackX, stackY);
        matrixStack.scale(0.5f);

        graphics.text(font, overlay, 0, 0, -1, true);

        matrixStack.popMatrix();
    }
}