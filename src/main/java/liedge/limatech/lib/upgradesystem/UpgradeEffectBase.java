package liedge.limatech.lib.upgradesystem;

import net.minecraft.network.chat.Component;

import java.util.List;

public interface UpgradeEffectBase
{
    void appendEffectTooltip(int upgradeRank, List<Component> lines);
}