package liedge.ltxindustries.client.gui.layer;

import liedge.limacore.client.gui.HorizontalAlignment;
import liedge.limacore.client.gui.LimaGuiLayer;
import liedge.limacore.client.gui.VerticalAlignment;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.registry.game.LTXIAttachmentTypes;
import liedge.ltxindustries.util.config.LTXIClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

public final class BubbleShieldLayer extends LimaGuiLayer
{
    private static final ResourceLocation DISPLAY_SPRITE = LTXIndustries.RESOURCES.location("hud/shield");
    public static final BubbleShieldLayer BUBBLE_SHIELD_LAYER = new BubbleShieldLayer();

    private BubbleShieldLayer()
    {
        super(LTXIndustries.RESOURCES.location("bubble_shield"));
    }

    @Override
    protected void renderGuiLayer(LocalPlayer player, GuiGraphics graphics, float partialTicks)
    {
        float shieldHealth = player.getData(LTXIAttachmentTypes.BUBBLE_SHIELD).getShieldHealth();

        if (shieldHealth > 0)
        {
            Font font = Minecraft.getInstance().font;
            HorizontalAlignment xAlign = LTXIClientConfig.getShieldHorizontalAlign();
            VerticalAlignment yAlign = LTXIClientConfig.getShieldVerticalAlign();
            int x = xAlign.getAbsoluteX(33, graphics.guiWidth(), LTXIClientConfig.SHIELD_HUD_X_OFFSET.getAsInt());
            int y = yAlign.getAbsoluteY(13, graphics.guiHeight(), LTXIClientConfig.SHIELD_HUD_Y_OFFSET.getAsInt());

            String shieldStr = Integer.toString((int) shieldHealth);
            int shieldX0 = font.width(shieldStr) / 2;
            graphics.blitSprite(DISPLAY_SPRITE, x, y, 33, 13);
            graphics.drawString(font, shieldStr, x + 19 - shieldX0, y + 3, LTXIConstants.BUBBLE_SHIELD_BLUE.argb32(), false);
        }
    }
}