package liedge.limatech.client.gui.screen;

import liedge.limatech.blockentity.RecomposerBlockEntity;
import liedge.limatech.menu.RecomposerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class RecomposerScreen extends SingleItemRecipeScreen<RecomposerBlockEntity, RecomposerMenu>
{
    public RecomposerScreen(RecomposerMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
    }
}