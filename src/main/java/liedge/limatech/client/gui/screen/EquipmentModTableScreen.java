package liedge.limatech.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limacore.client.gui.UnmanagedSprite;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.client.gui.EquipmentUpgradeTextures;
import liedge.limatech.client.gui.widget.ScrollableGUIElement;
import liedge.limatech.client.gui.widget.ScrollbarWidget;
import liedge.limatech.menu.EquipmentModTableMenu;
import liedge.limatech.upgradesystem.EquipmentUpgrade;
import liedge.limatech.upgradesystem.EquipmentUpgradeEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static liedge.limacore.client.gui.LimaGuiUtil.isMouseWithinArea;
import static liedge.limatech.LimaTechConstants.LIME_GREEN;
import static liedge.limatech.LimaTechConstants.NIOBIUM_PURPLE;

public class EquipmentModTableScreen extends LimaMenuScreen<EquipmentModTableMenu> implements ScrollableGUIElement
{
    private static final ResourceLocation TEXTURE = LimaTech.RESOURCES.textureLocation("gui", "item_upgrades");
    private static final UnmanagedSprite UPGRADE_ENTRY_NEUTRAL = new UnmanagedSprite(TEXTURE, 0, 200, 104, 20);
    private static final UnmanagedSprite UPGRADE_ENTRY_HOVERED = new UnmanagedSprite(TEXTURE, 0, 220, 104, 20);

    private int upgradeCount;
    private int scrollWheelDelta = 1;
    private int currentScrollRow;
    private @Nullable ScrollbarWidget scrollbar;

    public EquipmentModTableScreen(EquipmentModTableMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 190, 200, LIME_GREEN.packedRGB());

        this.inventoryLabelX = 14;
        this.inventoryLabelY = 108;
    }

    @Override
    protected void addWidgets()
    {
        this.scrollbar = addRenderableWidget(new ScrollbarWidget(leftPos + 167, topPos + 23, 80, this));
    }

    @Override
    public ResourceLocation getBgTexture()
    {
        return TEXTURE;
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

        List<EquipmentUpgradeEntry> remoteUpgrades = menu.getRemoteUpgrades();

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

                UnmanagedSprite sprite = isMouseWithinArea(mouseX, mouseY, ix, iy, 104, 20) ? UPGRADE_ENTRY_HOVERED : UPGRADE_ENTRY_NEUTRAL;
                sprite.singleBlit(graphics, ix, iy);

                EquipmentUpgradeEntry entry = remoteUpgrades.get(i);
                EquipmentUpgrade upgrade = entry.upgrade().value();

                graphics.blit(ix + 2, iy + 2, 0, 16, 16, EquipmentUpgradeTextures.getUpgradeSprites().getSprite(upgrade));
                PoseStack poseStack = graphics.pose();

                poseStack.pushPose();

                int titleX = ix + 22;
                int titleY = iy + 3;

                poseStack.translate(titleX, titleY, 0);
                poseStack.scale(0.7f, 0.7f, 0);

                graphics.enableScissor(titleX, titleY, titleX + 79, titleY + 6);
                graphics.drawString(font, upgrade.title(), 0, 0, LIME_GREEN.packedRGB(), false);
                graphics.disableScissor();
                graphics.drawString(font, LimaTechLang.EQUIPMENT_UPGRADE_RANK.translateArgs(entry.upgradeRank()), 0, font.lineHeight + 4, 0xffffff, false);

                poseStack.popPose();
            }
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int x, int y)
    {
        final int minY = topPos + 23;

        if (menu.getCarried().isEmpty() && isMouseWithinArea(x, y, leftPos + 61, minY, 104, 80))
        {
            List<EquipmentUpgradeEntry> remoteUpgrades = menu.getRemoteUpgrades();

            if (!remoteUpgrades.isEmpty())
            {
                final int min = currentScrollRow;
                final int max = Math.min(remoteUpgrades.size(), min + 4);

                for (int i = min; i < max; i++)
                {
                    int upgradeIndex = i - min;
                    int iy = minY + 20 * upgradeIndex;

                    if (y >= iy && y < (iy + 20))
                    {
                        EquipmentUpgrade upgrade = remoteUpgrades.get(i).upgrade().value();

                        List<Component> lines = new ObjectArrayList<>();
                        lines.add(upgrade.title().copy().withStyle(LIME_GREEN.chatStyle()));
                        lines.add(upgrade.description());
                        lines.add(LimaTechLang.UPGRADE_REMOVE_HINT.translate().withStyle(NIOBIUM_PURPLE.chatStyle()));

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
        final int minY = topPos + 23;

        if (isMouseWithinArea(mouseX, mouseY, leftPos + 61, minY, 104, 80) && Screen.hasShiftDown())
        {
            List<EquipmentUpgradeEntry> remoteUpgrades = menu.getRemoteUpgrades();

            if (!remoteUpgrades.isEmpty())
            {
                final int min = currentScrollRow;
                final int max = Math.min(remoteUpgrades.size(), min + 4);

                for (int i = min; i < max; i++)
                {
                    int upgradeIndex = i - min;
                    int iy = minY + 20 * upgradeIndex;

                    if (mouseY >= iy && mouseY < (iy + 20))
                    {
                        sendCustomButtonData(0, LimaRegistryUtil.getNonNullRegistryId(remoteUpgrades.get(i).upgrade()), LimaCoreNetworkSerializers.RESOURCE_LOCATION);
                    }
                }
            }

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}