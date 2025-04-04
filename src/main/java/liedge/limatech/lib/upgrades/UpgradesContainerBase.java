package liedge.limatech.lib.upgrades;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.limatech.lib.upgrades.effect.value.ComplexValueUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.value.SimpleValueUpgradeEffect;
import liedge.limatech.registry.game.LimaTechUpgradeEffectComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class UpgradesContainerBase<CTX, U extends UpgradeBase<CTX, U>>
{
    protected static <CTX, U extends UpgradeBase<CTX, U>, T extends UpgradesContainerBase<CTX, U>> Codec<T> createCodec(Codec<Holder<U>> upgradeCodec, Function<Object2IntMap<Holder<U>>, T> factory)
    {
        return LimaCoreCodecs.object2IntMap(upgradeCodec, UpgradeBase.UPGRADE_RANK_CODEC).xmap(factory, o -> o.internalMap);
    }

    protected static <CTX, U extends UpgradeBase<CTX, U>, T extends UpgradesContainerBase<CTX, U>> StreamCodec<RegistryFriendlyByteBuf, T> createStreamCodec(StreamCodec<RegistryFriendlyByteBuf, Holder<U>> upgradeStreamCodec, Function<Object2IntMap<Holder<U>>, T> factory)
    {
        return LimaStreamCodecs.object2IntMap(upgradeStreamCodec, UpgradeBase.UPGRADE_RANK_STREAM_CODEC).map(factory, o -> o.internalMap);
    }

    final Object2IntMap<Holder<U>> internalMap;

    protected UpgradesContainerBase(Object2IntMap<Holder<U>> internalMap)
    {
        this.internalMap = internalMap;
    }

    //#region General iteration helpers
    public <T> void forEachConditionalEffect(DataComponentType<List<ConditionalEffect<T>>> type, LootContext context, BiConsumer<T, Integer> consumer)
    {
        for (Object2IntMap.Entry<Holder<U>> entry : internalMap.object2IntEntrySet())
        {
            List<ConditionalEffect<T>> list = entry.getKey().value().getListEffect(type);
            for (ConditionalEffect<T> conditionalEffect : list)
            {
                if (conditionalEffect.matches(context))
                {
                    consumer.accept(conditionalEffect.effect(), entry.getIntValue());
                }
            }
        }
    }

    public <T> void forEachConditionalEffect(Supplier<? extends DataComponentType<List<ConditionalEffect<T>>>> typeSupplier, LootContext context, BiConsumer<T, Integer> consumer)
    {
        forEachConditionalEffect(typeSupplier.get(), context, consumer);
    }

    public <T> void forEachEffect(DataComponentType<List<T>> type, BiConsumer<T, Integer> consumer)
    {
        for (Object2IntMap.Entry<Holder<U>> entry : internalMap.object2IntEntrySet())
        {
            entry.getKey().value().getListEffect(type).forEach(effect -> consumer.accept(effect, entry.getIntValue()));
        }
    }

    public <T> void forEachEffect(Supplier<? extends DataComponentType<List<T>>> typeSupplier, BiConsumer<T, Integer> consumer)
    {
        forEachEffect(typeSupplier.get(), consumer);
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

    public <T> Stream<T> effectFlatStream(DataComponentType<List<T>> type)
    {
        return entryStream().flatMap(entry -> entry.getKey().value().getListEffect(type).stream());
    }

    public <T> Stream<EffectRankPair<T>> boxedFlatStream(DataComponentType<List<T>> type)
    {
        return entryStream().flatMap(entry -> {
            List<T> data = entry.getKey().value().getListEffect(type);
            return data.stream().map(effect -> new EffectRankPair<>(effect, entry.getIntValue()));
        });
    }

    public <T> Stream<EffectRankPair<T>> boxedFlatStream(Supplier<? extends DataComponentType<List<T>>> typeSupplier)
    {
        return boxedFlatStream(typeSupplier.get());
    }

    public <T> Stream<EffectRankPair<T>> boxedConditionalFlatStream(DataComponentType<List<ConditionalEffect<T>>> type, LootContext context)
    {
        return entryStream().flatMap(entry -> {
            List<ConditionalEffect<T>> data = entry.getKey().value().getListEffect(type);
            return data.stream().filter(e -> e.matches(context)).map(e -> new EffectRankPair<>(e.effect(), entry.getIntValue()));
        });
    }

    public <T> IntStream flatMapToInt(DataComponentType<List<T>> type, ToIntBiFunction<T, Integer> mapper)
    {
        return entryStream().flatMapToInt(entry -> {
            List<T> data = entry.getKey().value().getListEffect(type);
            return data.stream().mapToInt(effect -> mapper.applyAsInt(effect, entry.getIntValue()));
        });
    }
    //#endregion

    //#region Specialized iteration helpers
    public ItemEnchantments getEnchantments()
    {
        ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY.withTooltip(false));
        forEachEffect(LimaTechUpgradeEffectComponents.ENCHANTMENT_LEVEL, (effect, rank) -> effect.applyEnchantment(builder, rank));
        return builder.keySet().isEmpty() ? ItemEnchantments.EMPTY : builder.toImmutable();
    }

    public void applyEnchantments(ItemStack stack)
    {
        stack.set(DataComponents.ENCHANTMENTS, getEnchantments());
    }
    //#endregion

    //#region Value computing helpers
    public double applySimpleValue(DataComponentType<List<SimpleValueUpgradeEffect>> type, double base, double total)
    {
        List<EffectRankPair<SimpleValueUpgradeEffect>> list = boxedFlatStream(type).sorted(Comparator.comparing(entry -> entry.effect.operation())).toList();
        double result = total;

        for (EffectRankPair<SimpleValueUpgradeEffect> pair : list)
        {
            SimpleValueUpgradeEffect effect = pair.effect;
            result = effect.operation().computeDouble(base, result, effect.value().calculate(pair.upgradeRank));
        }

        return result;
    }

    public double applySimpleValue(DataComponentType<List<SimpleValueUpgradeEffect>> type, double base)
    {
        return applySimpleValue(type, base, base);
    }

    public double applySimpleValue(Supplier<? extends DataComponentType<List<SimpleValueUpgradeEffect>>> typeSupplier, double base, double total)
    {
        return applySimpleValue(typeSupplier.get(), base, total);
    }

    public double applySimpleValue(Supplier<? extends DataComponentType<List<SimpleValueUpgradeEffect>>> typeSupplier, double base)
    {
        return applySimpleValue(typeSupplier.get(), base);
    }

    public double applyComplexDamageContextValue(DataComponentType<List<ConditionalEffect<ComplexValueUpgradeEffect>>> type, ServerLevel serverLevel, Entity targetEntity, DamageSource damageSource, double base, double total)
    {
        List<EffectRankPair<ConditionalEffect<ComplexValueUpgradeEffect>>> list = boxedFlatStream(type).sorted(Comparator.comparing(pair -> pair.effect.effect().operation())).toList();
        double result = total;

        for (EffectRankPair<ConditionalEffect<ComplexValueUpgradeEffect>> pair : list)
        {
            LootContext context = Enchantment.damageContext(serverLevel, pair.upgradeRank, targetEntity, damageSource);
            if (pair.effect.matches(context))
            {
                ComplexValueUpgradeEffect effect = pair.effect.effect();
                result = effect.operation().computeDouble(base, result, effect.value().getFloat(context));
            }
        }

        return result;
    }
    //#endregion

    // Container properties
    public int size()
    {
        return internalMap.size();
    }

    public boolean hasUpgrade(Holder<U> upgradeHolder)
    {
        return internalMap.containsKey(upgradeHolder);
    }

    public boolean canInstallUpgrade(Holder<CTX> contextHolder, Holder<U> upgradeHolder)
    {
        if (!hasUpgrade(upgradeHolder))
        {
            boolean canInstall = upgradeHolder.value().canBeInstalledOn(contextHolder);
            boolean isCompatibleWithOthers = internalMap.keySet().stream().map(Holder::value).allMatch(upgrade -> upgrade.canBeInstalledAlongside(upgradeHolder));

            return canInstall && isCompatibleWithOthers;
        }

        return false;
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
        return internalMap.hashCode();
    }

    public record EffectRankPair<T>(T effect, int upgradeRank) {}
}