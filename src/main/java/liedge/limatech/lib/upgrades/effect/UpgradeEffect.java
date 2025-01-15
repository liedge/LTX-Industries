package liedge.limatech.lib.upgrades.effect;

import net.minecraft.network.chat.Component;

public interface UpgradeEffect
{
    default Component defaultEffectTooltip(int upgradeRank)
    {
        return Component.empty();
    }
}