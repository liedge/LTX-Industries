package liedge.limatech.upgradesystem.effect;

import com.mojang.serialization.MapCodec;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import liedge.limatech.upgradesystem.ItemEquipmentUpgrades;
import liedge.limatech.lib.weapons.WeaponAmmoSource;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.function.Function;

import static liedge.limatech.registry.LimaTechDataComponents.WEAPON_AMMO_SOURCE;

public class SetAmmoSourceUpgradeEffect implements EquipmentUpgradeEffect
{
    public static final MapCodec<SetAmmoSourceUpgradeEffect> CODEC = WeaponAmmoSource.CODEC.fieldOf("ammo_source").xmap(SetAmmoSourceUpgradeEffect::new, o -> o.ammoSource);

    private final WeaponAmmoSource ammoSource;

    public SetAmmoSourceUpgradeEffect(WeaponAmmoSource ammoSource)
    {
        this.ammoSource = ammoSource;
    }

    @Override
    public void postUpgradeInstall(ItemEquipmentUpgrades upgrades, ItemStack equipmentItem, int upgradeRank)
    {
        WeaponAmmoSource newSource = upgrades.flatMapEffects(SetAmmoSourceUpgradeEffect.class, (effect, rank) -> effect.ammoSource).max(Comparator.comparing(Function.identity())).orElse(WeaponAmmoSource.NORMAL);
        equipmentItem.set(WEAPON_AMMO_SOURCE, newSource);
    }

    @Override
    public void postUpgradeRemoved(ItemEquipmentUpgrades upgrades, ItemStack equipmentItem, int upgradeRank)
    {
        WeaponAmmoSource newSource = upgrades.flatMapEffects(SetAmmoSourceUpgradeEffect.class, (effect, rank) -> effect.ammoSource).max(Comparator.comparing(Function.identity())).orElse(WeaponAmmoSource.NORMAL);
        equipmentItem.set(WEAPON_AMMO_SOURCE, newSource);
    }

    @Override
    public UpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.SET_AMMO_SOURCE.get();
    }
}