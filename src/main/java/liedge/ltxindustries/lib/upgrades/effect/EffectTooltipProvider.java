package liedge.ltxindustries.lib.upgrades.effect;

import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * To be implemented by upgrade effect data component classes.
 */
public interface EffectTooltipProvider
{
    void appendEffectLines(int upgradeRank, Consumer<Component> linesConsumer);

    interface SingleLine extends EffectTooltipProvider
    {
        Component getEffectTooltip(int upgradeRank);

        @Override
        default void appendEffectLines(int upgradeRank, Consumer<Component> linesConsumer)
        {
            linesConsumer.accept(getEffectTooltip(upgradeRank));
        }
    }
}