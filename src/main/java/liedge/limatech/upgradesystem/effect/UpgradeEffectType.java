package liedge.limatech.upgradesystem.effect;

import com.mojang.serialization.MapCodec;

public record UpgradeEffectType<T extends EquipmentUpgradeEffect>(MapCodec<T> codec) { }