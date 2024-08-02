package liedge.limatech.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ScrollModeSwitchItem
{
    void switchItemMode(ItemStack stack, Player player, int delta);
}