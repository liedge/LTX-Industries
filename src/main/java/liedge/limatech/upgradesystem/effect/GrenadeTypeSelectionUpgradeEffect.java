package liedge.limatech.upgradesystem.effect;

import com.google.common.base.Preconditions;
import com.mojang.serialization.MapCodec;
import liedge.limatech.item.weapon.GrenadeLauncherWeaponItem;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import liedge.limatech.upgradesystem.ItemEquipmentUpgrades;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static liedge.limatech.registry.LimaTechDataComponents.GRENADE_TYPE;

public class GrenadeTypeSelectionUpgradeEffect implements EquipmentUpgradeEffect
{
    public static final MapCodec<GrenadeTypeSelectionUpgradeEffect> CODEC = GrenadeType.CODEC.setOf().fieldOf("types").xmap(GrenadeTypeSelectionUpgradeEffect::new, o -> o.grenadeTypes);

    public static GrenadeTypeSelectionUpgradeEffect unlockSingle(GrenadeType type)
    {
        return new GrenadeTypeSelectionUpgradeEffect(EnumSet.of(type));
    }

    public static GrenadeTypeSelectionUpgradeEffect unlockMultiple(GrenadeType... types)
    {
        return new GrenadeTypeSelectionUpgradeEffect(EnumSet.copyOf(List.of(types)));
    }

    private final Set<GrenadeType> grenadeTypes;

    private GrenadeTypeSelectionUpgradeEffect(Set<GrenadeType> grenadeTypes)
    {
        Preconditions.checkArgument(!grenadeTypes.contains(GrenadeType.EXPLOSIVE), "Grenade type selection upgrade cannot contain Explosive since it is the default element.");
        this.grenadeTypes = grenadeTypes;
    }

    public Stream<GrenadeType> allowedGrenadeTypeSelections()
    {
        return grenadeTypes.stream();
    }

    @Override
    public void postUpgradeRemoved(ItemEquipmentUpgrades upgrades, ItemStack equipmentItem, int upgradeRank)
    {
        GrenadeType currentlyEquipped = GrenadeLauncherWeaponItem.getGrenadeTypeFromItem(equipmentItem);
        boolean shouldResetType = upgrades.noEffectMatches(GrenadeTypeSelectionUpgradeEffect.class, (effect, rank) -> effect.grenadeTypes.contains(currentlyEquipped), false);

        // Reset equipped type to explosive if remaining upgrades do not allow for selection
        if (shouldResetType)
        {
            equipmentItem.set(GRENADE_TYPE, GrenadeType.EXPLOSIVE);
        }
    }

    @Override
    public UpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.GRENADE_TYPE_SELECTION.get();
    }
}