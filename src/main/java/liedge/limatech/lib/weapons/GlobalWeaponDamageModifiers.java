package liedge.limatech.lib.weapons;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limatech.LimaTech;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.CompoundValueOperation;
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
import org.jetbrains.annotations.NotNull;

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
    public static double applyGlobalModifiers(WeaponItem weaponItem, Entity target, double baseDamage, double totalDamage)
    {
        Holder<EntityType<?>> holder = target.getType().builtInRegistryHolder();
        List<WeaponDamageModifier> modifiers = holder.getData(DATA_MAP_TYPE);

        if (modifiers != null)
        {
            List<WeaponDamageModifier> list = modifiers.stream().filter(o -> o.weapon.map(item -> weaponItem == item).orElse(true)).sorted().toList();

            for (WeaponDamageModifier modifier : list)
            {
                totalDamage = modifier.operation.computeDouble(baseDamage, totalDamage, modifier.factor);
            }
        }

        return totalDamage;
    }

    public record WeaponDamageModifier(Optional<WeaponItem> weapon, float factor, CompoundValueOperation operation) implements Comparable<WeaponDamageModifier>
    {
        public static final Codec<WeaponDamageModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                WeaponItem.CODEC.optionalFieldOf("weapon").forGetter(WeaponDamageModifier::weapon),
                Codec.FLOAT.fieldOf("factor").forGetter(WeaponDamageModifier::factor),
                CompoundValueOperation.CODEC.fieldOf("operation").forGetter(WeaponDamageModifier::operation))
                .apply(instance, WeaponDamageModifier::new));

        public static WeaponDamageModifier create(Supplier<? extends WeaponItem> supplier, float factor, CompoundValueOperation operation)
        {
            return new WeaponDamageModifier(Optional.of(supplier.get()), factor, operation);
        }

        @Override
        public int compareTo(@NotNull GlobalWeaponDamageModifiers.WeaponDamageModifier o)
        {
            return this.operation.compareTo(o.operation);
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