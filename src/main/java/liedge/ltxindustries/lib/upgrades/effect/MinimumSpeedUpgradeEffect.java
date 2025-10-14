package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.tooltip.UpgradeTooltipsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Consumer;

public record MinimumSpeedUpgradeEffect(int minimumSpeed) implements UpgradeTooltipsProvider
{
    public static final Codec<MinimumSpeedUpgradeEffect> CODEC = ExtraCodecs.NON_NEGATIVE_INT.xmap(MinimumSpeedUpgradeEffect::new, MinimumSpeedUpgradeEffect::minimumSpeed);

    public static MinimumSpeedUpgradeEffect atLeast(int minimumSpeed)
    {
        return new MinimumSpeedUpgradeEffect(minimumSpeed);
    }

    @Override
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        lines.accept(LTXILangKeys.MINIMUM_MACHINE_SPEED_EFFECT.translateArgs(minimumSpeed));
    }
}