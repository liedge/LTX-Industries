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

public final class MirageClientItem extends AutoWeaponClientItem
{
    public MirageClientItem()
    {
        super(1, 1, 1, 5);
    }

    @Override
    protected void extractCrosshairSprites(GuiGraphicsExtractor graphics, RenderPipeline pipeline, LocalPlayer player, WeaponItem weaponItem, ClientExtendedInput controls, int x, int y, LimaColor color, float partialTick)
    {
        float baseBloom;
        if (LimaEntityUtil.isEntityUsingItem(player, InteractionHand.MAIN_HAND))
        {
            float f = Math.min(1f, (player.getTicksUsingItem() + partialTick) / 3f);
            baseBloom = 4f - (4f * f);
        }
        else
        {
            baseBloom = 4f;
        }

        float bloom = baseBloom + 1.5f * LTXIRenderer.sineAnimationCurve(controls.lerpTriggerTimer(weaponItem, partialTick));

        blitSprite(graphics, pipeline, STANDARD_HORIZONTAL, x - 5 - bloom, y, 4, 1, color);
        blitSprite(graphics, pipeline, STANDARD_HORIZONTAL, x + 2 + bloom, y, 4, 1, color);
        blitSprite(graphics, pipeline, STANDARD_VERTICAL, x, y + 2 + bloom, 1, 4, color);
    }
}