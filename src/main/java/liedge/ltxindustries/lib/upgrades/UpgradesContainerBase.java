package liedge.ltxindustries.lib.upgrades;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.lib.function.ObjectIntConsumer;
import liedge.limacore.lib.function.ObjectIntFunction;
import liedge.limacore.lib.math.MathOperation;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.lib.upgrades.effect.AddEnchantmentLevels;
import liedge.ltxindustries.lib.upgrades.effect.ValueOperation;
import liedge.ltxindustries.lib.upgrades.effect.entity.EntityUpgradeEffect;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
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
    public <T> void forEachEffect(DataComponentType<List<T>> type, ObjectIntConsumer<T> consumer)
    {
        for (Object2IntMap.Entry<Holder<U>> entry : internalMap.object2IntEntrySet())
        {
            List<T> effects = entry.getKey().value().getListEffect(type);
            final int rank = entry.getIntValue();

            for (T effect: effects)
            {
                consumer.acceptWithInt(effect, rank);
            }
        }
    }

    public <T> void forEachEffect(Supplier<? extends DataComponentType<List<T>>> typeSupplier, ObjectIntConsumer<T> consumer)
    {
        forEachEffect(typeSupplier.get(), consumer);
    }

    public <T> void forEachConditionalEffect(DataComponentType<List<ConditionalEffect<T>>> type, LootContext context, ObjectIntConsumer<T> consumer)
    {
        for (Object2IntMap.Entry<Holder<U>> entry : internalMap.object2IntEntrySet())
        {
            int rank = entry.getIntValue();
            List<ConditionalEffect<T>> list = entry.getKey().value().getListEffect(type);
            for (ConditionalEffect<T> conditionalEffect : list)
            {
                if (conditionalEffect.matches(context)) consumer.acceptWithInt(conditionalEffect.effect(), rank);
            }
        }
    }

    public <T> void forEachConditionalEffect(Supplier<? extends DataComponentType<List<ConditionalEffect<T>>>> typeSupplier, LootContext context, ObjectIntConsumer<T> consumer)
    {
        forEachConditionalEffect(typeSupplier.get(), context, consumer);
    }

    public <T> boolean anyMatch(DataComponentType<List<T>> type, ObjectIntFunction<T, Boolean> predicate)
    {
        for (Object2IntMap.Entry<Holder<U>> entry : internalMap.object2IntEntrySet())
        {
            List<T> list = entry.getKey().value().getListEffect(type);
            final int rank = entry.getIntValue();

            if (list.stream().anyMatch(effect -> predicate.applyWithInt(effect, rank))) return true;
        }

        return false;
    }

    public <T> boolean noneMatch(DataComponentType<List<T>> type, ObjectIntFunction<T, Boolean> predicate)
    {
        return !anyMatch(type, predicate);
    }

    public <T> boolean upgradeEffectTypePresent(DataComponentType<T> type)
    {
        return entryStream().anyMatch(entry -> entry.getKey().value().effects().has(type));
    }

    public <T> boolean upgradeEffectTypeAbsent(DataComponentType<T> type)
    {
        return entryStream().noneMatch(entry -> entry.getKey().value().effects().has(type));
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

    public <T> Stream<EffectRankPair<T>> matchingEffectPairs(DataComponentType<List<ConditionalEffect<T>>> type, LootContext context)
    {
        return entryStream().flatMap(entry ->
        {
            List<ConditionalEffect<T>> data = entry.getKey().value().getListEffect(type);
            return data.stream().filter(o -> o.matches(context)).map(o -> new EffectRankPair<>(o.effect(), entry.getIntValue()));
        });
    }

    public <T> Stream<EffectRankPair<T>> matchingEffectPairs(Supplier<? extends DataComponentType<List<ConditionalEffect<T>>>> typeSupplier, LootContext context)
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

    public void applyDamageEntityEffects(DataComponentType<List<TargetedConditionalEffect<EntityUpgradeEffect>>> type, ServerLevel level, LootContext context, EnchantmentTarget enchanted)
    {
        for (var entry : internalMap.object2IntEntrySet())
        {
            List<TargetedConditionalEffect<EntityUpgradeEffect>> effects = entry.getKey().value().getListEffect(type);
            int rank = entry.getIntValue();

            for (TargetedConditionalEffect<EntityUpgradeEffect> tce : effects)
            {
                if (tce.enchanted() == enchanted && tce.matches(context))
                {
                    Entity entity = switch (tce.affected())
                    {
                        case ATTACKER -> context.getParamOrNull(LootContextParams.ATTACKING_ENTITY);
                        case DAMAGING_ENTITY -> context.getParamOrNull(LootContextParams.DIRECT_ATTACKING_ENTITY);
                        case VICTIM -> context.getParamOrNull(LootContextParams.THIS_ENTITY);
                    };

                    if (entity != null) tce.effect().applyEntityEffect(level, entity, rank, context);
                }
            }
        }
    }

    public void applyDamageEntityEffects(Supplier<? extends DataComponentType<List<TargetedConditionalEffect<EntityUpgradeEffect>>>> typeSupplier, ServerLevel level, LootContext context, EnchantmentTarget enchanted)
    {
        applyDamageEntityEffects(typeSupplier.get(), level, context, enchanted);
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

    public double runConditionalValueOps(DataComponentType<List<ConditionalEffect<ValueOperation>>> type, LootContext context, double base, double total)
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

    public double runConditionalValueOps(DataComponentType<List<ConditionalEffect<ValueOperation>>> type, LootContext context, double base)
    {
        return runConditionalValueOps(type, context, base, base);
    }

    public double runConditionalValueOps(Supplier<? extends DataComponentType<List<ConditionalEffect<ValueOperation>>>> typeSupplier, LootContext context, double base, double total)
    {
        return runConditionalValueOps(typeSupplier.get(), context, base, total);
    }

    public double runConditionalValueOps(Supplier<? extends DataComponentType<List<ConditionalEffect<ValueOperation>>>> typeSupplier, LootContext context, double base)
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
}