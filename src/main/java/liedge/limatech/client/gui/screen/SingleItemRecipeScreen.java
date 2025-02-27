package liedge.limatech.client.gui.screen;

import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.SimpleRecipeMachineBlockEntity;
import liedge.limatech.client.gui.widget.EnergyGaugeWidget;
import liedge.limatech.client.gui.widget.MachineProgressWidget;
import liedge.limatech.menu.SidedUpgradableMachineMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SingleItemRecipeScreen<CTX extends SimpleRecipeMachineBlockEntity<?, ?>, M extends SidedUpgradableMachineMenu<CTX>> extends SidedUpgradableMachineScreen<M>
{
    public static final ResourceLocation SCREEN_TEXTURE = LimaTech.RESOURCES.textureLocation("gui", "single_input_machine");

    public SingleItemRecipeScreen(M menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.alignInventoryLabelRight = true;
    }

    @Override
    protected void addWidgets()
    {
        addSidebarWidgets();
        addRenderableOnly(new EnergyGaugeWidget(menu.menuContext().getEnergyStorage(), leftPos + 11, topPos + 10));
        addRenderableOnly(new MachineProgressWidget(menu.menuContext(), leftPos + 75, topPos + 39));
    }

    @Override
    public ResourceLocation getBgTexture()
    {
        return SCREEN_TEXTURE;
    }
}