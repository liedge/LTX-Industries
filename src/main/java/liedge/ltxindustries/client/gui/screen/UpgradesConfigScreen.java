package liedge.ltxindustries.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaMathUtil;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.UpgradeIconRenderers;
import liedge.ltxindustries.client.gui.widget.ScrollableGUIElement;
import liedge.ltxindustries.client.gui.widget.ScrollbarWidget;
import liedge.ltxindustries.lib.upgrades.UpgradeBase;
import liedge.ltxindustries.menu.UpgradesConfigMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Optional;

import static liedge.limacore.client.gui.LimaGuiUtil.isMouseWithinArea;
import static liedge.ltxindustries.LTXIConstants.OUTPUT_ORANGE;
import static liedge.ltxindustries.LTXIConstants.UPGRADE_RANK_MAGENTA;

public abstract class UpgradesConfigScreen<U extends UpgradeBase<?, U>, M extends UpgradesConfigMenu<?, U, ?>> extends LTXIScreen<M> implements ScrollableGUIElement
{
    private static final ResourceLocation SELECTOR_SPRITE = LTXIndustries.RESOURCES.location("widget/upgrade_selector");
    private static final ResourceLocation SELECTOR_SPRITE_FOCUS = LTXIndustries.RESOURCES.location("widget/upgrade_selector_focus");
    private static final int SELECTOR_WIDTH = 104;
    private static final int SELECTOR_HEIGHT = 20;

    private int upgradeCount;
    private int scrollWheelDelta = 1;
    private int currentScrollRow;
    private ScrollbarWidget scrollbar;

    protected UpgradesConfigScreen(M menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 190, 200);

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

    protected abstract void blitSlotSprites(GuiGraphics graphics);

    @Override
    protected void addWidgets()
    {
        this.scrollbar = addRenderableWidget(new ScrollbarWidget(leftPos + 167, topPos + 23, 80, this));
        scrollbar.reset(); // Always reset after re-initializing
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

        // Background sprites
        blitInventoryAndHotbar(graphics, 14, 117);
        blitDarkPanel(graphics, 60, 22, 106, 82);
        blitLightPanel(graphics, 166, 22, 10, 82);
        blitSlotSprites(graphics);

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
                int iy = selectorTop() + 20 * upgradeIndex;

                ResourceLocation sprite = isMouseWithinArea(mouseX, mouseY, ix, iy, SELECTOR_WIDTH, SELECTOR_HEIGHT) ? SELECTOR_SPRITE_FOCUS : SELECTOR_SPRITE;
                graphics.blitSprite(sprite, ix, iy, SELECTOR_WIDTH, SELECTOR_HEIGHT);

                Object2IntMap.Entry<Holder<U>> entry = remoteUpgrades.get(i);
                U upgrade = entry.getKey().value();

                UpgradeIconRenderers.renderWithSpriteFallback(graphics, upgrade.display().icon(), ix + 2, iy + 2);

                // Render title
                int titleX = ix + 22;
                int titleY = iy + 3;

                PoseStack poseStack = graphics.pose();

                poseStack.pushPose();

                graphics.enableScissor(titleX, titleY, titleX + 79,  titleY + 9);

                poseStack.translate(titleX, titleY + (11d - (double) font.lineHeight * 0.8d) / 2d, 0);
                poseStack.scale(0.8f, 0.8f, 0.8f);

                graphics.drawString(font, upgrade.display().title(), 0, 0, -1, false);

                graphics.disableScissor();

                poseStack.popPose();


                // Render rank bar
                float xo = 75f * LimaMathUtil.divideFloat(entry.getIntValue(), upgrade.maxRank());
                int leftColor;
                int rightColor;

                if (entry.getIntValue() == upgrade.maxRank())
                {
                    leftColor = 0xff3f5ff0;
                    rightColor = 0xff3ff0d1;
                }
                else
                {
                    leftColor = 0xffd13ff0;
                    rightColor = UPGRADE_RANK_MAGENTA.argb32();
                }

                renderGradientBar(graphics, ix + 21, iy + 15, ix + 21 + xo, iy + 19, leftColor, rightColor);
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
                        lines.add(upgrade.display().title());
                        lines.add(LTXILangKeys.UPGRADE_RANK_TOOLTIP.translateArgs(entry.getIntValue(), upgrade.maxRank()).withStyle(UPGRADE_RANK_MAGENTA.chatStyle()));
                        lines.add(upgrade.display().description());
                        upgrade.applyEffectsTooltips(entry.getIntValue(), lines::add);

                        lines.add(LTXILangKeys.UPGRADE_REMOVE_HINT.translate().withStyle(OUTPUT_ORANGE.chatStyle()));


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
                        sendCustomButtonData(UpgradesConfigMenu.UPGRADE_REMOVAL_BUTTON_ID, LimaRegistryUtil.getNonNullRegistryId(remoteUpgrades.get(i).getKey()), LimaCoreNetworkSerializers.RESOURCE_LOCATION);
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

    private void renderGradientBar(GuiGraphics graphics, float x1, float y1, float x2, float y2, int leftColor, int rightColor)
    {
        Matrix4f mx4 = graphics.pose().last().pose();
        VertexConsumer buffer = graphics.bufferSource().getBuffer(RenderType.guiOverlay());

        buffer.addVertex(mx4, x1, y1, 0).setColor(leftColor);
        buffer.addVertex(mx4, x1, y2, 0).setColor(leftColor);
        buffer.addVertex(mx4, x2, y2, 0).setColor(rightColor);
        buffer.addVertex(mx4, x2, y1, 0).setColor(rightColor);

        graphics.flush();
    }
}