package liedge.ltxindustries.lib;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.math.MathOperation;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

public record EquipmentDamageModifier(Optional<ItemPredicate> equipmentPredicate, Optional<LootItemCondition> condition,
                                      NumberProvider value, MathOperation operation) implements Comparable<EquipmentDamageModifier>, BiPredicate<ItemStack, LootContext>
{
    public static final Codec<EquipmentDamageModifier> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemPredicate.CODEC.optionalFieldOf("equipment_predicate").forGetter(EquipmentDamageModifier::equipmentPredicate),
            ConditionalEffect.conditionCodec(LootContextParamSets.ENTITY).optionalFieldOf("condition").forGetter(EquipmentDamageModifier::condition),
            NumberProviders.CODEC.fieldOf("value").forGetter(EquipmentDamageModifier::value),
            MathOperation.COMPOUND_OP_CODEC.fieldOf("op").forGetter(EquipmentDamageModifier::operation))
            .apply(instance, EquipmentDamageModifier::new));
    public static final Codec<Optional<EquipmentDamageModifier>> CODEC = ConditionalOps.createConditionalCodec(DIRECT_CODEC);

    public static Builder builder(NumberProvider value, MathOperation operation)
    {
        return new Builder(value, operation);
    }

    public static Builder builder(float constantValue, MathOperation operation)
    {
        return builder(ConstantValue.exactly(constantValue), operation);
    }

    @Override
    public int compareTo(@NotNull EquipmentDamageModifier o)
    {
        return this.operation.compareTo(o.operation);
    }

    @Override
    public boolean test(ItemStack stack, LootContext context)
    {
        boolean a = equipmentPredicate.map(o -> o.test(stack)).orElse(true);
        boolean b = condition.map(o -> o.test(context)).orElse(true);

        return a && b;
    }

    public static final class Builder
    {
        private final NumberProvider value;
        private final MathOperation operation;
        private @Nullable ItemPredicate equipmentPredicate;
        private final List<LootItemCondition> conditions = new ObjectArrayList<>();

        private Builder(NumberProvider value, MathOperation operation)
        {
            this.value = value;
            this.operation = operation;
        }

        public Builder withEquipmentPredicate(ItemPredicate.Builder builder)
        {
            this.equipmentPredicate = builder.build();
            return this;
        }

        public Builder withCondition(LootItemCondition.Builder condition)
        {
            this.conditions.add(condition.build());
            return this;
        }

        public Builder forEquipmentItem(ItemLike item)
        {
            return withEquipmentPredicate(ItemPredicate.Builder.item().of(item));
        }

        public Builder forEquipmentTag(TagKey<Item> tagKey)
        {
            return withEquipmentPredicate(ItemPredicate.Builder.item().of(tagKey));
        }

        public Builder againstEntities(TagKey<EntityType<?>> tagKey)
        {
            return withCondition(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().of(tagKey)));
        }

        public EquipmentDamageModifier build()
        {
            Optional<LootItemCondition> condition = switch (conditions.size())
            {
                case 0 -> Optional.empty();
                case 1 -> Optional.of(conditions.getFirst());
                default -> Optional.of(AllOfCondition.allOf(conditions));
            };

            return new EquipmentDamageModifier(Optional.ofNullable(equipmentPredicate), condition, value, operation);
        }
    }
}