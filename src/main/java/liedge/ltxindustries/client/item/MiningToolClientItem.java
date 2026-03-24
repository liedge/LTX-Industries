package liedge.ltxindustries.client.item;

import liedge.limacore.client.gui.HorizontalAlignment;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.client.gui.VerticalAlignment;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.gui.layer.EquipmentHUDLayer;
import liedge.ltxindustries.item.tool.ModularEnergyMiningItem;
import liedge.ltxindustries.item.tool.ToolSpeed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public final class MiningToolClientItem implements EquipmentHUDLayer.Renderer
{
    public static final MiningToolClientItem INSTANCE = new MiningToolClientItem();

    private static final Identifier SPRITE = LTXIndustries.RESOURCES.id("hud/tool_speed");

    private MiningToolClientItem() {}

    @Override
    public void renderHUDLayer(GuiGraphics graphics, LocalPlayer player, ItemStack heldItem, HorizontalAlignment xAlign, VerticalAlignment yAlign, int xOffset, int yOffset, float partialTick)
    {
        if (!(heldItem.getItem() instanceof ModularEnergyMiningItem item)) return;

        int x = xAlign.getAbsoluteX(45, graphics.guiWidth(), xOffset);
        int y = yAlign.getAbsoluteY(17, graphics.guiHeight(), yOffset);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE, x, y, 45, 17);

        ToolSpeed speed = item.getToolSpeed(heldItem);
        float fill = 41f * (speed.ordinal() / 4f);
        LimaGuiUtil.submitHorizontalGradient(graphics, RenderPipelines.GUI, x + 2, y + 13, x + 2 + fill, y + 15, 0xff83cf19, 0xffd3ff3c);

        Font font = Minecraft.getInstance().font;

        Component speedText = speed.translate();
        int speedX0 = font.width(speedText) / 2;
        graphics.drawString(font, speedText, x + 23 - speedX0, y + 3, LTXIConstants.LIME_GREEN.argb32(), false);
    }
}