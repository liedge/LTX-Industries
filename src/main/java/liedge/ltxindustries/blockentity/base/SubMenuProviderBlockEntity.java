package liedge.ltxindustries.blockentity.base;

import liedge.limacore.blockentity.LimaBlockEntityAccess;
import liedge.limacore.inventory.menu.LimaMenuProvider;
import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.world.entity.player.Player;

public interface SubMenuProviderBlockEntity extends LimaBlockEntityAccess
{
    default void returnToPrimaryMenuScreen(Player player)
    {
        LimaCoreUtil.castOrThrow(LimaMenuProvider.class, getAsLimaBlockEntity()).openMenuScreen(player);
    }
}