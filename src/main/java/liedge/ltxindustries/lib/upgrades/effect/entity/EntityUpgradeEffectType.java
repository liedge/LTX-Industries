package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.MapCodec;

public record EntityUpgradeEffectType<T extends EntityUpgradeEffect>(MapCodec<T> codec)
{
    public static <T extends EntityUpgradeEffect> EntityUpgradeEffectType<T> create(MapCodec<T> codec)
    {
        return new EntityUpgradeEffectType<>(codec);
    }
}