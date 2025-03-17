package liedge.limatech.lib.weapons;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limatech.LimaTech;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.CompoundValueOperation;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapValueMerger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public final class GlobalWeaponDamageModifiers
{
    private GlobalWeaponDamageModifiers() {}

    public static final Codec<List<WeaponDamageModifier>> CODEC = WeaponDamageModifier.CODEC.listOf();
    public static final AdvancedDataMapType<EntityType<?>, List<WeaponDamageModifier>, ?> DATA_MAP_TYPE = AdvancedDataMapType.builder(LimaTech.RESOURCES.location("weapon_damage_modifier"), Registries.ENTITY_TYPE, CODEC)
            .synced(CODEC, false)
            .merger(DataMapValueMerger.listMerger())
            .build();

    @SuppressWarnings("deprecation")
    public static double applyGlobalModifiers(WeaponItem weaponItem, Entity target, LootContext context, double baseDamage, double totalDamage)
    {
        Holder<EntityType<?>> holder = target.getType().builtInRegistryHolder();
        List<WeaponDamageModifier> modifiers = holder.getData(DATA_MAP_TYPE);

        if (modifiers != null)
        {
            List<WeaponDamageModifier> toApply = modifiers.stream().filter(o -> o.condition.map(c -> c.test(context)).orElse(true) && o.weaponItem.map(weaponItem::equals).orElse(true)).sorted().toList();

            for (WeaponDamageModifier modifier : toApply)
            {
                totalDamage = modifier.operation.computeDouble(baseDamage, totalDamage, modifier.value.getFloat(context));
            }
        }

        return totalDamage;
    }

    public record WeaponDamageModifier(Optional<WeaponItem> weaponItem, Optional<LootItemCondition> condition, NumberProvider value, CompoundValueOperation operation) implements Comparable<WeaponDamageModifier>
    {
        public static final Codec<WeaponDamageModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                WeaponItem.CODEC.optionalFieldOf("weapon").forGetter(WeaponDamageModifier::weaponItem),
                LootItemCondition.DIRECT_CODEC.optionalFieldOf("condition").forGetter(WeaponDamageModifier::condition),
                NumberProviders.CODEC.fieldOf("value").forGetter(WeaponDamageModifier::value),
                CompoundValueOperation.CODEC.fieldOf("op").forGetter(WeaponDamageModifier::operation))
                .apply(instance, WeaponDamageModifier::new));

        public static Builder modifier(CompoundValueOperation operation)
        {
            return new Builder(operation);
        }

        @Override
        public int compareTo(@NotNull GlobalWeaponDamageModifiers.WeaponDamageModifier o)
        {
            return this.operation.compareTo(o.operation);
        }

        public static final class Builder
        {
            private WeaponItem weaponItem;
            private NumberProvider amount;
            private final List<LootItemCondition> conditions = new ObjectArrayList<>();
            private final CompoundValueOperation operation;

            private Builder(CompoundValueOperation operation)
            {
                this.operation = operation;
            }

            public Builder forWeapon(WeaponItem weaponItem)
            {
                this.weaponItem = weaponItem;
                return this;
            }

            public Builder forWeapon(Supplier<? extends WeaponItem> weaponSupplier)
            {
                return forWeapon(weaponSupplier.get());
            }

            public Builder onlyIf(LootItemCondition.Builder condition)
            {
                this.conditions.add(condition.build());
                return this;
            }

            public Builder withAmount(NumberProvider provider)
            {
                this.amount = provider;
                return this;
            }

            public Builder withConstantAmount(float amount)
            {
                return withAmount(ConstantValue.exactly(amount));
            }

            public WeaponDamageModifier build()
            {
                Optional<LootItemCondition> condition = switch (conditions.size())
                {
                    case 0 -> Optional.empty();
                    case 1 -> Optional.of(conditions.getFirst());
                    default -> Optional.of(AllOfCondition.allOf(conditions));
                };

                return new WeaponDamageModifier(Optional.ofNullable(weaponItem),
                        condition,
                        Objects.requireNonNull(amount, "NumberProvider for weapon damage modifier cannot be null."),
                        operation);
            }
        }
    }
}