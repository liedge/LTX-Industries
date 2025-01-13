package liedge.limatech.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limacore.client.gui.UnmanagedSprite;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.client.gui.widget.ScrollableGUIElement;
import liedge.limatech.client.gui.widget.ScrollbarWidget;
import liedge.limatech.lib.upgradesystem.UpgradeBase;
import liedge.limatech.menu.UpgradesConfigMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.Optional;

import static liedge.limacore.client.gui.LimaGuiUtil.isMouseWithinArea;
import static liedge.limatech.LimaTechConstants.LIME_GREEN;
import static liedge.limatech.LimaTechConstants.OUTPUT_ORANGE;
import static liedge.limatech.client.gui.widget.ScreenWidgetSprites.UPGRADE_ENTRY_FOCUSED;
import static liedge.limatech.client.gui.widget.ScreenWidgetSprites.UPGRADE_ENTRY_NOT_FOCUSED;

public abstract class UpgradesConfigScreen<U extends UpgradeBase<?, ?, U>, M extends UpgradesConfigMenu<?, U, ?>> extends LimaMenuScreen<M> implements ScrollableGUIElement
{
    private int upgradeCount;
    private int scrollWheelDelta = 1;
    private int currentScrollRow;
    private ScrollbarWidget scrollbar;

    protected UpgradesConfigScreen(M menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 190, 200, LIME_GREEN.packedRGB());

        this.inventoryLabelX = 14;
        this.inventoryLabelY = 108;
    }

    private int selectorLeft()
    {
        return leftPos + 61;
    }

    private int selectorTop()
    {
        return topPos + 23;
    }

    private boolean isMouseOverSelector(double mouseX, double mouseY)
    {
        return isMouseWithinArea(mouseX, mouseY, selectorLeft(), selectorTop(), 104, 80);
    }

    protected abstract int upgradeRemovalButtonId();

    @Override
    protected void init()
    {
        super.init();

        if (scrollbar != null)
        {
            scrollbar.reset();
            currentScrollRow = 0;
        }
    }

    @Override
    protected void addWidgets()
    {
        this.scrollbar = addRenderableWidget(new ScrollbarWidget(leftPos + 167, topPos + 23, 80, this));
    }

    @Override
    public boolean canScroll()
    {
        return upgradeCount > 4;
    }

    @Override
    public void scrollUpdated(int scrollPosition)
    {
        int newScrollRow = Math.max(0, Math.min(scrollPosition / scrollWheelDelta, upgradeCount - 4));
        if (newScrollRow != currentScrollRow)
        {
            currentScrollRow = newScrollRow;
        }
    }

    @Override
    protected void containerTick()
    {
        if (menu.shouldUpdateScreen())
        {
            int newUpgradeCount = menu.getRemoteUpgrades().size();
            this.upgradeCount = newUpgradeCount;
            this.scrollWheelDelta = canScroll() ? 67 / newUpgradeCount : 1;
            if (scrollbar != null) scrollbar.reset();
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        super.renderBg(graphics, partialTick, mouseX, mouseY);

        List<Object2IntMap.Entry<Holder<U>>> remoteUpgrades = menu.getRemoteUpgrades();

        // Render upgrades
        if (!remoteUpgrades.isEmpty())
        {
            final int min = currentScrollRow;
            final int max = Math.min(remoteUpgrades.size(), min + 4);

            for (int i = min; i < max; i++)
            {
                int upgradeIndex = i - min;
                int ix = leftPos + 61;
                int iy = topPos + 23 + (20 * upgradeIndex);

                UnmanagedSprite sprite = isMouseWithinArea(mouseX, mouseY, ix, iy, 104, 20) ? UPGRADE_ENTRY_FOCUSED : UPGRADE_ENTRY_NOT_FOCUSED;
                sprite.singleBlit(graphics, ix, iy);

                Object2IntMap.Entry<Holder<U>> entry = remoteUpgrades.get(i);
                U upgrade = entry.getKey().value();

                //graphics.blit(ix + 2, iy + 2, 0, 16, 16, UpgradeIconTextures.getUpgradeSprites().getSprite(upgrade));
                entry.getKey().value().icon().render(graphics, ix + 2, iy + 2);
                PoseStack poseStack = graphics.pose();

                poseStack.pushPose();

                int titleX = ix + 22;
                int titleY = iy + 3;

                poseStack.translate(titleX, titleY, 0);
                poseStack.scale(0.7f, 0.7f, 0);

                graphics.enableScissor(titleX, titleY, titleX + 79, titleY + 6);
                graphics.drawString(font, upgrade.title(), 0, 0, LIME_GREEN.packedRGB(), false);
                graphics.disableScissor();
                graphics.drawString(font, LimaTechLang.UPGRADE_RANK_TOOLTIP.translateArgs(entry.getIntValue(), upgrade.maxRank()), 0, font.lineHeight + 4, 0xffffff, false);

                poseStack.popPose();
            }
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int x, int y)
    {
        if (menu.getCarried().isEmpty() && isMouseOverSelector(x, y))
        {
            List<Object2IntMap.Entry<Holder<U>>> remoteUpgrades = menu.getRemoteUpgrades();

            if (!remoteUpgrades.isEmpty())
            {
                final int min = currentScrollRow;
                final int max = Math.min(remoteUpgrades.size(), min + 4);

                for (int i = min; i < max; i++)
                {
                    int upgradeIndex = i - min;
                    int iy = selectorTop() + 20 * upgradeIndex;

                    if (y >= iy && y < (iy + 20))
                    {
                        Object2IntMap.Entry<Holder<U>> entry = remoteUpgrades.get(i);
                        U upgrade = entry.getKey().value();

                        List<Component> lines = new ObjectArrayList<>();
                        lines.add(upgrade.title().copy().withStyle(LIME_GREEN.chatStyle()));
                        lines.add(upgrade.description());
                        lines.add(upgrade.getEffectsTooltip(entry.getIntValue()));
                        lines.add(LimaTechLang.UPGRADE_REMOVE_HINT.translate().withStyle(OUTPUT_ORANGE.chatStyle()));

                        graphics.renderTooltip(font, lines, Optional.empty(), x, y);
                    }
                }
            }
        }
        else
        {
            super.renderTooltip(graphics, x, y);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (isMouseOverSelector(mouseX, mouseY) && Screen.hasShiftDown())
        {
            List<Object2IntMap.Entry<Holder<U>>> remoteUpgrades = menu.getRemoteUpgrades();

            if (!remoteUpgrades.isEmpty())
            {
                final int min = currentScrollRow;
                final int max = Math.min(remoteUpgrades.size(), min + 4);

                for (int i = min; i < max; i++)
                {
                    int upgradeIndex = i - min;
                    int iy = selectorTop() + 20 * upgradeIndex;

                    if (mouseY >= iy && mouseY < (iy + 20))
                    {
                        sendCustomButtonData(upgradeRemovalButtonId(), LimaRegistryUtil.getNonNullRegistryId(remoteUpgrades.get(i).getKey()), LimaCoreNetworkSerializers.RESOURCE_LOCATION);
                    }
                }
            }

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
    {
        if (isMouseOverSelector(mouseX, mouseY) && scrollbar != null)
        {
            int delta = scrollWheelDelta * (int) -Math.signum(scrollY);
            scrollbar.moveScrollbar(delta);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}