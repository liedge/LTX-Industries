package liedge.limatech.client.gui.widget;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limacore.client.gui.UnmanagedSprite;
import liedge.limatech.menu.SidedUpgradableMachineMenu;
import liedge.limatech.registry.LimaTechMenus;
import net.minecraft.client.gui.components.Tooltip;

public class MachineUpgradesButton extends LimaSidebarButton
{
    private final LimaMenuScreen<?> parent;

    public MachineUpgradesButton(int x, int y, LimaMenuScreen<?> parent)
    {
        super(x, y, LimaTechMenus.MACHINE_UPGRADES.get().translate());
        this.parent = parent;
        setTooltip(Tooltip.create(getMessage()));
    }

    @Override
    protected UnmanagedSprite iconSprite()
    {
        return ScreenWidgetSprites.SIDEBAR_ICON_UPGRADES;
    }

    @Override
    public void onPress(int button)
    {
        parent.sendUnitButtonData(SidedUpgradableMachineMenu.UPGRADES_BUTTON_ID);
    }
}