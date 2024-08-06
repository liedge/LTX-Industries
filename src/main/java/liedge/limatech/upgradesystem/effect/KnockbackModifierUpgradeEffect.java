package liedge.limatech.upgradesystem.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limatech.lib.weapons.WeaponContextCalculation;
import liedge.limatech.lib.weapons.WeaponDamageSource;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import net.minecraft.world.entity.Entity;

public class KnockbackModifierUpgradeEffect implements EquipmentUpgradeEffect
{
    public static final MapCodec<KnockbackModifierUpgradeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("ignore_resistance", false).forGetter(o -> o.ignoreResistance),
            WeaponContextCalculation.CODEC.fieldOf("modifier").forGetter(o -> o.modifier))
            .apply(instance, KnockbackModifierUpgradeEffect::new));

    private final boolean ignoreResistance;
    private final WeaponContextCalculation modifier;

    public KnockbackModifierUpgradeEffect(boolean ignoreResistance, WeaponContextCalculation modifier)
    {
        this.ignoreResistance = ignoreResistance;
        this.modifier = modifier;
    }

    @Override
    public void modifyDamageSource(WeaponDamageSource damageSource, Entity targetEntity, int upgradeRank)
    {
        if (ignoreResistance) damageSource.setBypassKnockbackResistance(true);
        damageSource.setKnockbackMultiplier(modifier.doCalculation(damageSource.weaponItem(), 1d, upgradeRank, targetEntity));
    }

    @Override
    public UpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.KNOCKBACK_MODIFIER.get();
    }
}