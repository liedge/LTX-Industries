package liedge.limatech.lib.upgrades;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.lib.function.ObjectIntConsumer;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.limacore.util.LimaStreamsUtil;
import liedge.limatech.lib.upgrades.effect.equipment.EquipmentUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.value.ValueUpgradeEffect;
import liedge.limatech.registry.game.LimaTechUpgradeEffectComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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
    public <T> void forEachConditionalEffect(DataComponentType<List<ConditionalEffect<T>>> type, LootContext context, ObjectIntConsumer<T> consumer)
    {
        for (Object2IntMap.Entry<Holder<U>> entry : internalMap.object2IntEntrySet())
        {
            List<ConditionalEffect<T>> list = entry.getKey().value().getListEffect(type);
            for (ConditionalEffect<T> conditionalEffect : list)
            {
                if (conditionalEffect.matches(context))
                {
                    consumer.acceptWithInt(conditionalEffect.effect(), entry.getIntValue());
                }
            }
        }
    }

    public <T> void forEachConditionalEffect(Supplier<? extends DataComponentType<List<ConditionalEffect<T>>>> typeSupplier, LootContext context, ObjectIntConsumer<T> consumer)
    {
        forEachConditionalEffect(typeSupplier.get(), context, consumer);
    }

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

    public void applyDamageContextEffects(DataComponentType<List<TargetedConditionalEffect<EquipmentUpgradeEffect>>> type, ServerLevel level, EnchantmentTarget effectTarget, LivingEntity targetEntity, Player attacker, DamageSource damageSource)
    {
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, targetEntity)
                .withParameter(LootContextParams.ORIGIN, targetEntity.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
                .withParameter(LootContextParams.ATTACKING_ENTITY, attacker)
                .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, damageSource.getDirectEntity())
                .create(LootContextParamSets.ENTITY);

        LootContext context = new LootContext.Builder(params).create(Optional.empty());

        for (Object2IntMap.Entry<Holder<U>> entry : internalMap.object2IntEntrySet())
        {
            List<TargetedConditionalEffect<EquipmentUpgradeEffect>> list = entry.getKey().value().getListEffect(type);
            for (TargetedConditionalEffect<EquipmentUpgradeEffect> conditionalEffect : list)
            {
                if (conditionalEffect.enchanted() == effectTarget && conditionalEffect.matches(context))
                {
                    Entity entity = switch (conditionalEffect.affected())
                    {
                        case ATTACKER -> attacker;
                        case DAMAGING_ENTITY -> damageSource.getDirectEntity();
                        case VICTIM -> targetEntity;
                    };

                    if (entity != null) conditionalEffect.effect().applyEquipmentEffect(level, entity, entry.getIntValue(), context);
                }
            }
        }
    }

    public void applyDamageContextEffects(Supplier<? extends DataComponentType<List<TargetedConditionalEffect<EquipmentUpgradeEffect>>>> typeSupplier, ServerLevel level, EnchantmentTarget effectTarget, LivingEntity targetEntity, Player attacker, DamageSource damageSource)
    {
        applyDamageContextEffects(typeSupplier.get(), level, effectTarget, targetEntity, attacker, damageSource);
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

    public <T> Stream<T> effectFlatStream(DataComponentType<List<T>> type)
    {
        return entryStream().flatMap(entry -> entry.getKey().value().getListEffect(type).stream());
    }

    public <T> Stream<T> effectFlatStream(Supplier<? extends DataComponentType<List<T>>> typeSupplier)
    {
        return effectFlatStream(typeSupplier.get());
    }

    public <T> Stream<EffectRankPair<T>> boxedFlatStream(DataComponentType<List<T>> type)
    {
        return entryStream().flatMap(entry -> {
            List<T> data = entry.getKey().value().getListEffect(type);
            return data.stream().map(effect -> new EffectRankPair<>(effect, entry.getIntValue()));
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
    public double applyValue(DataComponentType<List<ValueUpgradeEffect>> type, LootContext context, double base, double total)
    {
        List<EffectRankPair<ValueUpgradeEffect>> list = boxedFlatStream(type).sorted(Comparator.comparing(entry -> entry.effect().getOperation())).collect(LimaStreamsUtil.toObjectList());
        double result = total;

        for (EffectRankPair<ValueUpgradeEffect> pair : list)
        {
            ValueUpgradeEffect effect = pair.effect();
            result = effect.apply(context, pair.upgradeRank(), base, result);
        }

        return result;
    }

    public double applyValue(DataComponentType<List<ValueUpgradeEffect>> type, LootContext context, double base)
    {
        return applyValue(type, context, base, base);
    }

    public double applyValue(Supplier<? extends DataComponentType<List<ValueUpgradeEffect>>> typeSupplier, LootContext context, double base, double total)
    {
        return applyValue(typeSupplier.get(), context, base, total);
    }

    public double applyValue(Supplier<? extends DataComponentType<List<ValueUpgradeEffect>>> typeSupplier, LootContext context, double base)
    {
        return applyValue(typeSupplier.get(), context, base);
    }

    public double applyConditionalValue(DataComponentType<List<ConditionalEffect<ValueUpgradeEffect>>> type, IntFunction<LootContext> contextFunction, double base, double total)
    {
        List<EffectRankPair<ConditionalEffect<ValueUpgradeEffect>>> list = boxedFlatStream(type).sorted(Comparator.comparing(pair -> pair.effect().effect().getOperation())).collect(LimaStreamsUtil.toObjectList());
        double result = total;

        for (EffectRankPair<ConditionalEffect<ValueUpgradeEffect>> pair : list)
        {
            ConditionalEffect<ValueUpgradeEffect> conditionalEffect = pair.effect();
            LootContext context = contextFunction.apply(pair.upgradeRank());

            if (conditionalEffect.matches(context))
            {
                ValueUpgradeEffect effect = conditionalEffect.effect();
                result = effect.apply(context, pair.upgradeRank(), base, result);
            }
        }

        return result;
    }

    public double applyConditionalValue(DataComponentType<List<ConditionalEffect<ValueUpgradeEffect>>> type, IntFunction<LootContext> contextFunction, double base)
    {
        return applyConditionalValue(type, contextFunction, base, base);
    }

    public double applyConditionalValue(Supplier<? extends DataComponentType<List<ConditionalEffect<ValueUpgradeEffect>>>> typeSupplier, IntFunction<LootContext> contextFunction, double base, double total)
    {
        return applyConditionalValue(typeSupplier.get(), contextFunction, base, total);
    }

    public double applyConditionalValue(Supplier<? extends DataComponentType<List<ConditionalEffect<ValueUpgradeEffect>>>> typeSupplier, IntFunction<LootContext> contextFunction, double base)
    {
        return applyConditionalValue(typeSupplier.get(), contextFunction, base);
    }
    //#endregion

    // Container properties
    public abstract MutableUpgradesContainer<U, ? extends UpgradesContainerBase<CTX, U>> toMutableContainer();

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
}