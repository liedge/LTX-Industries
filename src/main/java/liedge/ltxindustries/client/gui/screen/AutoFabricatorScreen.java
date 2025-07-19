package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.gui.widget.EnergyGaugeWidget;
import liedge.ltxindustries.client.gui.widget.FabricatorProgressWidget;
import liedge.ltxindustries.menu.AutoFabricatorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AutoFabricatorScreen extends SidedUpgradableMachineScreen<AutoFabricatorMenu>
{
    public static final ResourceLocation TEXTURE = LTXIndustries.RESOURCES.textureLocation("gui", "auto_fabricator");

    public AutoFabricatorScreen(AutoFabricatorMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 190, 180);

        this.inventoryLabelX = 14;
        this.inventoryLabelY = 87;
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();

        addRenderableOnly(new EnergyGaugeWidget(menu.menuContext().getEnergyStorage(), leftPos + 11, topPos + 10));
        addRenderableOnly(new FabricatorProgressWidget(leftPos + 171, topPos + 70, menu.menuContext()));
    }

    @Override
    public ResourceLocation getBgTexture()
    {
        return TEXTURE;
    }
}