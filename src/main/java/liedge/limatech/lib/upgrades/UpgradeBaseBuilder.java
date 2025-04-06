package liedge.limatech.lib.upgrades;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class UpgradeBaseBuilder<CTX, U extends UpgradeBase<CTX, U>, B extends UpgradeBaseBuilder<CTX, U, B>>
{
    private final ResourceKey<U> key;
    private final UpgradeBase.UpgradeFactory<CTX, U> factory;
    private final DataComponentMap.Builder effectMapBuilder = DataComponentMap.builder();
    private final Map<DataComponentType<?>, List<?>> effectLists = new Object2ObjectOpenHashMap<>();

    private Component title;
    private Component description;
    private int maxRank = 1;
    private HolderSet<CTX> supportedSet;
    private HolderSet<U> exclusiveSet = HolderSet.empty();
    private UpgradeIcon icon = UpgradeIcon.DEFAULT_ICON;

    protected UpgradeBaseBuilder(ResourceKey<U> key, UpgradeBase.UpgradeFactory<CTX, U> factory)
    {
        this.key = key;
        this.factory = factory;
        this.title = Component.translatable(defaultTitleKey(key));
        this.description = Component.translatable(defaultDescriptionKey(key));
    }

    @SuppressWarnings("unchecked")
    private B selfUnchecked()
    {
        return (B) this;
    }

    public B setTitle(Component title)
    {
        this.title = title;
        return selfUnchecked();
    }

    public B setDescription(Component description)
    {
        this.description = description;
        return selfUnchecked();
    }

    public B createDefaultTitle(Function<String, ? extends Component> function)
    {
        this.title = function.apply(defaultTitleKey(key));
        return selfUnchecked();
    }

    public B createDefaultDescription(Function<String, ? extends Component> function)
    {
        this.description = function.apply(defaultDescriptionKey(key));
        return selfUnchecked();
    }

    public B setMaxRank(int maxRank)
    {
        this.maxRank = Mth.clamp(maxRank, 1, UpgradeBase.MAX_UPGRADE_RANK);
        return selfUnchecked();
    }

    public B supports(HolderSet<CTX> supportedSet)
    {
        this.supportedSet = supportedSet;
        return selfUnchecked();
    }

    public B supports(HolderGetter<CTX> holders, TagKey<CTX> tagKey)
    {
        return supports(holders.getOrThrow(tagKey));
    }

    public B supports(Holder<CTX> ctxObject)
    {
        return supports(HolderSet.direct(ctxObject));
    }

    @SafeVarargs
    public final B supports(Holder<CTX>... ctxObjects)
    {
        return supports(HolderSet.direct(ctxObjects));
    }

    public B exclusiveWith(HolderSet<U> exclusiveSet)
    {
        this.exclusiveSet = exclusiveSet;
        return selfUnchecked();
    }

    public B exclusiveWith(HolderGetter<U> holders, TagKey<U> tagKey)
    {
        return exclusiveWith(holders.getOrThrow(tagKey));
    }

    public <T> B withEffect(DataComponentType<List<T>> type, T effect)
    {
        getEffectsList(type).add(effect);
        return selfUnchecked();
    }

    public <T> B withEffect(Supplier<? extends DataComponentType<List<T>>> typeSupplier, T effect)
    {
        return withEffect(typeSupplier.get(), effect);
    }

    public <T> B withConditionalEffect(DataComponentType<List<ConditionalEffect<T>>> type, T effect, @Nullable LootItemCondition.Builder condition)
    {
        getEffectsList(type).add(new ConditionalEffect<>(effect, Optional.ofNullable(condition).map(LootItemCondition.Builder::build)));
        return selfUnchecked();
    }

    public <T> B withConditionalEffect(DataComponentType<List<ConditionalEffect<T>>> type, T effect)
    {
        return withConditionalEffect(type, effect, null);
    }

    public <T> B withConditionalEffect(Supplier<? extends DataComponentType<List<ConditionalEffect<T>>>> typeSupplier, T effect, @Nullable LootItemCondition.Builder condition)
    {
        return withConditionalEffect(typeSupplier.get(), effect, condition);
    }

    public <T> B withConditionalEffect(Supplier<? extends DataComponentType<List<ConditionalEffect<T>>>> typeSupplier, T effect)
    {
        return withConditionalEffect(typeSupplier, effect, null);
    }

    public <T> B withTargetedEffect(DataComponentType<List<TargetedConditionalEffect<T>>> type, EnchantmentTarget enchanted, EnchantmentTarget affected, T effect, @Nullable LootItemCondition.Builder condition)
    {
        getEffectsList(type).add(new TargetedConditionalEffect<>(enchanted, affected, effect, Optional.ofNullable(condition).map(LootItemCondition.Builder::build)));
        return selfUnchecked();
    }

    public <T> B withTargetedEffect(DataComponentType<List<TargetedConditionalEffect<T>>> type, EnchantmentTarget enchanted, EnchantmentTarget affected, T effect)
    {
        return withTargetedEffect(type, enchanted, affected, effect, null);
    }

    public <T> B withTargetedEffect(Supplier<? extends DataComponentType<List<TargetedConditionalEffect<T>>>> typeSupplier, EnchantmentTarget enchanted, EnchantmentTarget affected, T effect, @Nullable LootItemCondition.Builder condition)
    {
        return withTargetedEffect(typeSupplier.get(), enchanted, affected, effect, condition);
    }

    public <T> B withTargetedEffect(Supplier<? extends DataComponentType<List<TargetedConditionalEffect<T>>>> typeSupplier, EnchantmentTarget enchanted, EnchantmentTarget affected, T effect)
    {
        return withTargetedEffect(typeSupplier, enchanted, affected, effect, null);
    }

    public <T> B withSpecialEffect(DataComponentType<T> type, T effect)
    {
        effectMapBuilder.set(type, effect);
        return selfUnchecked();
    }

    public <T> B withSpecialEffect(Supplier<? extends DataComponentType<T>> typeSupplier, T effect)
    {
        return withSpecialEffect(typeSupplier.get(), effect);
    }

    public B withUnitEffect(DataComponentType<Unit> type)
    {
        return withSpecialEffect(type, Unit.INSTANCE);
    }

    public B withUnitEffect(Supplier<? extends DataComponentType<Unit>> typeSupplier)
    {
        return withUnitEffect(typeSupplier.get());
    }

    public B effectIcon(UpgradeIcon icon)
    {
        this.icon = icon;
        return selfUnchecked();
    }

    public U build()
    {
        return factory.apply(title, description, maxRank, supportedSet, exclusiveSet, effectMapBuilder.build(), icon);
    }

    public void buildAndRegister(BootstrapContext<U> ctx)
    {
        ctx.register(key, build());
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getEffectsList(DataComponentType<List<T>> type)
    {
        return (List<T>) effectLists.computeIfAbsent(type, key -> {
            List<T> list = new ObjectArrayList<>();
            effectMapBuilder.set(type, list);
            return list;
        });
    }

    protected abstract String defaultTitleKey(ResourceKey<U> key);

    protected abstract String defaultDescriptionKey(ResourceKey<U> key);
}