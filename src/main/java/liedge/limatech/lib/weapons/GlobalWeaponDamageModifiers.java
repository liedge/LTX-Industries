package liedge.limatech.lib.weapons;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import liedge.limatech.LimaTech;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.upgradesystem.calculation.CompoundCalculation;
import liedge.limatech.lib.upgradesystem.calculation.EmptyCalculation;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.Map;
import java.util.function.Supplier;

public final class GlobalWeaponDamageModifiers
{
    public static final Codec<GlobalWeaponDamageModifiers> CODEC = Codec.unboundedMap(WeaponItem.CODEC, CompoundCalculation.CODEC).xmap(GlobalWeaponDamageModifiers::new, o -> o.modifierMap);
    public static final DataMapType<EntityType<?>, GlobalWeaponDamageModifiers> DATA_MAP_TYPE = DataMapType.builder(LimaTech.RESOURCES.location("weapon_damage_modifiers"), Registries.ENTITY_TYPE, CODEC).synced(CODEC, false).build();

    public static Builder builder()
    {
        return new Builder();
    }

    @SuppressWarnings("deprecation")
    public static CompoundCalculation getModifierForEntity(WeaponItem weaponItem, Entity target)
    {
        Holder<EntityType<?>> holder = target.getType().builtInRegistryHolder();
        GlobalWeaponDamageModifiers modifiers = holder.getData(DATA_MAP_TYPE);

        if (modifiers != null && modifiers.modifierMap.containsKey(weaponItem))
        {
            return modifiers.modifierMap.get(weaponItem);
        }
        else
        {
            return EmptyCalculation.empty();
        }
    }

    private final Map<WeaponItem, CompoundCalculation> modifierMap;

    private GlobalWeaponDamageModifiers(Map<WeaponItem, CompoundCalculation> modifierMap)
    {
        this.modifierMap = modifierMap;
    }

    public static class Builder
    {
        private final Map<WeaponItem, CompoundCalculation> map = new Object2ObjectOpenHashMap<>();

        private Builder() {}

        public Builder add(WeaponItem weaponItem, CompoundCalculation modifier)
        {
            map.put(weaponItem, modifier);
            return this;
        }

        public Builder add(Supplier<? extends WeaponItem> supplier, CompoundCalculation modifier)
        {
            return add(supplier.get(), modifier);
        }

        public GlobalWeaponDamageModifiers build()
        {
            return new GlobalWeaponDamageModifiers(ImmutableMap.copyOf(map));
        }
    }
}