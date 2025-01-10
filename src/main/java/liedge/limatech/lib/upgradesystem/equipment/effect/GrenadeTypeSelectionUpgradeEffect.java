package liedge.limatech.lib.upgradesystem.equipment.effect;

import com.google.common.base.Preconditions;
import com.mojang.serialization.MapCodec;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.item.weapon.GrenadeLauncherWeaponItem;
import liedge.limatech.lib.upgradesystem.equipment.EquipmentUpgrades;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
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
    public void postUpgradeRemoved(EquipmentUpgrades upgrades, ItemStack equipmentItem, int upgradeRank)
    {
        GrenadeType currentlyEquipped = GrenadeLauncherWeaponItem.getGrenadeTypeFromItem(equipmentItem);
        boolean shouldResetType = upgrades.noEffectMatches(GrenadeTypeSelectionUpgradeEffect.class, (effect, rank) -> effect.grenadeTypes.contains(currentlyEquipped));

        // Reset equipped type to explosive if remaining upgrades do not allow for selection
        if (shouldResetType)
        {
            equipmentItem.set(GRENADE_TYPE, GrenadeType.EXPLOSIVE);
        }
    }

    @Override
    public EquipmentUpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.GRENADE_TYPE_SELECTION.get();
    }

    @Override
    public void appendEffectTooltip(int upgradeRank, List<Component> lines)
    {
        lines.add(LimaTechLang.GRENADE_UNLOCK_EFFECT.translateArgs(ComponentUtils.formatList(grenadeTypes, GrenadeType::translate)));
    }
}