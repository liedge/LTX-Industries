package liedge.ltxindustries.client.item;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientExtendedInput;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;

public final class NovaClientItem extends WeaponClientItem
{
    public NovaClientItem()
    {
        super(1, 1, 6, 8);
    }

    @Override
    protected void extractCrosshairSprites(GuiGraphicsExtractor graphics, RenderPipeline pipeline, LocalPlayer player, WeaponItem weaponItem, ClientExtendedInput controls, int x, int y, LimaColor color, float partialTick)
    {
        boolean aiming = LimaEntityUtil.isEntityUsingItem(player, InteractionHand.MAIN_HAND);
        float triggerLerp = controls.lerpTriggerTimer(weaponItem, partialTick);

        float baseBloom;
        if (aiming)
        {
            float f = Math.min(1f, (player.getTicksUsingItem() + partialTick) / 3f);
            baseBloom = 3f - (3f * f);
        }
        else
        {
            baseBloom = 3f;
        }

        float bloom = baseBloom + 7f * LTXIRenderer.linearThresholdCurve(triggerLerp, 0.15f);

        float xl = x - 6 - bloom;
        float yu = y - 6 - bloom;
        float xr = x + 2 + bloom;
        float yd = y + 2 + bloom;

        blitSprite(graphics, pipeline, HEAVY_PISTOL_CROSSHAIR, xl, yu, 5, 5, color);
        blitSpriteMirrorU(graphics, pipeline, HEAVY_PISTOL_CROSSHAIR, xr, yu, 5, 5, color);
        blitSpriteMirrorU(graphics, pipeline, HEAVY_PISTOL_CROSSHAIR, xl, yd, 5, 5, color);
        blitSprite(graphics, pipeline, HEAVY_PISTOL_CROSSHAIR, xr, yd, 5, 5, color);

        if (aiming && triggerLerp == 0) graphics.fill(x, y, x + 1, y + 1, LTXIConstants.LIME_GREEN.argb32());
    }
}