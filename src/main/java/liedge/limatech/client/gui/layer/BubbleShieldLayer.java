package liedge.limatech.client.gui.layer;

import liedge.limacore.client.gui.LimaGuiLayer;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.registry.game.LimaTechAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

public final class BubbleShieldLayer extends LimaGuiLayer
{
    private static final ResourceLocation DISPLAY_SPRITE = LimaTech.RESOURCES.location("bubble_shield_display");
    public static final BubbleShieldLayer BUBBLE_SHIELD_LAYER = new BubbleShieldLayer();

    private BubbleShieldLayer()
    {
        super(LimaTech.RESOURCES.location("bubble_shield"));
    }

    @Override
    protected void renderGuiLayer(LocalPlayer player, GuiGraphics graphics, float partialTicks)
    {
        float shieldHealth = player.getData(LimaTechAttachmentTypes.BUBBLE_SHIELD).getShieldHealth();

        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        if (shieldHealth > 0)
        {
            final int x = (screenWidth / 2) - 91;
            final int y = screenHeight - 62;

            graphics.blitSprite(DISPLAY_SPRITE, x, y, 37, 13);

            graphics.drawString(Minecraft.getInstance().font, Integer.toString((int) shieldHealth), x + 11, y + 3, LimaTechConstants.BUBBLE_SHIELD_GREEN.argb32(), false);
        }
    }
}