package liedge.ltxindustries.integration.guideme;

import guideme.document.LytRect;
import guideme.document.block.LytBlock;
import guideme.document.interaction.GuideTooltip;
import guideme.document.interaction.InteractiveElement;
import guideme.document.interaction.TextTooltip;
import guideme.layout.LayoutContext;
import guideme.render.RenderContext;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.ItemLikeIconsRenderer;
import liedge.ltxindustries.lib.upgrades.Upgrade;
import liedge.ltxindustries.lib.upgrades.UpgradeEntry;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

final class LytUpgradeEntry extends LytBlock implements InteractiveElement
{
    private static final Identifier SPRITE = LTXIndustries.RESOURCES.id("widget/guide_upgrade_entry");
    private static final int WIDTH = 24;
    private static final int HEIGHT = 20;

    private final UpgradeEntry entry;

    LytUpgradeEntry(UpgradeEntry entry)
    {
        this.entry = entry;
    }

    @Override
    protected LytRect computeLayout(LayoutContext context, int x, int y, int availableWidth)
    {
        return new LytRect(x, y, WIDTH, HEIGHT);
    }

    @Override
    protected void onLayoutMoved(int deltaX, int deltaY) { }

    @Override
    public void render(RenderContext context)
    {
        int x = bounds.x();
        int y = bounds.y();

        GuiGraphicsExtractor graphics = context.guiGraphics();
        Upgrade upgrade = entry.upgrade().value();

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE, x, y, WIDTH, HEIGHT);
        ItemLikeIconsRenderer.render(graphics, upgrade.display().icon(), x + 2, y + 2);

        float rankHeight = 16f - 16f * LimaCoreMath.divideFloat(entry.rank(), upgrade.maxRank());
        LimaGuiUtil.fillVerticalGradient(graphics, RenderPipelines.GUI, x + 20, y + 2 + rankHeight, x + 22, y + 18, LTXIConstants.UPGRADE_RANK_MAGENTA_1.argb32(), LTXIConstants.UPGRADE_RANK_MAGENTA_2.argb32());
    }

    @Override
    public Optional<GuideTooltip> getTooltip(float x, float y)
    {
        List<Component> lines = new ObjectArrayList<>();

        Upgrade upgrade = entry.upgrade().value();

        lines.add(upgrade.display().title());

        if (upgrade.maxRank() > 1)
        {
            lines.add(LTXILangKeys.UPGRADE_RANK_TOOLTIP.translateArgs(entry.rank(), upgrade.maxRank()).withStyle(LTXIConstants.UPGRADE_RANK_MAGENTA_1.chatStyle()));
        }

        upgrade.appendEffectTooltips(entry.rank(), lines::add);

        return Optional.of(new TextTooltip(lines));
    }
}