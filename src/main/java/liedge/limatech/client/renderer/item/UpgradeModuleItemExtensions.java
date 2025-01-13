package liedge.limatech.client.renderer.item;

import liedge.limacore.client.ItemGuiRenderOverride;
import liedge.limatech.client.UpgradeIcon;
import liedge.limatech.item.UpgradeModuleItem;
import liedge.limatech.lib.upgradesystem.UpgradeBaseEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

public final class UpgradeModuleItemExtensions implements ItemGuiRenderOverride
{
    private static final UpgradeModuleItemExtensions INSTANCE = new UpgradeModuleItemExtensions();

    public static UpgradeModuleItemExtensions getInstance()
    {
        return INSTANCE;
    }

    private UpgradeModuleItemExtensions() {}

    @Override
    public boolean renderCustomGuiItem(GuiGraphics graphics, ItemStack stack, int x, int y)
    {
        if (stack.getItem() instanceof UpgradeModuleItem<?, ?> moduleItem)
        {
            UpgradeBaseEntry<?> entry = stack.get(moduleItem.entryComponentType());
            if (entry != null && Minecraft.getInstance().screen != null && Screen.hasShiftDown())
            {
                UpgradeIcon icon = entry.upgrade().value().icon();
                icon.render(graphics, x, y);

                // Render rank number if applicable
                if (icon.shouldRenderRank(entry))
                {
                    graphics.pose().pushPose();
                    graphics.pose().translate(0, 0, 240f);

                    graphics.drawString(Minecraft.getInstance().font, Integer.toString(entry.upgradeRank()), x + 1, y + 8, 0xffffff);

                    graphics.pose().popPose();
                }

                return true;
            }
        }

        return false;
    }
}