package liedge.limatech.lib.upgrades.effect;

import net.minecraft.network.chat.Component;

/**
 * To be implemented by upgrade effect data component classes.
 */
public interface EffectTooltipProvider
{
    Component getEffectTooltip(int upgradeRank);
}