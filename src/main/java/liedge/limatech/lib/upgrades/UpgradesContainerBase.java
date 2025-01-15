package liedge.limatech.lib.upgrades;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.limatech.lib.upgrades.effect.UpgradeEffectDataType;
import liedge.limatech.lib.upgrades.effect.value.ValueUpgradeEffect;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

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

    // Iteration helpers
    public <T> void forEachListEffect(UpgradeEffectDataType<List<T>> type, BiConsumer<T, Integer> consumer)
    {
        for (Object2IntMap.Entry<Holder<U>> entry : internalMap.object2IntEntrySet())
        {
            entry.getKey().value().effects().getListEffect(type).forEach(effect -> consumer.accept(effect, entry.getIntValue()));
        }
    }

    public <T> void forEachListEffect(Supplier<? extends UpgradeEffectDataType<List<T>>> typeSupplier, BiConsumer<T, Integer> consumer)
    {
        forEachListEffect(typeSupplier.get(), consumer);
    }

    public <T> boolean upgradeEffectTypePresent(UpgradeEffectDataType<T> type)
    {
        return entryStream().anyMatch(entry -> entry.getKey().value().effects().has(type));
    }

    public <T> boolean upgradeEffectTypeAbsent(UpgradeEffectDataType<T> type)
    {
        return entryStream().noneMatch(entry -> entry.getKey().value().effects().has(type));
    }

    public <T> Stream<T> effectStream(UpgradeEffectDataType<T> type)
    {
        return entryStream().map(entry -> entry.getKey().value().effects().get(type)).filter(Objects::nonNull);
    }

    public <T> Stream<T> effectFlatStream(UpgradeEffectDataType<List<T>> type)
    {
        return entryStream().flatMap(entry -> entry.getKey().value().effects().getListEffect(type).stream());
    }

    public <T> Stream<EffectRankPair<T>> boxedFlatStream(UpgradeEffectDataType<List<T>> type)
    {
        return entryStream().flatMap(entry -> {
            List<T> data = entry.getKey().value().effects().getListEffect(type);
            return data.stream().map(effect -> new EffectRankPair<>(effect, entry.getIntValue()));
        });
    }

    public <T> Stream<EffectRankPair<T>> boxedFlatStream(Supplier<? extends UpgradeEffectDataType<List<T>>> typeSupplier)
    {
        return boxedFlatStream(typeSupplier.get());
    }

    public <T> IntStream flatMapToInt(UpgradeEffectDataType<List<T>> type, ToIntBiFunction<T, Integer> mapper)
    {
        return entryStream().flatMapToInt(entry -> {
            List<T> data = entry.getKey().value().effects().getListEffect(type);
            return data.stream().mapToInt(effect -> mapper.applyAsInt(effect, entry.getIntValue()));
        });
    }

    public double runCompoundOps(ValueUpgradeEffect.DataType type, @Nullable Player player, @Nullable Entity targetEntity, double baseValue)
    {
        List<EffectRankPair<ValueUpgradeEffect>> list = boxedFlatStream(type).sorted(Comparator.comparing(entry -> entry.effect.operation())).toList();
        double result = baseValue;
        for (EffectRankPair<ValueUpgradeEffect> pair : list)
        {
            ValueUpgradeEffect effect = pair.effect;
            result = effect.operation().apply(baseValue, result, effect.calculate(player, targetEntity, pair.upgradeRank));
        }
        return result;
    }

    public double runCompoundOps(Supplier<? extends ValueUpgradeEffect.DataType> typeSupplier, @Nullable Player player, @Nullable Entity targetEntity, double baseValue)
    {
        return runCompoundOps(typeSupplier.get(), player, targetEntity, baseValue);
    }

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