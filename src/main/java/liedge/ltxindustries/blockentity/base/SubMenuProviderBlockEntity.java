package liedge.ltxindustries.blockentity.base;

import liedge.limacore.blockentity.LimaBlockEntityAccess;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

import java.util.Objects;

public interface SubMenuProviderBlockEntity extends LimaBlockEntityAccess
{
    default void returnToPrimaryMenuScreen(ServerPlayer player)
    {
        MenuProvider provider = Objects.requireNonNull(getAsLimaBlockEntity().getMenuProvider(false));
        player.openMenu(provider);
    }
}