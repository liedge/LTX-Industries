package liedge.limatech.lib.upgradesystem.equipment.effect;

import com.mojang.serialization.MapCodec;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import liedge.limatech.lib.weapons.WeaponDamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.List;

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
    public EquipmentUpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.NO_ANGER.get();
    }

    @Override
    public void appendEffectTooltip(int upgradeRank, List<Component> lines)
    {
        lines.add(LimaTechLang.NO_ANGER_EFFECT.translate());
    }
}