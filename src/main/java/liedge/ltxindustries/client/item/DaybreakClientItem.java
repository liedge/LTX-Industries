package liedge.ltxindustries.client.item;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientExtendedInput;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;

public final class DaybreakClientItem extends WeaponClientItem
{
    public DaybreakClientItem()
    {
        super(5, 5, 10, 10);
    }

    @Override
    protected void extractCrosshairSprites(GuiGraphicsExtractor graphics, RenderPipeline pipeline, LocalPlayer player, WeaponItem weaponItem, ClientExtendedInput controls, int x, int y, LimaColor color, float partialTick)
    {
        float bloom = 4f * triggerCurve(controls, weaponItem, 0.1f, partialTick);

        blitSprite(graphics, pipeline, HOLLOW_DOT, x, y, 5, 5, color);

        blitSpriteMirrorV(graphics, pipeline, AOE_VERTICAL, x - 1, y - 4 - bloom, 7, 2, color);
        blitSprite(graphics, pipeline, AOE_VERTICAL, x - 1, y + 7 + bloom, 7, 2, color);

        blitSprite(graphics, pipeline, AOE_HORIZONTAL, x - 4 - bloom, y - 1, 2, 7, color);
        blitSpriteMirrorU(graphics, pipeline, AOE_HORIZONTAL, x + 7 + bloom, y - 1, 2, 7, color);
    }
}