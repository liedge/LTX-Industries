package liedge.ltxindustries.lib.upgrades;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.lib.math.MathOperation;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.lib.upgrades.effect.*;
import liedge.ltxindustries.lib.upgrades.effect.entity.EntityUpgradeEffect;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.registries.holdersets.AnyHolderSet;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class UpgradesContainerBase<CTX, U extends UpgradeBase<CTX, U>>
{
    protected static <CTX, U extends UpgradeBase<CTX, U>, T extends UpgradesContainerBase<CTX, U>> Codec<T> createCodec(Codec<Holder<U>> upgradeCodec, Function<Object2IntMap<Holder<U>>, T> factory)
    {
        return LimaCoreCodecs.object2IntLinkedHashMap(upgradeCodec, UpgradeBase.UPGRADE_RANK_CODEC).xmap(factory, UpgradesContainerBase::getMapForCloning);
    }

    protected static <CTX, U extends UpgradeBase<CTX, U>, T extends UpgradesContainerBase<CTX, U>> StreamCodec<RegistryFriendlyByteBuf, T> createStreamCodec(StreamCodec<RegistryFriendlyByteBuf, Holder<U>> upgradeStreamCodec, Function<Object2IntMap<Holder<U>>, T> factory)
    {
        return LimaStreamCodecs.object2IntLinkedMap(upgradeStreamCodec, UpgradeBase.UPGRADE_RANK_STREAM_CODEC).map(factory, UpgradesContainerBase::getMapForCloning);
    }

    private final Object2IntMap<Holder<U>> internalMap;
    private final int mapHash;

    protected UpgradesContainerBase(Object2IntMap<Holder<U>> internalMap)
    {
        this.internalMap = internalMap;
        this.mapHash = internalMap.hashCode();
    }

    //#region General iteration helpers
    public <T> void forEachEffect(DataComponentType<List<T>> type, EffectVisitor<T> visitor)
    {
        for (Object2IntMap.Entry<Holder<U>> entry : internalMap.object2IntEntrySet())
        {
            List<T> effects = entry.getKey().value().getListEffect(type);
            final int rank = entry.getIntValue();

            for (T effect: effects)
            {
                visitor.accept(effect, rank);
            }
        }
    }

    public <T> void forEachEffect(Supplier<? extends DataComponentType<List<T>>> typeSupplier, EffectVisitor<T> visitor)
    {
        forEachEffect(typeSupplier.get(), visitor);
    }

    public <T, C extends EffectConditionHolder<T>> void forEachConditionalEffect(DataComponentType<List<C>> type, LootContext context, EffectVisitor<T> visitor)
    {
        for (Object2IntMap.Entry<Holder<U>> entry : internalMap.object2IntEntrySet())
        {
            int rank = entry.getIntValue();
            List<C> effects = entry.getKey().value().getListEffect(type);
            for (C holder : effects)
            {
                if (holder.test(context)) visitor.accept(holder.effect(), rank);
            }
        }
    }

    public <T, C extends EffectConditionHolder<T>> void forEachConditionalEffect(Supplier<? extends DataComponentType<List<C>>> typeSupplier, LootContext context, EffectVisitor<T> visitor)
    {
        forEachConditionalEffect(typeSupplier.get(), context, visitor);
    }

    public <T> boolean anySpecialMatch(DataComponentType<T> type, EffectPredicate<T> predicate)
    {
        for (var entry : internalMap.object2IntEntrySet())
        {
            T effect = entry.getKey().value().effects().get(type);
            if (effect != null && predicate.test(effect, entry.getIntValue())) return true;
        }

        return false;
    }

    public <T> boolean anySpecialMatch(Supplier<? extends DataComponentType<T>> typeSupplier, EffectPredicate<T> predicate)
    {
        return anySpecialMatch(typeSupplier.get(), predicate);
    }

    public <T> boolean anyMatch(DataComponentType<List<T>> type, EffectPredicate<T> predicate)
    {
        for (Object2IntMap.Entry<Holder<U>> entry : internalMap.object2IntEntrySet())
        {
            List<T> list = entry.getKey().value().getListEffect(type);
            final int rank = entry.getIntValue();

            for (T effect : list)
            {
                if (predicate.test(effect, rank)) return true;
            }
        }

        return false;
    }

    public <T> boolean anyMatch(Supplier<? extends DataComponentType<List<T>>> typeSupplier, EffectPredicate<T> predicate)
    {
        return anyMatch(typeSupplier.get(), predicate);
    }

    public <T> boolean noneMatch(DataComponentType<List<T>> type, EffectPredicate<T> predicate)
    {
        return !anyMatch(type, predicate);
    }

    public <T> boolean noneMatch(Supplier<? extends DataComponentType<List<T>>> typeSupplier, EffectPredicate<T> predicate)
    {
        return noneMatch(typeSupplier.get(), predicate);
    }

    public <T> boolean upgradeEffectTypePresent(DataComponentType<T> type)
    {
        return entryStream().anyMatch(entry -> entry.getKey().value().effects().has(type));
    }

    public <T> boolean upgradeEffectTypePresent(Supplier<? extends DataComponentType<T>> typeSupplier)
    {
        return upgradeEffectTypePresent(typeSupplier.get());
    }

    public <T> boolean upgradeEffectTypeAbsent(DataComponentType<T> type)
    {
        return entryStream().noneMatch(entry -> entry.getKey().value().effects().has(type));
    }

    public <T> boolean upgradeEffectTypeAbsent(Supplier<? extends DataComponentType<T>> typeSupplier)
    {
        return upgradeEffectTypeAbsent(typeSupplier.get());
    }

    public <T> Stream<T> effectStream(DataComponentType<T> type)
    {
        return entryStream().map(entry -> entry.getKey().value().effects().get(type)).filter(Objects::nonNull);
    }

    public <T> Stream<T> effectStream(Supplier<? extends DataComponentType<T>> typeSupplier)
    {
        return effectStream(typeSupplier.get());
    }

    public <T> Stream<T> listEffectStream(DataComponentType<List<T>> type)
    {
        return entryStream().flatMap(entry -> entry.getKey().value().getListEffect(type).stream());
    }

    public <T> Stream<T> listEffectStream(Supplier<? extends DataComponentType<List<T>>> typeSupplier)
    {
        return listEffectStream(typeSupplier.get());
    }

    public <T> Stream<EffectRankPair<T>> effectPairs(DataComponentType<List<T>> type)
    {
        return entryStream().flatMap(entry -> {
            List<T> data = entry.getKey().value().getListEffect(type);
            return data.stream().map(effect -> new EffectRankPair<>(effect, entry.getIntValue()));
        });
    }

    public <T> Stream<EffectRankPair<T>> effectPairs(Supplier<? extends DataComponentType<List<T>>> typeSupplier)
    {
        return effectPairs(typeSupplier.get());
    }

    public <T, C extends EffectConditionHolder<T>> Stream<EffectRankPair<T>> matchingEffectPairs(DataComponentType<List<C>> type, LootContext context)
    {
        return entryStream().mapMulti((entry, consumer) ->
        {
            List<C> data = entry.getKey().value().getListEffect(type);
            for (C holder : data)
            {
                if (holder.test(context)) consumer.accept(new EffectRankPair<>(holder.effect(), entry.getIntValue()));
            }
        });
    }

    public <T, C extends EffectConditionHolder<T>> Stream<EffectRankPair<T>> matchingEffectPairs(Supplier<? extends DataComponentType<List<C>>> typeSupplier, LootContext context)
    {
        return matchingEffectPairs(typeSupplier.get(), context);
    }
    //#endregion

    //#region Specialized iteration helpers
    public ItemEnchantments getEnchantments()
    {
        ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY.withTooltip(false));
        effectPairs(LTXIUpgradeEffectComponents.ENCHANTMENT_LEVELS)
                .sorted(Comparator.comparing(EffectRankPair::effect, AddEnchantmentLevels.DESCENDING_MAX_LEVELS_COMPARATOR))
                .forEach(pair -> pair.effect().applyEnchantment(builder, pair.upgradeRank()));
        return builder.keySet().isEmpty() ? ItemEnchantments.EMPTY : builder.toImmutable();
    }

    public void applyEnchantments(ItemStack stack)
    {
        stack.set(DataComponents.ENCHANTMENTS, getEnchantments());
    }

    public <T, R> HolderSet<R> mergeEffectHolderSets(DataComponentType<List<T>> type, Function<T, HolderSet<R>> mapper)
    {
        List<HolderSet<R>> sets = new ObjectArrayList<>();

        for (Object2IntMap.Entry<Holder<U>> entry : internalMap.object2IntEntrySet())
        {
            List<T> effects = entry.getKey().value().getListEffect(type);
            for (T effect : effects)
            {
                HolderSet<R> set = mapper.apply(effect);
                if (set instanceof AnyHolderSet<R>) return set;
                else sets.add(set);
            }
        }

        return LimaRegistryUtil.mergeHolderSets(sets);
    }

    public <T, R> HolderSet<R> mergeEffectHolderSets(Supplier<? extends DataComponentType<List<T>>> typeSupplier, Function<T, HolderSet<R>> mapper)
    {
        return mergeEffectHolderSets(typeSupplier.get(), mapper);
    }

    public void applyDamageEntityEffects(DataComponentType<List<TargetableEffect<EntityUpgradeEffect>>> type, ServerLevel level, LootContext context, EffectTarget source, UpgradedEquipmentInUse equipmentInUse)
    {
        for (var entry : internalMap.object2IntEntrySet())
        {
            List<TargetableEffect<EntityUpgradeEffect>> effects = entry.getKey().value().getListEffect(type);
            int rank = entry.getIntValue();

            for (TargetableEffect<EntityUpgradeEffect> data : effects)
            {
                if (data.source() == source && data.test(context))
                {
                    Entity affectedEntity = data.affected().apply(context);
                    if (affectedEntity != null) data.effect().apply(level, context, rank, affectedEntity, equipmentInUse);
                }
            }
        }
    }

    public void applyDamageEntityEffects(Supplier<? extends DataComponentType<List<TargetableEffect<EntityUpgradeEffect>>>> typeSupplier, ServerLevel level, LootContext context, EffectTarget source, UpgradedEquipmentInUse equipmentInUse)
    {
        applyDamageEntityEffects(typeSupplier.get(), level, context, source, equipmentInUse);
    }

    public float applyDamageReduction(LootContext context, UpgradedEquipmentInUse equipmentInUse)
    {
        float pieceTotal = 0f;

        for (var entry : internalMap.object2IntEntrySet())
        {
            List<ConditionEffect<DamageReduction>> effects = entry.getKey().value().getListEffect(LTXIUpgradeEffectComponents.DAMAGE_REDUCTION);
            int rank = entry.getIntValue();

            for (ConditionEffect<DamageReduction> data : effects)
            {
                if (data.test(context)) pieceTotal += data.effect().apply(rank, equipmentInUse);

                if (pieceTotal >= 1f)
                {
                    return 1f;
                }
            }
        }

        return pieceTotal;
    }

    public void tickEquipment(ServerLevel level, LootContext context, Entity entity, UpgradedEquipmentInUse equipmentInUse)
    {
        forEachConditionalEffect(LTXIUpgradeEffectComponents.EQUIPMENT_TICK, context, (effect, rank) ->
                effect.apply(level, context, rank, entity, equipmentInUse));
    }

    public void applyReductionBreaches(LootContext context, LivingIncomingDamageEvent event)
    {
        Map<DamageContainer.Reduction, Float> reductions = new EnumMap<>(DamageContainer.Reduction.class);
        forEachConditionalEffect(LTXIUpgradeEffectComponents.REDUCTION_BREACH, context, (effect, rank) ->
                reductions.merge(effect.reduction().getReduction(), effect.get(rank), Float::sum));

        for (var entry : reductions.entrySet())
        {
            float modifier = Mth.clamp(-entry.getValue(), -1f, 0f);
            event.addReductionModifier(entry.getKey(), (type, reduction) -> reduction + (reduction * modifier));
        }
    }
    //#endregion

    //#region Value computing helpers
    public double runValueOps(DataComponentType<List<ValueOperation>> type, LootContext context, double base, double total)
    {
        double result = total;
        List<EffectRankPair<ValueOperation>> list = effectPairs(type)
                .sorted(MathOperation.comparingPriority(p -> p.effect().operation()))
                .toList();

        for (EffectRankPair<ValueOperation> pair : list)
        {
            ValueOperation effect = pair.effect();
            result = effect.apply(context, pair.upgradeRank(), base, result);
        }

        return result;
    }

    public double runValueOps(DataComponentType<List<ValueOperation>> type, LootContext context, double base)
    {
        return runValueOps(type, context, base, base);
    }

    public double runValueOps(Supplier<? extends DataComponentType<List<ValueOperation>>> typeSupplier, LootContext context, double base, double total)
    {
        return runValueOps(typeSupplier.get(), context, base, total);
    }

    public double runValueOps(Supplier<? extends DataComponentType<List<ValueOperation>>> typeSupplier, LootContext context, double base)
    {
        return runValueOps(typeSupplier.get(), context, base);
    }

    public double runConditionalValueOps(DataComponentType<List<ConditionEffect<ValueOperation>>> type, LootContext context, double base, double total)
    {
        double result = total;
        List<EffectRankPair<ValueOperation>> pairs = matchingEffectPairs(type, context)
                .sorted(MathOperation.comparingPriority(o -> o.effect().operation()))
                .toList();

        for (EffectRankPair<ValueOperation> pair : pairs)
        {
            ValueOperation effect = pair.effect();
            result = effect.apply(context, pair.upgradeRank(), base, result);
        }

        return result;
    }

    public double runConditionalValueOps(DataComponentType<List<ConditionEffect<ValueOperation>>> type, LootContext context, double base)
    {
        return runConditionalValueOps(type, context, base, base);
    }

    public double runConditionalValueOps(Supplier<? extends DataComponentType<List<ConditionEffect<ValueOperation>>>> typeSupplier, LootContext context, double base, double total)
    {
        return runConditionalValueOps(typeSupplier.get(), context, base, total);
    }

    public double runConditionalValueOps(Supplier<? extends DataComponentType<List<ConditionEffect<ValueOperation>>>> typeSupplier, LootContext context, double base)
    {
        return runConditionalValueOps(typeSupplier.get(), context, base);
    }
    //#endregion

    // Container properties
    public abstract MutableUpgradesContainer<U, ? extends UpgradesContainerBase<CTX, U>> toMutableContainer();

    public int size()
    {
        return internalMap.size();
    }

    public boolean isEmpty()
    {
        return internalMap.isEmpty();
    }

    public boolean canInstallUpgrade(Holder<CTX> contextHolder, UpgradeBaseEntry<U> entry)
    {
        Holder<U> upgrade = entry.upgrade();

        // Check rank
        if (entry.upgradeRank() <= getUpgradeRank(upgrade)) return false;

        // Check general context holder set compatibility
        if (!upgrade.value().canBeInstalledOn(contextHolder)) return false;

        // Check every upgrade for compatibility
        U existing = upgrade.value();
        return internalMap.keySet().stream().map(Holder::value).filter(o -> !existing.equals(o)).allMatch(o -> o.canBeInstalledAlongside(upgrade));
    }

    public int getUpgradeRank(Holder<U> upgradeHolder)
    {
        return internalMap.getOrDefault(upgradeHolder, 0);
    }

    public Set<Object2IntMap.Entry<Holder<U>>> toEntrySet()
    {
        return Collections.unmodifiableSet(internalMap.object2IntEntrySet());
    }

    private Stream<Object2IntMap.Entry<Holder<U>>> entryStream()
    {
        return internalMap.object2IntEntrySet().stream();
    }

    @ApiStatus.Internal
    Object2IntMap<Holder<U>> getMapForCloning()
    {
        return internalMap;
    }

    @Override
    public final String toString()
    {
        return entryStream().map(entry -> LimaRegistryUtil.getNonNullRegistryId(entry.getKey()) + "(" + entry.getIntValue() + ")").collect(Collectors.joining(","));
    }

    @Override
    public final boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        else if (obj instanceof UpgradesContainerBase<?,?> other)
        {
            return this.internalMap.equals(other.internalMap);
        }
        else
        {
            return false;
        }
    }

    @Override
    public final int hashCode()
    {
        return mapHash;
    }

    @FunctionalInterface
    public interface EffectPredicate<T>
    {
        boolean test(T effect, int upgradeRank);
    }

    @FunctionalInterface
    public interface EffectVisitor<T>
    {
        void accept(T effect, int upgradeRank);
    }
}