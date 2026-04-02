package liedge.ltxindustries.client.item;

import liedge.limacore.client.ItemGuiRenderOverride;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.gui.UpgradeIconRenderers;
import liedge.ltxindustries.item.UpgradeModuleItem;
import liedge.ltxindustries.lib.upgrades.UpgradeEntry;
import liedge.ltxindustries.lib.upgrades.UpgradeIcon;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.util.config.LTXIClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class UpgradeModuleClientItem implements ItemGuiRenderOverride
{
    public static final UpgradeModuleClientItem INSTANCE = new UpgradeModuleClientItem();

    private UpgradeModuleClientItem() {}

    private static boolean shouldRender()
    {
        return Minecraft.getInstance().screen != null && (Minecraft.getInstance().hasShiftDown() || LTXIClientConfig.ALWAYS_SHOW_UPGRADE_ICONS.getAsBoolean());
    }

    private boolean renderIcon(GuiGraphicsExtractor graphics, UpgradeEntry entry, int x, int y)
    {
        UpgradeIcon icon = entry.upgrade().value().display().icon();
        if (!UpgradeIconRenderers.renderIcon(graphics, icon, x, y)) return false;

        int rank = entry.rank();
        int maxRank = entry.upgrade().value().maxRank();

        // Render rank bar if applicable
        if (maxRank > 1 && rank < maxRank)
        {
            graphics.nextStratum();
            LimaGuiUtil.submitVerticalGradient(graphics, RenderPipelines.GUI, x + 1, y + 1, x + 3, y + 15, 0xff4a4a4a, -16777216);
            float yo = 14f - 14f * LimaCoreMath.divideFloat(rank, maxRank);
            LimaGuiUtil.submitVerticalGradient(graphics, RenderPipelines.GUI, x + 1, y + 1 + yo, x + 3, y + 15, LTXIConstants.UPGRADE_RANK_MAGENTA.argb32(), 0xffd13ff0);
        }

        return true;
    }

    @Override
    public boolean renderCustomGuiItem(GuiGraphicsExtractor graphics, @Nullable LivingEntity owner, @Nullable Level level, ItemStack stack, int x, int y)
    {
        if (stack.getItem() instanceof UpgradeModuleItem)
        {
            UpgradeEntry entry = stack.get(LTXIDataComponents.UPGRADE_ENTRY);
            if (entry != null && shouldRender()) return renderIcon(graphics, entry, x, y);
        }

        return false;
    }
}