package liedge.limatech.client.gui.screen;

import liedge.limatech.LimaTech;
import liedge.limatech.client.gui.widget.EnergyGaugeWidget;
import liedge.limatech.client.gui.widget.MachineProgressWidget;
import liedge.limatech.menu.MaterialFusingChamberMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MaterialFusingChamberScreen extends SidedUpgradableMachineScreen<MaterialFusingChamberMenu>
{
    public static final ResourceLocation SCREEN_TEXTURE = LimaTech.RESOURCES.textureLocation("gui", "material_fusing_chamber");

    public MaterialFusingChamberScreen(MaterialFusingChamberMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.alignInventoryLabelRight = true;
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();

        addRenderableOnly(new EnergyGaugeWidget(menu.menuContext().getEnergyStorage(), leftPos + 11, topPos + 10));
        addRenderableOnly(new MachineProgressWidget(menu.menuContext(), leftPos + 81, topPos + 41));
    }

    @Override
    public ResourceLocation getBgTexture()
    {
        return SCREEN_TEXTURE;
    }
}