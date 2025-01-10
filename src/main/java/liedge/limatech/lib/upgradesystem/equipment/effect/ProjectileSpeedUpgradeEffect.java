package liedge.limatech.lib.upgradesystem.equipment.effect;

import com.mojang.serialization.MapCodec;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.upgradesystem.calculation.CompoundCalculation;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import net.minecraft.network.chat.Component;

import java.util.List;

public record ProjectileSpeedUpgradeEffect(CompoundCalculation modifier) implements EquipmentUpgradeEffect
{
    public static final MapCodec<ProjectileSpeedUpgradeEffect> CODEC = CompoundCalculation.CODEC.fieldOf("modifier").xmap(ProjectileSpeedUpgradeEffect::new, ProjectileSpeedUpgradeEffect::modifier);

    @Override
    public EquipmentUpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.PROJECTILE_SPEED_MODIFIER.get();
    }

    @Override
    public void appendEffectTooltip(int upgradeRank, List<Component> lines)
    {
        lines.add(LimaTechLang.PROJECTILE_SPEED_EFFECT.translateArgs(modifier.getTooltip(upgradeRank)));
    }
}