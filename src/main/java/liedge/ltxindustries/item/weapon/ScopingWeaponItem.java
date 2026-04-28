package liedge.ltxindustries.item.weapon;

import liedge.limacore.lib.TickTimer;
import liedge.ltxindustries.lib.weapons.LTXIExtendedInput;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public interface ScopingWeaponItem extends ItemLike
{
    default float getBaseScopingFOV()
    {
        return 1f;
    }

    default boolean canScope(ItemStack heldItem, Player player, LTXIExtendedInput controls)
    {
        return controls.getReloadTimer().getTimerState() == TickTimer.State.STOPPED;
    }

    default double getMouseSensitivityModifier()
    {
        return 1d;
    }
}