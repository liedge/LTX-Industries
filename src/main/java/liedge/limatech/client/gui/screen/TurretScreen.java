package liedge.limatech.client.gui.screen;

import liedge.limatech.LimaTech;
import liedge.limatech.client.gui.widget.EnergyGaugeWidget;
import liedge.limatech.menu.TurretMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TurretScreen extends SidedUpgradableMachineScreen<TurretMenu<?>>
{
    private static final ResourceLocation TEXTURE = LimaTech.RESOURCES.textureLocation("gui", "turret");

    public TurretScreen(TurretMenu<?> menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, 188);
        this.inventoryLabelY = 95;
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();

        addRenderableOnly(new EnergyGaugeWidget(menu.menuContext().getEnergyStorage(), leftPos + 10, topPos + 10));
    }

    @Override
    public ResourceLocation getBgTexture()
    {
        return TEXTURE;
    }
}