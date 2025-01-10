package liedge.limatech.lib.weapons;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limatech.LimaTech;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.upgradesystem.calculation.CompoundCalculation;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapValueMerger;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class GlobalWeaponDamageModifiers
{
    private GlobalWeaponDamageModifiers() {}

    public static final Codec<List<WeaponDamageModifier>> CODEC = WeaponDamageModifier.CODEC.listOf();
    public static final AdvancedDataMapType<EntityType<?>, List<WeaponDamageModifier>, RemovalEntry> DATA_MAP_TYPE = AdvancedDataMapType.builder(LimaTech.RESOURCES.location("weapon_damage_modifier"), Registries.ENTITY_TYPE, CODEC)
            .synced(CODEC, false)
            .merger(DataMapValueMerger.listMerger())
            .remover(RemovalEntry.CODEC)
            .build();

    @SuppressWarnings("deprecation")
    public static List<CompoundCalculation> getModifiersForEntity(WeaponItem weaponItem, Entity target)
    {
        Holder<EntityType<?>> holder = target.getType().builtInRegistryHolder();
        List<WeaponDamageModifier> modifiers = holder.getData(DATA_MAP_TYPE);

        if (modifiers != null)
        {
            return modifiers.stream().filter(entry -> entry.weapon.map(item -> item == weaponItem).orElse(true)).map(WeaponDamageModifier::modifier).toList();
        }

        return List.of();
    }

    public record WeaponDamageModifier(Optional<WeaponItem> weapon, CompoundCalculation modifier)
    {
        public static final Codec<WeaponDamageModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                WeaponItem.CODEC.optionalFieldOf("weapon").forGetter(WeaponDamageModifier::weapon),
                CompoundCalculation.CODEC.fieldOf("modifier").forGetter(WeaponDamageModifier::modifier))
                .apply(instance, WeaponDamageModifier::new));

        public static WeaponDamageModifier create(Supplier<? extends WeaponItem> supplier, CompoundCalculation modifier)
        {
            return new WeaponDamageModifier(Optional.of(supplier.get()), modifier);
        }

        public static WeaponDamageModifier allWeapons(CompoundCalculation modifier)
        {
            return new WeaponDamageModifier(Optional.empty(), modifier);
        }
    }

    public record RemovalEntry(WeaponItem weaponItem) implements DataMapValueRemover<EntityType<?>, List<WeaponDamageModifier>>
    {
        public static final Codec<RemovalEntry> CODEC = WeaponItem.CODEC.xmap(RemovalEntry::new, RemovalEntry::weaponItem);

        @Override
        public Optional<List<WeaponDamageModifier>> remove(List<WeaponDamageModifier> value, Registry<EntityType<?>> registry, Either<TagKey<EntityType<?>>, ResourceKey<EntityType<?>>> source, EntityType<?> object)
        {
            List<WeaponDamageModifier> filteredList = value.stream().filter(entry -> entry.weapon.map(item -> weaponItem != item).orElse(true)).toList();
            return Optional.of(filteredList);
        }
    }
}