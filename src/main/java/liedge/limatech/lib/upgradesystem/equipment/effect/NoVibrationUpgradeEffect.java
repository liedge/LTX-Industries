package liedge.limatech.lib.upgradesystem.equipment.effect;

import com.mojang.serialization.MapCodec;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import net.minecraft.network.chat.Component;

import java.util.List;

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
    public EquipmentUpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.NO_VIBRATION.get();
    }

    @Override
    public void appendEffectTooltip(int upgradeRank, List<Component> lines)
    {
        lines.add(LimaTechLang.NO_SCULK_VIBRATIONS_EFFECT.translate());
    }
}