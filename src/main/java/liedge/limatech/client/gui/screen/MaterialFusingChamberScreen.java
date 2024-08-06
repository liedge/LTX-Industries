package liedge.limatech.client.gui.screen;

import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.io.MachineInputType;
import liedge.limatech.client.gui.widget.EnergyGaugeWidget;
import liedge.limatech.client.gui.widget.MachineProgressWidget;
import liedge.limatech.client.gui.widget.OpenIOControlButton;
import liedge.limatech.menu.MaterialFusingChamberMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MaterialFusingChamberScreen extends LimaTechSidebarScreen<MaterialFusingChamberMenu>
{
    public static final ResourceLocation SCREEN_TEXTURE = LimaTech.RESOURCES.textureLocation("gui", "material_fusing_chamber");

    public MaterialFusingChamberScreen(MaterialFusingChamberMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT, 15);
    }

    @Override
    protected void positionLabels()
    {
        super.positionLabels();
        this.inventoryLabelX = bgWidth - 6 - font.width(playerInventoryTitle);
    }

    @Override
    protected void addWidgets()
    {
        addRenderableOnly(new EnergyGaugeWidget(menu.menuContext().getEnergyStorage(), leftPos + 11, topPos + 10));
        addRenderableOnly(new MachineProgressWidget(menu.menuContext(), leftPos + 81, topPos + 41));
        addRenderableWidget(new OpenIOControlButton(leftPos + bgWidth, topPos + 3, this, 0, MachineInputType.ITEMS));
    }

    @Override
    public ResourceLocation getBgTexture()
    {
        return SCREEN_TEXTURE;
    }
}