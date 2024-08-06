package liedge.limatech.upgradesystem.effect;

import com.mojang.serialization.MapCodec;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;

public class NoVibrationUpgradeEffect implements EquipmentUpgradeEffect
{
    public static final NoVibrationUpgradeEffect NO_VIBRATIONS_EFFECT = new NoVibrationUpgradeEffect();
    public static final MapCodec<NoVibrationUpgradeEffect> CODEC = MapCodec.unit(NO_VIBRATIONS_EFFECT);

    private NoVibrationUpgradeEffect() {}

    @Override
    public boolean preventsWeaponVibrationEvent(int upgradeRank)
    {
        return true;
    }

    @Override
    public UpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.NO_VIBRATION.get();
    }
}