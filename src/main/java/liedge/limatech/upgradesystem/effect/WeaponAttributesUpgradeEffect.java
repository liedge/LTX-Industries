package liedge.limatech.upgradesystem.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.WeaponAttribute;
import liedge.limatech.lib.weapons.WeaponContextCalculation;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class WeaponAttributesUpgradeEffect implements EquipmentUpgradeEffect
{
    public static final MapCodec<WeaponAttributesUpgradeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WeaponAttribute.CODEC.fieldOf("attribute").forGetter(o -> o.attribute),
            WeaponContextCalculation.CODEC.fieldOf("calculation").forGetter(o -> o.calculation))
            .apply(instance, WeaponAttributesUpgradeEffect::new));

    public static WeaponAttributesUpgradeEffect modifyAttribute(WeaponAttribute attribute, WeaponContextCalculation calculation)
    {
        return new WeaponAttributesUpgradeEffect(attribute, calculation);
    }

    private final WeaponAttribute attribute;
    private final WeaponContextCalculation calculation;

    private WeaponAttributesUpgradeEffect(WeaponAttribute attribute, WeaponContextCalculation calculation)
    {
        this.attribute = attribute;
        this.calculation = calculation;
    }

    @Override
    public double modifyWeaponAttribute(WeaponItem weaponItem, WeaponAttribute attribute, @Nullable Entity targetEntity, int upgradeRank, double baseValue)
    {
        return this.attribute.equals(attribute) ? calculation.doCalculation(weaponItem, baseValue, upgradeRank, targetEntity) : 0d;
    }

    @Override
    public UpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.WEAPON_ATTRIBUTE_MODIFIER.get();
    }
}