package liedge.limatech.lib.upgradesystem.equipment.effect;

import com.mojang.serialization.MapCodec;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.upgradesystem.calculation.CompoundCalculation;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import net.minecraft.network.chat.Component;

import java.util.List;

public record WeaponDamageUpgradeEffect(CompoundCalculation modifier) implements EquipmentUpgradeEffect
{
    public static final MapCodec<WeaponDamageUpgradeEffect> CODEC = CompoundCalculation.CODEC.fieldOf("modifier").xmap(WeaponDamageUpgradeEffect::new, WeaponDamageUpgradeEffect::modifier);

    @Override
    public EquipmentUpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.WEAPON_DAMAGE_MODIFIER.get();
    }

    @Override
    public void appendEffectTooltip(int upgradeRank, List<Component> lines)
    {
        lines.add(LimaTechLang.WEAPON_DAMAGE_EFFECT.translateArgs(modifier.getTooltip(upgradeRank)));
    }
}