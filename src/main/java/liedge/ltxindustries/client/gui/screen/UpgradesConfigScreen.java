package liedge.ltxindustries.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.UpgradeIconRenderers;
import liedge.ltxindustries.client.gui.widget.BaseScrollGridRenderable;
import liedge.ltxindustries.client.gui.widget.ScrollbarWidget;
import liedge.ltxindustries.lib.upgrades.UpgradeBase;
import liedge.ltxindustries.menu.UpgradesConfigMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

import java.util.List;
import java.util.Optional;

import static liedge.ltxindustries.LTXIConstants.OUTPUT_ORANGE;
import static liedge.ltxindustries.LTXIConstants.UPGRADE_RANK_MAGENTA;

public abstract class UpgradesConfigScreen<U extends UpgradeBase<?, U>, M extends UpgradesConfigMenu<?, U, ?>> extends LTXIScreen<M>
{
    private static final Identifier SELECTOR_SPRITE = LTXIndustries.RESOURCES.id("widget/upgrade_selector");
    private static final Identifier SELECTOR_SPRITE_FOCUS = LTXIndustries.RESOURCES.id("widget/upgrade_selector_focus");
    public static final Identifier EQUIPMENT_MODULE_SPRITE = LTXIndustries.RESOURCES.id("equipment_upgrade_module");
    public static final Identifier MACHINE_MODULE_SPRITE = LTXIndustries.RESOURCES.id("machine_upgrade_module");

    private @Nullable ScrollbarWidget scrollbar;
    private @Nullable SelectorList<U> selectorList;

    protected UpgradesConfigScreen(M menu, Inventory inventory, Component title, int leftPadding)
    {
        super(menu, inventory, title, 190, 200, leftPadding, 0, 0);

        this.inventoryLabelX = 14;
        this.inventoryLabelY = 108;
    }

    protected abstract void blitSlotSprites(GuiGraphicsExtractor graphics);

    protected abstract Identifier fallbackModuleSprite();

    @Override
    protected void addWidgets()
    {
        this.selectorList = addRenderableOnly(new SelectorList<>(leftPos + 61, topPos + 23, this));
        this.scrollbar = addRenderableWidget(new ScrollbarWidget(leftPos + 167, topPos + 23, 80, selectorList));
        scrollbar.reset();
        selectorList.reset();
    }

    @Override
    protected void containerTick()
    {
        if (menu.shouldUpdateScreen() && selectorList != null && scrollbar != null)
        {
            selectorList.reset();
            scrollbar.reset();
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        // Background sprites
        blitInventoryAndHotbar(graphics, 14, 117);
        blitDarkPanel(graphics, 60, 22, 106, 82);
        blitLightPanel(graphics, 166, 22, 10, 82);
        blitSlotSprites(graphics);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int x, int y)
    {
        if (menu.getCarried().isEmpty() && selectorList != null)
        {
            if (selectorList.renderTooltips(graphics, x, y)) return;
        }

        super.extractTooltip(graphics, x, y);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick)
    {
        if (Minecraft.getInstance().hasShiftDown() && event.button() == InputConstants.MOUSE_BUTTON_LEFT && selectorList != null)
        {
            if (selectorList.onGridClicked(event.x(), event.y(), 0)) return true;
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
    {
        if (selectorList != null && scrollbar != null && selectorList.isMouseOver(mouseX, mouseY))
        {
            if (scrollbar.moveScrollBar(scrollY)) return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private static class SelectorList<U extends UpgradeBase<?, U>> extends BaseScrollGridRenderable<Object2IntMap.Entry<Holder<U>>>
    {
        private final UpgradesConfigScreen<U, ?> parent;

        SelectorList(int x, int y, UpgradesConfigScreen<U, ?> parent)
        {
            super(x, y, 104, 20, 1, 4);
            this.parent = parent;
        }

        @Override
        public List<Object2IntMap.Entry<Holder<U>>> getElements()
        {
            return parent.menu.getRemoteUpgrades();
        }

        @Override
        public void renderElement(GuiGraphicsExtractor graphics, Object2IntMap.Entry<Holder<U>> element, int posX, int posY, int gridIndex, int elementIndex, int mouseX, int mouseY)
        {
            U upgrade = element.getKey().value();
            int rank = element.getIntValue();

            Identifier sprite = isMouseOverElement(mouseX, mouseY, posX, posY) ? SELECTOR_SPRITE_FOCUS : SELECTOR_SPRITE;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, posX, posY, elementWidth(), elementHeight());

            // Render icon
            int iconX = posX + 2;
            int iconY = posY + 2;
            if (!UpgradeIconRenderers.renderIcon(graphics, upgrade.display().icon(), iconX, iconY))
            {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, parent.fallbackModuleSprite(), iconX, iconY, 16, 16);
            }

            // Render title
            int titleX = posX + 22;
            int titleY = posY + 3;

            Matrix3x2fStack matrixStack = graphics.pose();
            matrixStack.pushMatrix();

            graphics.enableScissor(titleX, titleY, titleX + 79, titleY + 9);

            matrixStack.translate(titleX, titleY + (11f - (float) Minecraft.getInstance().font.lineHeight * 0.8f) / 2f);
            matrixStack.scale(0.8f);

            graphics.text(Minecraft.getInstance().font, upgrade.display().title(), 0, 0, -1, false);

            graphics.disableScissor();

            matrixStack.popMatrix();

            // Render rank bar
            float xo = 75f * LimaCoreMath.divideFloat(rank, upgrade.maxRank());
            int leftColor;
            int rightColor;

            if (rank == upgrade.maxRank())
            {
                leftColor = 0xff3f5ff0;
                rightColor = 0xff3ff0d1;
            }
            else
            {
                leftColor = 0xffd13ff0;
                rightColor = UPGRADE_RANK_MAGENTA.argb32();
            }

            LimaGuiUtil.submitHorizontalGradient(graphics, RenderPipelines.GUI, posX + 21, posY + 15, posX + 21 + xo, posY + 19, leftColor, rightColor);
        }

        @Override
        public void renderElementTooltip(GuiGraphicsExtractor graphics, Object2IntMap.Entry<Holder<U>> element, int mouseX, int mouseY, int gridIndex, int elementIndex)
        {
            U upgrade = element.getKey().value();
            int rank = element.getIntValue();

            List<Component> lines = new ObjectArrayList<>();
            lines.add(upgrade.display().title());
            lines.add(LTXILangKeys.UPGRADE_RANK_TOOLTIP.translateArgs(rank, upgrade.maxRank()).withStyle(UPGRADE_RANK_MAGENTA.chatStyle()));
            lines.add(upgrade.display().description());
            upgrade.applyEffectsTooltips(rank, lines::add);

            lines.add(LTXILangKeys.UPGRADE_REMOVE_HINT.translate().withStyle(OUTPUT_ORANGE.chatStyle()));

            graphics.setTooltipForNextFrame(Minecraft.getInstance().font, lines, Optional.empty(), mouseX, mouseY);
        }

        @Override
        public void onElementClicked(Object2IntMap.Entry<Holder<U>> element, double mouseX, double mouseY, int button, int gridIndex, int elementIndex)
        {
            if (Minecraft.getInstance().hasShiftDown())
            {
                parent.sendCustomButtonData(UpgradesConfigMenu.UPGRADE_REMOVAL_BUTTON_ID, LimaRegistryUtil.getNonNullRegistryId(element.getKey()), LimaCoreNetworkSerializers.IDENTIFIER);
            }
        }
    }
}