package liedge.limatech.upgradesystem;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.limatech.registry.LimaTechDataComponents;
import liedge.limatech.upgradesystem.effect.EquipmentUpgradeEffect;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class ItemEquipmentUpgrades
{
    public static final Codec<ItemEquipmentUpgrades> CODEC = LimaCoreCodecs.object2IntMap(EquipmentUpgrade.CODEC, EquipmentUpgrade.UPGRADE_RANK_CODEC).xmap(ItemEquipmentUpgrades::new, o -> o.upgradeMap);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemEquipmentUpgrades> STREAM_CODEC = LimaStreamCodecs.object2IntMap(EquipmentUpgrade.STREAM_CODEC, EquipmentUpgrade.UPGRADE_RANK_STREAM_CODEC).map(ItemEquipmentUpgrades::new, o -> o.upgradeMap);

    public static final ItemEquipmentUpgrades EMPTY = new ItemEquipmentUpgrades(new Object2IntOpenHashMap<>());

    public static Builder builder()
    {
        return new Builder(new Object2IntOpenHashMap<>());
    }

    public static ItemEquipmentUpgrades getFromItem(ItemStack stack)
    {
        return stack.getOrDefault(LimaTechDataComponents.EQUIPMENT_UPGRADES, EMPTY);
    }

    private final Object2IntMap<Holder<EquipmentUpgrade>> upgradeMap;

    private ItemEquipmentUpgrades(Object2IntMap<Holder<EquipmentUpgrade>> upgradeMap)
    {
        this.upgradeMap = upgradeMap;
    }

    public void forEachEffect(UpgradeEffectConsumer<EquipmentUpgradeEffect> consumer)
    {
        for (Object2IntMap.Entry<Holder<EquipmentUpgrade>> entry : upgradeMap.object2IntEntrySet())
        {
            entry.getKey().value().effects().forEach(effect -> consumer.accept(effect, entry.getIntValue()));
        }
    }

    public <E extends EquipmentUpgradeEffect> void forEachEffect(Class<E> effectClass, UpgradeEffectConsumer<E> consumer)
    {
        for (Object2IntMap.Entry<Holder<EquipmentUpgrade>> entry : upgradeMap.object2IntEntrySet())
        {
            entry.getKey().value().effects().stream().filter(effectClass::isInstance).forEach(effect -> consumer.accept(effectClass.cast(effect), entry.getIntValue()));
        }
    }

    public boolean noEffectMatches(UpgradeEffectPredicate<EquipmentUpgradeEffect> predicate)
    {
        return streamEntries().allMatch(entry -> entry.getKey().value().effects().stream().noneMatch(effect -> predicate.testEffect(effect, entry.getIntValue())));
    }

    public <E extends EquipmentUpgradeEffect> boolean noEffectMatches(Class<E> effectClass, UpgradeEffectPredicate<? super E> predicate, boolean failOnInvalidCast)
    {
        return streamEntries().allMatch(entry -> entry.getKey().value().effects().stream().noneMatch(effect -> {
            if (effectClass.isInstance(effect))
            {
                return predicate.testEffect(effectClass.cast(effect), entry.getIntValue());
            }
            else
            {
                return failOnInvalidCast;
            }
        }));
    }

    public boolean anyEffectMatches(UpgradeEffectPredicate<EquipmentUpgradeEffect> predicate)
    {
        return streamEntries().anyMatch(entry -> entry.getKey().value().effects().stream().anyMatch(effect -> predicate.testEffect(effect, entry.getIntValue())));
    }

    public <E extends EquipmentUpgradeEffect> boolean anyEffectMatches(Class<E> effectClass, UpgradeEffectPredicate<E> predicate)
    {
        return streamEntries().anyMatch(entry -> entry.getKey().value().effects().stream().filter(effectClass::isInstance).anyMatch(effect -> predicate.testEffect(effectClass.cast(effect), entry.getIntValue())));
    }

    public IntStream flatMapEffectsToInt(UpgradeEffectToIntFunction<EquipmentUpgradeEffect> mapper)
    {
        return streamEntries().flatMapToInt(entry -> entry.getKey().value().effects().stream().mapToInt(effect -> mapper.apply(effect, entry.getIntValue())));
    }

    public DoubleStream flatMapEffectsToDouble(UpgradeEffectToDoubleFunction<EquipmentUpgradeEffect> mapper)
    {
        return streamEntries().flatMapToDouble(entry -> entry.getKey().value().effects().stream().mapToDouble(effect -> mapper.apply(effect, entry.getIntValue())));
    }

    public <E extends EquipmentUpgradeEffect> DoubleStream flatMapEffectsToDouble(Class<E> effectClass, UpgradeEffectToDoubleFunction<E> mapper)
    {
        return streamEntries().flatMapToDouble(entry -> entry.getKey().value().effects().stream().filter(effectClass::isInstance).mapToDouble(effect -> mapper.apply(effectClass.cast(effect), entry.getIntValue())));
    }

    public <T> Stream<T> flatMapEffects(UpgradeEffectFunction<EquipmentUpgradeEffect, ? extends T> mapper)
    {
        return streamEntries().flatMap(entry -> entry.getKey().value().effects().stream().map(effect -> mapper.apply(effect, entry.getIntValue())));
    }

    public <E extends EquipmentUpgradeEffect, T> Stream<T> flatMapEffects(Class<E> effectClass, UpgradeEffectFunction<? super E, ? extends T> mapper)
    {
        return streamEntries().flatMap(entry -> entry.getKey().value().effects().stream().filter(effectClass::isInstance).map(effect -> mapper.apply(effectClass.cast(effect), entry.getIntValue())));
    }

    public <E extends EquipmentUpgradeEffect, T> Stream<T> flatMapEffectsTwice(Class<E> effectClass, UpgradeEffectFunction<? super E, ? extends Stream<T>> streamMapper)
    {
        return streamEntries().flatMap(entry -> entry.getKey().value().effects().stream().filter(effectClass::isInstance).flatMap(effect -> streamMapper.apply(effectClass.cast(effect), entry.getIntValue())));
    }

    public int size()
    {
        return upgradeMap.size();
    }

    public boolean canInstallUpgrade(ItemStack equipmentItem, Holder<EquipmentUpgrade> upgradeHolder)
    {
        if (!hasUpgrade(upgradeHolder))
        {
            boolean canInstall = upgradeHolder.value().canBeInstalledOn(equipmentItem);
            boolean isCompatibleWithOthers = upgradeMap.keySet().stream().map(Holder::value).allMatch(upgrade -> upgrade.canBeInstalledAlongside(upgradeHolder));

            return canInstall && isCompatibleWithOthers;
        }

        return false;
    }

    public boolean hasUpgrade(Holder<EquipmentUpgrade> upgradeHolder)
    {
        return upgradeMap.containsKey(upgradeHolder);
    }

    public int getUpgradeRank(Holder<EquipmentUpgrade> upgradeHolder)
    {
        return upgradeMap.getOrDefault(upgradeHolder, 0);
    }

    public List<EquipmentUpgradeEntry> toEntryList()
    {
        return upgradeMap.object2IntEntrySet().stream().map(entry -> new EquipmentUpgradeEntry(entry.getKey(), entry.getIntValue())).toList();
    }

    public Builder asBuilder()
    {
        return new Builder(new Object2IntOpenHashMap<>(upgradeMap));
    }

    private Stream<Object2IntMap.Entry<Holder<EquipmentUpgrade>>> streamEntries()
    {
        return upgradeMap.object2IntEntrySet().stream();
    }

    @Override
    public String toString()
    {
        return streamEntries().map(entry -> LimaRegistryUtil.getNonNullRegistryId(entry.getKey()) + "(" + entry.getIntValue() + ")").collect(Collectors.joining(","));
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        else if (obj instanceof ItemEquipmentUpgrades other)
        {
            return this.upgradeMap.equals(other.upgradeMap);
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return upgradeMap.hashCode();
    }

    public static class Builder
    {
        private final Object2IntMap<Holder<EquipmentUpgrade>> map;

        private Builder(Object2IntMap<Holder<EquipmentUpgrade>> map)
        {
            this.map = map;
        }

        public Builder add(Holder<EquipmentUpgrade> upgradeHolder, int rank)
        {
            map.put(upgradeHolder, rank);
            return this;
        }

        public Builder add(Holder<EquipmentUpgrade> upgradeHolder)
        {
            return add(upgradeHolder, 1);
        }

        public Builder remove(Holder<EquipmentUpgrade> upgradeHolder)
        {
            map.removeInt(upgradeHolder);
            return this;
        }

        public ItemEquipmentUpgrades build()
        {
            return new ItemEquipmentUpgrades(map);
        }
    }

    @FunctionalInterface
    public interface UpgradeEffectConsumer<E extends EquipmentUpgradeEffect>
    {
        void accept(E effect, int upgradeRank);
    }

    @FunctionalInterface
    public interface UpgradeEffectFunction<E extends EquipmentUpgradeEffect, T>
    {
        T apply(E effect, int upgradeRank);
    }

    @FunctionalInterface
    public interface UpgradeEffectToIntFunction<E extends EquipmentUpgradeEffect>
    {
        int apply(E effect, int upgradeRank);
    }

    @FunctionalInterface
    public interface UpgradeEffectToDoubleFunction<E extends EquipmentUpgradeEffect>
    {
        double apply(E effect, int upgradeRank);
    }

    @FunctionalInterface
    public interface UpgradeEffectPredicate<E extends EquipmentUpgradeEffect>
    {
        boolean testEffect(E effect, int upgradeRank);
    }
}