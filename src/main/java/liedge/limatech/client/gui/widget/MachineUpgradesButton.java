package liedge.limatech.client.gui.widget;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limatech.LimaTech;
import liedge.limatech.menu.UpgradableMachineMenu;
import liedge.limatech.registry.game.LimaTechMenus;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;

public class MachineUpgradesButton extends LimaSidebarButton
{
    private static final ResourceLocation ICON_SPRITE = LimaTech.RESOURCES.location("machine_upgrade_module");

    private final LimaMenuScreen<?> parent;

    public MachineUpgradesButton(int x, int y, LimaMenuScreen<?> parent)
    {
        super(x, y, LimaTechMenus.MACHINE_UPGRADES.get().translate(), ICON_SPRITE);
        this.parent = parent;
        setTooltip(Tooltip.create(getMessage()));
    }

    @Override
    public void onPress(int button)
    {
        parent.sendUnitButtonData(UpgradableMachineMenu.UPGRADES_BUTTON_ID);
    }
}