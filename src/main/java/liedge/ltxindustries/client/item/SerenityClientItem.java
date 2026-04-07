package liedge.ltxindustries.client.item;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientExtendedInput;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;

public final class SerenityClientItem extends WeaponClientItem
{
    public SerenityClientItem()
    {
        super(5, 5);
    }

    @Override
    protected void extractCrosshairSprites(GuiGraphicsExtractor graphics, RenderPipeline pipeline, LocalPlayer player, WeaponItem weaponItem, ClientExtendedInput controls, int x, int y, LimaColor color, float partialTick)
    {
        float baseBloom;
        if (LimaEntityUtil.isEntityUsingItem(player, InteractionHand.MAIN_HAND))
        {
            float f = Math.min(1f, (player.getTicksUsingItem() + partialTick) / 3f);
            baseBloom = 3f - (3f * f);
        }
        else
        {
            baseBloom = 3f;
        }

        float bloom = baseBloom + 2f * (controls.isTriggerHeld() ? LTXIRenderer.sineAnimationCurve(partialTick) : 0f);

        blitSprite(graphics, pipeline, HOLLOW_DOT, x, y, 5, 5, color);
        blitSprite(graphics, pipeline, CIRCLE_BRACKET, x - 4 - bloom, y - 4, 6, 13, color);
        blitSpriteMirrorU(graphics, pipeline, CIRCLE_BRACKET, x + 3 + bloom, y - 4, 6, 13, color);
    }
}