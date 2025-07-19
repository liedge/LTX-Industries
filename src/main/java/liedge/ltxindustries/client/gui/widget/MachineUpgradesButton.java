package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.menu.UpgradableMachineMenu;
import liedge.ltxindustries.registry.game.LTXIMenus;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;

public class MachineUpgradesButton extends LimaSidebarButton
{
    private static final ResourceLocation ICON_SPRITE = LTXIndustries.RESOURCES.location("machine_upgrade_module");

    private final LimaMenuScreen<?> parent;

    public MachineUpgradesButton(int x, int y, LimaMenuScreen<?> parent)
    {
        super(x, y, LTXIMenus.MACHINE_UPGRADES.get().translate(), ICON_SPRITE);
        this.parent = parent;
        setTooltip(Tooltip.create(getMessage()));
    }

    @Override
    public void onPress(int button)
    {
        parent.sendUnitButtonData(UpgradableMachineMenu.UPGRADES_BUTTON_ID);
    }
}