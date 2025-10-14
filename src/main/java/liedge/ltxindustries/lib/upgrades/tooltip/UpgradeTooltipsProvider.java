package liedge.ltxindustries.lib.upgrades.tooltip;

import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public interface UpgradeTooltipsProvider
{
    void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines);
}