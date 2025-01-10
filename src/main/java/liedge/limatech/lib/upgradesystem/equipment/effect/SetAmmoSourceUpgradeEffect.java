package liedge.limatech.lib.upgradesystem.equipment.effect;

import com.mojang.serialization.MapCodec;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.upgradesystem.equipment.EquipmentUpgrades;
import liedge.limatech.lib.weapons.WeaponAmmoSource;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static liedge.limatech.LimaTechConstants.CREATIVE_PINK;
import static liedge.limatech.LimaTechConstants.REM_BLUE;
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
    public void postUpgradeInstall(EquipmentUpgrades upgrades, ItemStack equipmentItem, int upgradeRank)
    {
        WeaponAmmoSource newSource = upgrades.flatMapEffects(SetAmmoSourceUpgradeEffect.class, (effect, rank) -> effect.ammoSource).max(Comparator.comparing(Function.identity())).orElse(WeaponAmmoSource.NORMAL);
        equipmentItem.set(WEAPON_AMMO_SOURCE, newSource);
    }

    @Override
    public void postUpgradeRemoved(EquipmentUpgrades upgrades, ItemStack equipmentItem, int upgradeRank)
    {
        WeaponAmmoSource newSource = upgrades.flatMapEffects(SetAmmoSourceUpgradeEffect.class, (effect, rank) -> effect.ammoSource).max(Comparator.comparing(Function.identity())).orElse(WeaponAmmoSource.NORMAL);
        equipmentItem.set(WEAPON_AMMO_SOURCE, newSource);
    }

    @Override
    public EquipmentUpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.SET_AMMO_SOURCE.get();
    }

    @Override
    public void appendEffectTooltip(int upgradeRank, List<Component> lines)
    {
        if (ammoSource == WeaponAmmoSource.COMMON_ENERGY_UNIT)
        {
            lines.add(LimaTechLang.ENERGY_AMMO_EFFECT.translate().withStyle(REM_BLUE.chatStyle()));
        }
        else if (ammoSource == WeaponAmmoSource.INFINITE)
        {
            lines.add(LimaTechLang.INFINITE_AMMO_EFFECT.translate().withStyle(CREATIVE_PINK.chatStyle()));
        }
    }
}