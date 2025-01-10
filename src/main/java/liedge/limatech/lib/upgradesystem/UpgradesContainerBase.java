package liedge.limatech.lib.upgradesystem;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.limatech.lib.upgradesystem.calculation.CompoundCalculation;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class UpgradesContainerBase<CTX, E extends UpgradeEffectBase, U extends UpgradeBase<CTX, E, U>>
{
    protected static <CTX, E extends UpgradeEffectBase, U extends UpgradeBase<CTX, E, U>, T extends UpgradesContainerBase<CTX, E, U>> Codec<T> createCodec(Codec<Holder<U>> upgradeCodec, Function<Object2IntMap<Holder<U>>, T> factory)
    {
        return LimaCoreCodecs.object2IntMap(upgradeCodec, UpgradeBase.UPGRADE_RANK_CODEC).xmap(factory, o -> o.internalMap);
    }

    protected static <CTX, E extends UpgradeEffectBase, U extends UpgradeBase<CTX, E, U>, T extends UpgradesContainerBase<CTX, E, U>> StreamCodec<RegistryFriendlyByteBuf, T> createStreamCodec(StreamCodec<RegistryFriendlyByteBuf, Holder<U>> upgradeStreamCodec, Function<Object2IntMap<Holder<U>>, T> factory)
    {
        return LimaStreamCodecs.object2IntMap(upgradeStreamCodec, UpgradeBase.UPGRADE_RANK_STREAM_CODEC).map(factory, o -> o.internalMap);
    }

    final Object2IntMap<Holder<U>> internalMap;

    protected UpgradesContainerBase(Object2IntMap<Holder<U>> internalMap)
    {
        this.internalMap = internalMap;
    }

    public void forEachEffect(UpgradeEffectConsumer<E> consumer)
    {
        for (Object2IntMap.Entry<Holder<U>> entry : internalMap.object2IntEntrySet())
        {
            entry.getKey().value().effects().forEach(effect -> consumer.accept(effect, entry.getIntValue()));
        }
    }

    public <T extends E> void forEachEffect(Class<T> effectClass, UpgradeEffectConsumer<T> consumer)
    {
        for (Object2IntMap.Entry<Holder<U>> entry : internalMap.object2IntEntrySet())
        {
            streamEffects(entry, effectClass).forEach(effect -> consumer.accept(effect, entry.getIntValue()));
        }
    }

    public boolean noEffectMatches(UpgradeEffectPredicate<E> predicate)
    {
        return entryStream().allMatch(entry -> streamEffects(entry).noneMatch(effect -> predicate.test(effect, entry.getIntValue())));
    }

    public <T extends E> boolean noEffectMatches(Class<T> effectClass, UpgradeEffectPredicate<T> predicate)
    {
        return entryStream().allMatch(entry -> streamEffects(entry, effectClass).noneMatch(effect -> predicate.test(effect, entry.getIntValue())));
    }

    public boolean anyEffectMatches(UpgradeEffectPredicate<E> predicate)
    {
        return entryStream().anyMatch(entry -> streamEffects(entry).anyMatch(effect -> predicate.test(effect, entry.getIntValue())));
    }

    public <T extends E> boolean anyEffectMatches(Class<T> effectClass, UpgradeEffectPredicate<T> predicate)
    {
        return entryStream().anyMatch(entry -> streamEffects(entry, effectClass).anyMatch(effect -> predicate.test(effect, entry.getIntValue())));
    }

    public IntStream flatMapEffectsToInt(UpgradeEffectToIntFunction<E> mapper)
    {
        return entryStream().flatMapToInt(entry -> streamEffects(entry).mapToInt(effect -> mapper.apply(effect, entry.getIntValue())));
    }

    public <T extends E> IntStream flatMapEffectsToInt(Class<T> effectClass, UpgradeEffectToIntFunction<T> mapper)
    {
        return entryStream().flatMapToInt(entry -> streamEffects(entry, effectClass).mapToInt(effect -> mapper.apply(effect, entry.getIntValue())));
    }

    public DoubleStream flatMapEffectsToDouble(UpgradeEffectToDoubleFunction<E> mapper)
    {
        return entryStream().flatMapToDouble(entry -> streamEffects(entry).mapToDouble(effect -> mapper.apply(effect, entry.getIntValue())));
    }

    public <T extends E> DoubleStream flatMapEffectsToDouble(Class<T> effectClass, UpgradeEffectToDoubleFunction<T> mapper)
    {
        return entryStream().flatMapToDouble(entry -> streamEffects(entry, effectClass).mapToDouble(effect -> mapper.apply(effect, entry.getIntValue())));
    }

    public <R> Stream<R> flatMapEffects(UpgradeEffectFunction<E, ? extends R> mapper)
    {
        return entryStream().flatMap(entry -> streamEffects(entry).map(effect -> mapper.apply(effect, entry.getIntValue())));
    }

    public <T extends E, R> Stream<R> flatMapEffects(Class<T> effectClass, UpgradeEffectFunction<? super T, ? extends R> mapper)
    {
        return entryStream().flatMap(entry -> streamEffects(entry, effectClass).map(effect -> mapper.apply(effect, entry.getIntValue())));
    }

    public <R> Stream<R> flatMapEffectsTwice(UpgradeEffectFunction<E, ? extends Stream<R>> flatMapper)
    {
        return entryStream().flatMap(entry -> streamEffects(entry).flatMap(effect -> flatMapper.apply(effect, entry.getIntValue())));
    }

    public <T extends E, R> Stream<R> flatMapEffectsTwice(Class<T> effectClass, UpgradeEffectFunction<? super T, ? extends Stream<R>> flatMapper)
    {
        return entryStream().flatMap(entry -> streamEffects(entry, effectClass).flatMap(effect -> flatMapper.apply(effect, entry.getIntValue())));
    }

    public <T extends E> List<CompoundCalculation.Step> flatMapToSortedCalculations(Class<T> effectClass, Function<? super T, ? extends CompoundCalculation> function)
    {
        return flatMapEffects(effectClass, (effect, rank) -> new CompoundCalculation.Step(function.apply(effect), rank)).filter(s -> !s.calculation().isEmpty()).toList();
    }

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

    public List<Object2IntMap.Entry<Holder<U>>> toEntryList()
    {
        return List.copyOf(internalMap.object2IntEntrySet());
    }

    private Stream<Object2IntMap.Entry<Holder<U>>> entryStream()
    {
        return internalMap.object2IntEntrySet().stream();
    }

    private Stream<E> streamEffects(Object2IntMap.Entry<Holder<U>> entry)
    {
        return entry.getKey().value().effects().stream();
    }

    private <T extends E> Stream<T> streamEffects(Object2IntMap.Entry<Holder<U>> entry, Class<T> effectClass)
    {
        return streamEffects(entry).filter(effectClass::isInstance).map(effectClass::cast);
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
        else if (obj instanceof UpgradesContainerBase<?,?,?> other)
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

    @FunctionalInterface
    public interface UpgradeEffectConsumer<T>
    {
        void accept(T effect, int upgradeRank);
    }

    @FunctionalInterface
    public interface UpgradeEffectFunction<T, R>
    {
        R apply(T effect, int upgradeRank);
    }

    @FunctionalInterface
    public interface UpgradeEffectToIntFunction<T>
    {
        int apply(T effect, int upgradeRank);
    }

    @FunctionalInterface
    public interface UpgradeEffectToDoubleFunction<T>
    {
        double apply(T effect, int upgradeRank);
    }

    @FunctionalInterface
    public interface UpgradeEffectPredicate<T>
    {
        boolean test(T effect, int upgradeRank);
    }
}