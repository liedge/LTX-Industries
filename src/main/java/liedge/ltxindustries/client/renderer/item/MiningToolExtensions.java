package liedge.ltxindustries.client.renderer.item;

import liedge.limacore.client.gui.HorizontalAlignment;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.client.gui.VerticalAlignment;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.item.tool.ModularEnergyMiningItem;
import liedge.ltxindustries.item.tool.ToolSpeed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public final class MiningToolExtensions implements IClientItemExtensions, EquipmentHUDRenderer
{
    public static final MiningToolExtensions INSTANCE = new MiningToolExtensions();
    private static final ResourceLocation SPRITE = LTXIndustries.RESOURCES.location("hud/tool_speed");

    private MiningToolExtensions() {}

    @Override
    public void renderHUDLayer(GuiGraphics graphics, LocalPlayer player, ItemStack heldItem, HorizontalAlignment xAlign, VerticalAlignment yAlign, int xOffset, int yOffset, float partialTick)
    {
        if (!(heldItem.getItem() instanceof ModularEnergyMiningItem item)) return;

        Font font = Minecraft.getInstance().font;
        int x = xAlign.getAbsoluteX(45, graphics.guiWidth(), xOffset);
        int y = yAlign.getAbsoluteY(17, graphics.guiHeight(), yOffset);

        graphics.blitSprite(SPRITE, x, y, 45, 17);

        ToolSpeed speed = item.getToolSpeed(heldItem);
        float fill = 41f * (speed.ordinal() / 4f);
        LimaGuiUtil.fillHorizontalGradient(graphics, RenderType.guiOverlay(), x + 2, y + 13, x + 2 + fill, y + 15, 0xff83cf19, 0xffd3ff3c);

        Component speedStr = speed.translate();
        int speedX0 = font.width(speedStr) / 2;
        graphics.drawString(font, speedStr, x + 23 - speedX0, y + 3, LTXIConstants.LIME_GREEN.argb32(), false);
    }
}