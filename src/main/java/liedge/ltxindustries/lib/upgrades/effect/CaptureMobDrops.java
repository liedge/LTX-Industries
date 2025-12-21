package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.tooltip.UpgradeTooltipsProvider;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public enum CaptureMobDrops implements UpgradeTooltipsProvider
{
    INSTANCE;

    public static final Codec<CaptureMobDrops> CODEC = Codec.unit(INSTANCE);

    @Override
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        lines.accept(LTXILangKeys.CAPTURE_MOB_DROPS_EFFECT.translate().withStyle(LTXIConstants.LIME_GREEN.chatStyle()));
    }
}