package liedge.ltxindustries.client.gui.screen;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.ltxindustries.blockentity.MolecularReconstructorBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.widget.EnergyGaugeWidget;
import liedge.ltxindustries.client.gui.widget.MachineProgressWidget;
import liedge.ltxindustries.menu.MolecularReconstructorMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MolecularReconstructorScreen extends SidedUpgradableMachineScreen<MolecularReconstructorMenu>
{
    public MolecularReconstructorScreen(MolecularReconstructorMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();

        addRenderableOnly(new EnergyGaugeWidget(menu.menuContext(), leftPos + 10, topPos + 9));
        addRenderableOnly(new ProgressBar(menu.menuContext(), leftPos + 75, topPos + 39));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
    {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        blitInventoryAndHotbar(guiGraphics, 7, 83);
        blitPowerInSlot(guiGraphics, 7, 52);
        blitEmptySlot(guiGraphics, 55, 33);
        blitOutputSlot(guiGraphics, 101, 31);
    }

    private static class ProgressBar extends MachineProgressWidget
    {
        private ProgressBar(MolecularReconstructorBlockEntity blockEntity, int x, int y)
        {
            super(blockEntity, x, y);
        }

        @Override
        public boolean hasTooltip()
        {
            return blockEntity.getProcessTimePercent() > 0;
        }

        @Override
        public void createWidgetTooltip(TooltipLineConsumer consumer)
        {
            int fill = (int) (getFillPercentage() * 100f);
            consumer.accept(LTXILangKeys.WORKING_PROGRESS_TOOLTIP.translateArgs(fill).withStyle(ChatFormatting.GRAY));
        }
    }
}