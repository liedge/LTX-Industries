package liedge.ltxindustries.client.renderer.item;

import liedge.limacore.client.ItemGuiRenderOverride;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.gui.UpgradeIconRenderers;
import liedge.ltxindustries.item.UpgradeModuleItem;
import liedge.ltxindustries.lib.upgrades.UpgradeBaseEntry;
import liedge.ltxindustries.lib.upgrades.UpgradeIcon;
import liedge.ltxindustries.util.config.LTXIClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;

public final class UpgradeModuleItemExtensions implements ItemGuiRenderOverride
{
    private static final UpgradeModuleItemExtensions INSTANCE = new UpgradeModuleItemExtensions();

    private static boolean shouldShowIcon()
    {
        return Minecraft.getInstance().screen != null && (Screen.hasShiftDown() || LTXIClientConfig.ALWAYS_SHOW_UPGRADE_ICONS.getAsBoolean());
    }

    public static UpgradeModuleItemExtensions getInstance()
    {
        return INSTANCE;
    }

    private UpgradeModuleItemExtensions() {}

    public boolean renderIconWithRankBar(GuiGraphics graphics, UpgradeBaseEntry<?> entry, int x, int y)
    {
        UpgradeIcon icon = entry.upgrade().value().display().icon();
        if (!UpgradeIconRenderers.renderIcon(graphics, icon, x, y)) return false;

        final int rank = entry.upgradeRank();
        final int maxRank = entry.upgrade().value().maxRank();

        // Render rank bar if applicable
        if (maxRank > 1 && rank < maxRank)
        {
            graphics.pose().pushPose();

            LimaGuiUtil.fillVerticalGradient(graphics, RenderType.gui(), x + 1, y + 1, x + 3, y + 15, 300, 0xff4a4a4a, -16777216);
            float yo = 14f - 14f * LimaCoreMath.divideFloat(rank, maxRank);
            LimaGuiUtil.fillVerticalGradient(graphics, RenderType.gui(), x + 1, y + 1 + yo, x + 3, y + 15, 300, LTXIConstants.UPGRADE_RANK_MAGENTA.argb32(), 0xffd13ff0);

            graphics.pose().popPose();
        }

        return true;
    }

    @Override
    public boolean renderCustomGuiItem(GuiGraphics graphics, ItemStack stack, int x, int y)
    {
        if (stack.getItem() instanceof UpgradeModuleItem<?, ?> moduleItem)
        {
            UpgradeBaseEntry<?> entry = stack.get(moduleItem.entryComponentType());
            if (entry != null && shouldShowIcon())
            {
                return renderIconWithRankBar(graphics, entry, x, y);
            }
        }

        return false;
    }
}