package liedge.limatech.client.gui.screen;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.MolecularReconstructorBlockEntity;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.client.gui.widget.EnergyGaugeWidget;
import liedge.limatech.client.gui.widget.ShortVerticalProgressWidget;
import liedge.limatech.menu.MolecularReconstructorMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MolecularReconstructorScreen extends SidedUpgradableMachineScreen<MolecularReconstructorMenu>
{
    private static final ResourceLocation TEXTURE = LimaTech.RESOURCES.textureLocation("gui", "molecular_reconstructor");

    public MolecularReconstructorScreen(MolecularReconstructorMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.alignInventoryLabelRight = true;
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();

        addRenderableOnly(new EnergyGaugeWidget(menu.menuContext().getEnergyStorage(), leftPos + 11, topPos + 10));
        addRenderableOnly(new ProgressBar(leftPos + 76, topPos + 33, menu.menuContext()));
    }

    @Override
    public ResourceLocation getBgTexture()
    {
        return TEXTURE;
    }

    private static class ProgressBar extends ShortVerticalProgressWidget
    {
        private final  MolecularReconstructorBlockEntity blockEntity;

        private ProgressBar(int x, int y, MolecularReconstructorBlockEntity blockEntity)
        {
            super(x, y);
            this.blockEntity = blockEntity;
        }

        @Override
        protected float getFillPercentage()
        {
            return blockEntity.getProcessTimePercent();
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
            consumer.accept(LimaTechLang.WORKING_PROGRESS_TOOLTIP.translateArgs(fill).withStyle(ChatFormatting.GRAY));
        }
    }
}