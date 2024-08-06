package liedge.limatech.upgradesystem.effect;

import com.mojang.serialization.MapCodec;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import liedge.limatech.lib.weapons.WeaponDamageSource;
import net.minecraft.world.entity.Entity;

public class NoAngerUpgradeEffect implements EquipmentUpgradeEffect
{
    public static final NoAngerUpgradeEffect NO_ANGER_EFFECT = new NoAngerUpgradeEffect();
    public static final MapCodec<NoAngerUpgradeEffect> CODEC = MapCodec.unit(NO_ANGER_EFFECT);

    private NoAngerUpgradeEffect() {}

    @Override
    public void modifyDamageSource(WeaponDamageSource damageSource, Entity targetEntity, int upgradeRank)
    {
        damageSource.setNoAnger(true);
    }

    @Override
    public UpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.NO_ANGER.get();
    }
}