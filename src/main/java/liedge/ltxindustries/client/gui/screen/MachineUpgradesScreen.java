package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.gui.widget.LimaBackButton;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrade;
import liedge.ltxindustries.menu.MachineUpgradeMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MachineUpgradesScreen extends UpgradesConfigScreen<MachineUpgrade, MachineUpgradeMenu>
{
    private static final ResourceLocation TEXTURE = LTXIndustries.RESOURCES.textureLocation("gui", "machine_upgrades");

    public MachineUpgradesScreen(MachineUpgradeMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
    }

    @Override
    protected int upgradeRemovalButtonId()
    {
        return 1;
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();

        addRenderableWidget(new LimaBackButton(leftPos + 6, topPos + 6, this, 0));
    }

    @Override
    public ResourceLocation getBgTexture()
    {
        return TEXTURE;
    }
}