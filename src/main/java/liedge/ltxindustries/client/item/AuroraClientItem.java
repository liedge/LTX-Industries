package liedge.ltxindustries.client.item;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientExtendedInput;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;

public final class AuroraClientItem extends WeaponClientItem
{
    public AuroraClientItem()
    {
        super(1, 13);
    }

    @Override
    protected void extractCrosshairSprites(GuiGraphicsExtractor graphics, RenderPipeline pipeline, LocalPlayer player, WeaponItem weaponItem, ClientExtendedInput controls, int x, int y, LimaColor color, float partialTick)
    {
        float bloom = 3 + 4f * triggerCurve(controls, weaponItem, 0.15f, partialTick);
        blitSprite(graphics, pipeline, CIRCLE_BRACKET, x - 6 - bloom, y, 6, 13, color);
        blitSpriteMirrorU(graphics, pipeline, CIRCLE_BRACKET, x + 1 + bloom, y, 6, 13, color);
    }
}