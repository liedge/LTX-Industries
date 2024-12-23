package liedge.limatech.client.gui.screen;

import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.io.MachineInputType;
import liedge.limatech.client.gui.widget.EnergyGaugeWidget;
import liedge.limatech.client.gui.widget.OpenIOControlButton;
import liedge.limatech.menu.EnergyStorageArrayMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EnergyStorageArrayScreen extends RightSidebarScreen.RightAlignedInventoryLabel<EnergyStorageArrayMenu>
{
    private static final ResourceLocation TEXTURE = LimaTech.RESOURCES.textureLocation("gui", "energy_storage_array");

    public EnergyStorageArrayScreen(EnergyStorageArrayMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT, 15);
    }

    @Override
    protected void addWidgets()
    {
        addRenderableWidget(new OpenIOControlButton(leftPos + bgWidth, topPos + 3, this, 0, MachineInputType.ENERGY));
        addRenderableOnly(new EnergyGaugeWidget(menu.menuContext().getEnergyStorage(), leftPos + 11, topPos + 10));
    }

    @Override
    public ResourceLocation getBgTexture()
    {
        return TEXTURE;
    }
}