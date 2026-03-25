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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public final class BubbleShieldLayer extends LimaGuiLayer
{
    private static final Identifier DISPLAY_SPRITE = LTXIndustries.RESOURCES.id("hud/shield");
    public static final BubbleShieldLayer INSTANCE = new BubbleShieldLayer();

    private BubbleShieldLayer()
    {
        super(LTXIndustries.RESOURCES.id("bubble_shield"));
    }

    @Override
    protected void renderGuiLayer(LocalPlayer player, GuiGraphicsExtractor graphics, float partialTicks)
    {
        float shieldHealth = player.getData(LTXIAttachmentTypes.BUBBLE_SHIELD_HEALTH);

        if (shieldHealth > 0)
        {
            Font font = Minecraft.getInstance().font;
            HorizontalAlignment xAlign = LTXIClientConfig.getShieldHorizontalAlign();
            VerticalAlignment yAlign = LTXIClientConfig.getShieldVerticalAlign();
            int x = xAlign.getAbsoluteX(33, graphics.guiWidth(), LTXIClientConfig.SHIELD_HUD_X_OFFSET.getAsInt());
            int y = yAlign.getAbsoluteY(13, graphics.guiHeight(), LTXIClientConfig.SHIELD_HUD_Y_OFFSET.getAsInt());

            String shieldStr = Integer.toString((int) shieldHealth);
            int shieldX0 = font.width(shieldStr) / 2;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, DISPLAY_SPRITE, x, y, 33, 13);
            graphics.text(font, shieldStr, x + 19 - shieldX0, y + 3, LTXIConstants.BUBBLE_SHIELD_BLUE.argb32(), false);
        }
    }
}