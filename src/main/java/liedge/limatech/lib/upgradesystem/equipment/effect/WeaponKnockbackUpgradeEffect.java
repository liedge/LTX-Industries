package liedge.limatech.lib.upgradesystem.equipment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.upgradesystem.calculation.CompoundCalculation;
import liedge.limatech.lib.weapons.WeaponDamageSource;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.List;

public record WeaponKnockbackUpgradeEffect(boolean ignoreResistance, CompoundCalculation modifier) implements EquipmentUpgradeEffect
{
    public static final MapCodec<WeaponKnockbackUpgradeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("ignore_resistance", false).forGetter(WeaponKnockbackUpgradeEffect::ignoreResistance),
            CompoundCalculation.CODEC.fieldOf("modifier").forGetter(WeaponKnockbackUpgradeEffect::modifier))
            .apply(instance, WeaponKnockbackUpgradeEffect::new));

    @Override
    public void modifyDamageSource(WeaponDamageSource damageSource, Entity targetEntity, int upgradeRank)
    {
        if (ignoreResistance) damageSource.setBypassKnockbackResistance(true);
    }

    @Override
    public EquipmentUpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.WEAPON_KNOCKBACK_MODIFIER.get();
    }

    @Override
    public void appendEffectTooltip(int upgradeRank, List<Component> lines)
    {
        if (ignoreResistance) lines.add(LimaTechLang.WEAPON_KNOCKBACK_IGNORE_RESIST_EFFECT.translate().withStyle(ChatFormatting.GREEN));
        lines.add(LimaTechLang.WEAPON_KNOCKBACK_EFFECT.translateArgs(modifier.getTooltip(upgradeRank)));
    }
}