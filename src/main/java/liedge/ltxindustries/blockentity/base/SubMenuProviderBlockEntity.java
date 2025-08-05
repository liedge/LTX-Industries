package liedge.ltxindustries.blockentity.base;

import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.blockentity.LimaBlockEntityAccess;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public interface SubMenuProviderBlockEntity extends LimaBlockEntityAccess
{
    default void returnToPrimaryMenuScreen(Player player)
    {
        LimaBlockEntity thisBlock = getAsLimaBlockEntity();
        Objects.requireNonNull(thisBlock.getType().createMenuProvider(thisBlock, false)).openMenuScreen(player);
    }
}