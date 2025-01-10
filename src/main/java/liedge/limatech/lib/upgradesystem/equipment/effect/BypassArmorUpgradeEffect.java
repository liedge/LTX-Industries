package liedge.limatech.lib.upgradesystem.equipment.effect;

import com.mojang.serialization.MapCodec;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.weapons.WeaponDamageSource;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.List;

import static liedge.limacore.util.LimaMathUtil.FORMAT_PERCENTAGE;

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
    public EquipmentUpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.BYPASS_ARMOR.get();
    }

    @Override
    public void appendEffectTooltip(int upgradeRank, List<Component> lines)
    {
        float bypass = amount.calculate(upgradeRank);

        if (bypass == 0)
        {
            lines.add(LimaTechLang.NATURAL_ARMOR_BYPASS_EFFECT.translate());
        }
        else
        {
            lines.add(LimaTechLang.ARMOR_BYPASS_EFFECT.translateArgs(FORMAT_PERCENTAGE.format(bypass)));
        }
    }
}