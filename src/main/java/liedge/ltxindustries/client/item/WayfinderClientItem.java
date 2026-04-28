package liedge.ltxindustries.client.item;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientExtendedInput;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;

public final class WayfinderClientItem extends WeaponClientItem
{
    public WayfinderClientItem()
    {
        super(5, 5, 5, 7);
    }

    @Override
    protected void extractCrosshairSprites(GuiGraphicsExtractor graphics, RenderPipeline pipeline, LocalPlayer player, WeaponItem weaponItem, ClientExtendedInput controls, int x, int y, LimaColor color, float partialTick)
    {
        float bloom = 5f * triggerCurve(controls, weaponItem, 0.2f, partialTick);

        blitSprite(graphics, pipeline, HOLLOW_DOT, x, y, 5, 5, color);
        blitSprite(graphics, pipeline, ANGLE_BRACKET, x - 6 - bloom, y - 1, 4, 7, color);
        blitSpriteMirrorU(graphics, pipeline, ANGLE_BRACKET, x + 7 + bloom, y - 1, 4, 7, color);
    }
}