package liedge.limatech.client.renderer.item;

import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.ItemGuiRenderOverride;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.client.gui.UpgradeIconRenderers;
import liedge.limatech.item.UpgradeModuleItem;
import liedge.limatech.lib.upgrades.UpgradeBaseEntry;
import liedge.limatech.lib.upgrades.UpgradeIcon;
import liedge.limatech.util.config.LimaTechClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import static liedge.limatech.LimaTechConstants.UPGRADE_RANK_MAGENTA;

public final class UpgradeModuleItemExtensions implements ItemGuiRenderOverride
{
    private static final UpgradeModuleItemExtensions INSTANCE = new UpgradeModuleItemExtensions();

    private static boolean shouldShowIcon()
    {
        return Minecraft.getInstance().screen != null && (Screen.hasShiftDown() || LimaTechClientConfig.alwaysShowUpgradeIcons());
    }

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
            if (entry != null && shouldShowIcon())
            {
                UpgradeIcon icon = entry.upgrade().value().icon();
                UpgradeIconRenderers.renderIcon(graphics, icon, x, y);

                final int rank = entry.upgradeRank();
                final int maxRank = entry.upgrade().value().maxRank();

                // Render rank bar if applicable
                if (maxRank > 1 && rank < maxRank)
                {
                    graphics.pose().pushPose();

                    renderGradientBar(graphics, x + 1, y + 1, x + 3, y +  15, 0xff4a4a4a, -16777216);
                    float yo = 14f - 14f * LimaMathUtil.divideFloat(rank, maxRank);
                    renderGradientBar(graphics, x + 1, y + 1 + yo, x + 3, y + 15, UPGRADE_RANK_MAGENTA.packedRGB(), 0xffd13ff0);

                    graphics.pose().popPose();
                }

                return true;
            }
        }

        return false;
    }

    private void renderGradientBar(GuiGraphics graphics, float x1, float y1, float x2, float y2, int topColor, int bottomColor)
    {
        Matrix4f mx4 = graphics.pose().last().pose();
        VertexConsumer buffer = graphics.bufferSource().getBuffer(RenderType.guiOverlay());

        buffer.addVertex(mx4, x1, y1, 0).setColor(topColor);
        buffer.addVertex(mx4, x1, y2, 0).setColor(bottomColor);
        buffer.addVertex(mx4, x2, y2, 0).setColor(bottomColor);
        buffer.addVertex(mx4, x2, y1, 0).setColor(topColor);

        graphics.flush();
    }
}