package liedge.limatech.client.gui.screen;

import liedge.limatech.LimaTech;
import liedge.limatech.client.gui.widget.EnergyGaugeWidget;
import liedge.limatech.menu.EnergyStorageArrayMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EnergyStorageArrayScreen extends SidedUpgradableMachineScreen<EnergyStorageArrayMenu>
{
    private static final ResourceLocation TEXTURE = LimaTech.RESOURCES.textureLocation("gui", "energy_storage_array");

    public EnergyStorageArrayScreen(EnergyStorageArrayMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.alignInventoryLabelRight = true;
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();

        addRenderableOnly(new EnergyGaugeWidget(menu.menuContext().getEnergyStorage(), leftPos + 11, topPos + 10));
    }

    @Override
    public ResourceLocation getBgTexture()
    {
        return TEXTURE;
    }
}