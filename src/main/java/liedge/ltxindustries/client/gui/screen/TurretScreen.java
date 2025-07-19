package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.gui.widget.EnergyGaugeWidget;
import liedge.ltxindustries.menu.TurretMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TurretScreen extends SidedUpgradableMachineScreen<TurretMenu<?>>
{
    private static final ResourceLocation TEXTURE = LTXIndustries.RESOURCES.textureLocation("gui", "turret");

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