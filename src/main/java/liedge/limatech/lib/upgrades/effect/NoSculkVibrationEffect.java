package liedge.limatech.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import liedge.limatech.client.LimaTechLang;
import net.minecraft.network.chat.Component;

public final class NoSculkVibrationEffect implements UpgradeEffect
{
    private static final NoSculkVibrationEffect INSTANCE = new NoSculkVibrationEffect();
    public static final Codec<NoSculkVibrationEffect> CODEC = Codec.unit(INSTANCE);

    public static NoSculkVibrationEffect preventSculkVibrations()
    {
        return INSTANCE;
    }

    private NoSculkVibrationEffect() {}

    @Override
    public Component defaultEffectTooltip(int upgradeRank)
    {
        return LimaTechLang.NO_SCULK_VIBRATIONS_EFFECT.translate();
    }
}