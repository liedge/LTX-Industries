package liedge.limatech.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.weapons.WeaponAmmoSource;
import net.minecraft.network.chat.Component;

import static liedge.limatech.LimaTechConstants.CREATIVE_PINK;
import static liedge.limatech.LimaTechConstants.REM_BLUE;

public record AmmoSourceUpgradeEffect(WeaponAmmoSource ammoSource) implements UpgradeEffect
{
    public static final Codec<AmmoSourceUpgradeEffect> CODEC = WeaponAmmoSource.CODEC.xmap(AmmoSourceUpgradeEffect::new, AmmoSourceUpgradeEffect::ammoSource);

    @Override
    public Component defaultEffectTooltip(int upgradeRank)
    {
        return switch (ammoSource)
        {
            case NORMAL -> Component.empty();
            case COMMON_ENERGY_UNIT -> LimaTechLang.ENERGY_AMMO_EFFECT.translate().withStyle(REM_BLUE.chatStyle());
            case INFINITE -> LimaTechLang.INFINITE_AMMO_EFFECT.translate().withStyle(CREATIVE_PINK.chatStyle());
        };
    }
}