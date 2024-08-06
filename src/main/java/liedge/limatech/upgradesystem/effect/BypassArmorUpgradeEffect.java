package liedge.limatech.upgradesystem.effect;

import com.mojang.serialization.MapCodec;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import liedge.limatech.lib.weapons.WeaponDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public class BypassArmorUpgradeEffect implements EquipmentUpgradeEffect
{
    public static final MapCodec<BypassArmorUpgradeEffect> CODEC = LevelBasedValue.CODEC.xmap(BypassArmorUpgradeEffect::new, o -> o.amount).fieldOf("amount");

    private final LevelBasedValue amount;

    public BypassArmorUpgradeEffect(LevelBasedValue amount)
    {
        this.amount = amount;
    }

    @Override
    public void modifyDamageSource(WeaponDamageSource damageSource, Entity targetEntity, int upgradeRank)
    {
        if (targetEntity instanceof LivingEntity living)
        {
            float baseReduction = (float) living.getAttributeBaseValue(Attributes.ARMOR);
            float multiplicativeReduction = (float) living.getAttributeValue(Attributes.ARMOR) * amount.calculate(upgradeRank);

            damageSource.setArmorReduction(baseReduction + multiplicativeReduction);
        }
    }

    @Override
    public UpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.BYPASS_ARMOR.get();
    }
}