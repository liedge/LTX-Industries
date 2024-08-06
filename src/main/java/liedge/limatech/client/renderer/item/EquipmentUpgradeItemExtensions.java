package liedge.limatech.client.renderer.item;

import liedge.limacore.client.ItemGuiRenderOverride;
import liedge.limatech.client.gui.EquipmentUpgradeTextures;
import liedge.limatech.upgradesystem.EquipmentUpgradeEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;

public final class EquipmentUpgradeItemExtensions implements ItemGuiRenderOverride
{
    public static final EquipmentUpgradeItemExtensions UPGRADE_ITEM_EXTENSIONS = new EquipmentUpgradeItemExtensions();

    private EquipmentUpgradeItemExtensions() {}

    @Override
    public boolean renderCustomGuiItem(GuiGraphics graphics, ItemStack stack, int x, int y)
    {
        EquipmentUpgradeEntry data = EquipmentUpgradeEntry.getFromItem(stack);
        if (data != null && Minecraft.getInstance().screen != null && Screen.hasShiftDown())
        {
            TextureAtlasSprite sprite = EquipmentUpgradeTextures.getUpgradeSprites().getSprite(data.upgrade().value());
            graphics.blit(x, y, 0, 16, 16, sprite);

            // Render rank number if applicable
            if (data.upgrade().value().maxRank() > 1)
            {
                graphics.pose().pushPose();
                graphics.pose().translate(0, 0, 200f);

                graphics.drawString(Minecraft.getInstance().font, Integer.toString(data.upgradeRank()), x + 1, y + 8, 0xffffff);

                graphics.pose().popPose();
            }

            return true;
        }

        return false;
    }
}