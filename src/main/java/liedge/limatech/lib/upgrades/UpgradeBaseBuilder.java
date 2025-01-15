package liedge.limatech.lib.upgrades;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limatech.lib.upgrades.effect.UpgradeEffectDataType;
import liedge.limatech.lib.upgrades.effect.UpgradeEffectMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class UpgradeBaseBuilder<CTX, U extends UpgradeBase<CTX, U>, B extends UpgradeBaseBuilder<CTX, U, B>>
{
    private final ResourceKey<U> key;
    private final UpgradeBase.UpgradeFactory<CTX, U> factory;
    private final UpgradeEffectMap.Builder effectsBuilder = UpgradeEffectMap.builder();
    private final Map<UpgradeEffectDataType<?>, List<?>> effectsLists = new Object2ObjectOpenHashMap<>();

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

    public <T> B withEffect(UpgradeEffectDataType<T> type, T effect)
    {
        effectsBuilder.add(type, effect);
        return selfUnchecked();
    }

    public <T> B withEffect(Supplier<? extends UpgradeEffectDataType<T>> typeSupplier, T effect)
    {
        return withEffect(typeSupplier.get(), effect);
    }

    public <T> B withListEffect(UpgradeEffectDataType<List<T>> type, T effect)
    {
        getEffectsList(type).add(effect);
        return selfUnchecked();
    }

    public <T> B withListEffect(Supplier<? extends UpgradeEffectDataType<List<T>>> typeSupplier, T effect)
    {
        return withListEffect(typeSupplier.get(), effect);
    }

    @SafeVarargs
    public final <T> B withListEffects(UpgradeEffectDataType<List<T>> type, T... effects)
    {
        getEffectsList(type).addAll(List.of(effects));
        return selfUnchecked();
    }

    @SafeVarargs
    public final <T> B withListEffects(Supplier<? extends UpgradeEffectDataType<List<T>>> typeSupplier, T... effects)
    {
        return withListEffects(typeSupplier.get(), effects);
    }

    public B effectIcon(UpgradeIcon icon)
    {
        this.icon = icon;
        return selfUnchecked();
    }

    public U build()
    {
        return factory.apply(title, description, maxRank, supportedSet, exclusiveSet, effectsBuilder.build(), icon);
    }

    public void buildAndRegister(BootstrapContext<U> ctx)
    {
        ctx.register(key, build());
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getEffectsList(UpgradeEffectDataType<List<T>> type)
    {
        return (List<T>) effectsLists.computeIfAbsent(type, key -> {
            List<T> list = new ObjectArrayList<>();
            effectsBuilder.add(type, list);
            return list;
        });
    }

    protected abstract String defaultTitleKey(ResourceKey<U> key);

    protected abstract String defaultDescriptionKey(ResourceKey<U> key);
}