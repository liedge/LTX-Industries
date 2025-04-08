package liedge.limatech.lib.upgrades;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.data.BootstrapObjectBuilder;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.lib.ModResources;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class UpgradeBaseBuilder<CTX, U extends UpgradeBase<CTX, U>> implements BootstrapObjectBuilder<U>
{
    public static final String DEFAULT_DESCRIPTION_SUFFIX = "desc";

    private final ResourceKey<U> key;
    private final UpgradeBase.UpgradeFactory<CTX, U> factory;
    private final DataComponentMap.Builder effectMapBuilder = DataComponentMap.builder();
    private final Map<DataComponentType<?>, List<?>> effectLists = new Object2ObjectOpenHashMap<>();

    private Component title;
    private Component description;
    private UpgradeIcon icon = UpgradeIcon.noRenderIcon();
    private String category = UpgradeDisplayInfo.NO_CATEGORY;

    private int maxRank = 1;
    private HolderSet<CTX> supportedSet;
    private HolderSet<U> exclusiveSet = HolderSet.empty();

    public UpgradeBaseBuilder(ResourceKey<U> key, UpgradeBase.UpgradeFactory<CTX, U> factory)
    {
        this.key = key;
        this.factory = factory;
        this.title = defaultTitle();
        this.description = defaultDescription();
    }

    public UpgradeBaseBuilder<CTX, U> setTitle(Component title)
    {
        this.title = title;
        return this;
    }

    public UpgradeBaseBuilder<CTX, U> setDescription(Component description)
    {
        this.description = description;
        return this;
    }

    public UpgradeBaseBuilder<CTX, U> createDefaultTitle(LimaColor color)
    {
        return createDefaultTitle(title -> title.withStyle(color.chatStyle()));
    }

    public UpgradeBaseBuilder<CTX, U> createDefaultTitle(UnaryOperator<MutableComponent> operator)
    {
        this.title = operator.apply(defaultTitle());
        return this;
    }

    public UpgradeBaseBuilder<CTX, U> createDefaultDescription(UnaryOperator<MutableComponent> operator)
    {
        this.description = operator.apply(defaultDescription());
        return this;
    }

    public UpgradeBaseBuilder<CTX, U> setMaxRank(int maxRank)
    {
        this.maxRank = Mth.clamp(maxRank, 1, UpgradeBase.MAX_UPGRADE_RANK);
        return this;
    }

    public UpgradeBaseBuilder<CTX, U> supports(HolderSet<CTX> supportedSet)
    {
        this.supportedSet = supportedSet;
        return this;
    }

    public UpgradeBaseBuilder<CTX, U> supports(HolderGetter<CTX> holders, TagKey<CTX> tagKey)
    {
        return supports(holders.getOrThrow(tagKey));
    }

    public UpgradeBaseBuilder<CTX, U> supports(Holder<CTX> ctxObject)
    {
        return supports(HolderSet.direct(ctxObject));
    }

    @SafeVarargs
    public final UpgradeBaseBuilder<CTX, U> supports(Holder<CTX>... ctxObjects)
    {
        return supports(HolderSet.direct(ctxObjects));
    }

    public UpgradeBaseBuilder<CTX, U> exclusiveWith(HolderSet<U> exclusiveSet)
    {
        this.exclusiveSet = exclusiveSet;
        return this;
    }

    public UpgradeBaseBuilder<CTX, U> exclusiveWith(HolderGetter<U> holders, TagKey<U> tagKey)
    {
        return exclusiveWith(holders.getOrThrow(tagKey));
    }

    public <T> UpgradeBaseBuilder<CTX, U> withEffect(DataComponentType<List<T>> type, T effect)
    {
        getEffectsList(type).add(effect);
        return this;
    }

    public <T> UpgradeBaseBuilder<CTX, U> withEffect(Supplier<? extends DataComponentType<List<T>>> typeSupplier, T effect)
    {
        return withEffect(typeSupplier.get(), effect);
    }

    public <T> UpgradeBaseBuilder<CTX, U> withConditionalEffect(DataComponentType<List<ConditionalEffect<T>>> type, T effect, @Nullable LootItemCondition.Builder condition)
    {
        getEffectsList(type).add(new ConditionalEffect<>(effect, Optional.ofNullable(condition).map(LootItemCondition.Builder::build)));
        return this;
    }

    public <T> UpgradeBaseBuilder<CTX, U> withConditionalEffect(DataComponentType<List<ConditionalEffect<T>>> type, T effect)
    {
        return withConditionalEffect(type, effect, null);
    }

    public <T> UpgradeBaseBuilder<CTX, U> withConditionalEffect(Supplier<? extends DataComponentType<List<ConditionalEffect<T>>>> typeSupplier, T effect, @Nullable LootItemCondition.Builder condition)
    {
        return withConditionalEffect(typeSupplier.get(), effect, condition);
    }

    public <T> UpgradeBaseBuilder<CTX, U> withConditionalEffect(Supplier<? extends DataComponentType<List<ConditionalEffect<T>>>> typeSupplier, T effect)
    {
        return withConditionalEffect(typeSupplier, effect, null);
    }

    public <T> UpgradeBaseBuilder<CTX, U> withTargetedEffect(DataComponentType<List<TargetedConditionalEffect<T>>> type, EnchantmentTarget enchanted, EnchantmentTarget affected, T effect, @Nullable LootItemCondition.Builder condition)
    {
        getEffectsList(type).add(new TargetedConditionalEffect<>(enchanted, affected, effect, Optional.ofNullable(condition).map(LootItemCondition.Builder::build)));
        return this;
    }

    public <T> UpgradeBaseBuilder<CTX, U> withTargetedEffect(DataComponentType<List<TargetedConditionalEffect<T>>> type, EnchantmentTarget enchanted, EnchantmentTarget affected, T effect)
    {
        return withTargetedEffect(type, enchanted, affected, effect, null);
    }

    public <T> UpgradeBaseBuilder<CTX, U> withTargetedEffect(Supplier<? extends DataComponentType<List<TargetedConditionalEffect<T>>>> typeSupplier, EnchantmentTarget enchanted, EnchantmentTarget affected, T effect, @Nullable LootItemCondition.Builder condition)
    {
        return withTargetedEffect(typeSupplier.get(), enchanted, affected, effect, condition);
    }

    public <T> UpgradeBaseBuilder<CTX, U> withTargetedEffect(Supplier<? extends DataComponentType<List<TargetedConditionalEffect<T>>>> typeSupplier, EnchantmentTarget enchanted, EnchantmentTarget affected, T effect)
    {
        return withTargetedEffect(typeSupplier, enchanted, affected, effect, null);
    }

    public <T> UpgradeBaseBuilder<CTX, U> withSpecialEffect(DataComponentType<T> type, T effect)
    {
        effectMapBuilder.set(type, effect);
        return this;
    }

    public <T> UpgradeBaseBuilder<CTX, U> withSpecialEffect(Supplier<? extends DataComponentType<T>> typeSupplier, T effect)
    {
        return withSpecialEffect(typeSupplier.get(), effect);
    }

    public UpgradeBaseBuilder<CTX, U> withUnitEffect(DataComponentType<Unit> type)
    {
        return withSpecialEffect(type, Unit.INSTANCE);
    }

    public UpgradeBaseBuilder<CTX, U> withUnitEffect(Supplier<? extends DataComponentType<Unit>> typeSupplier)
    {
        return withUnitEffect(typeSupplier.get());
    }

    public UpgradeBaseBuilder<CTX, U> effectIcon(UpgradeIcon icon)
    {
        this.icon = icon;
        return this;
    }

    public UpgradeBaseBuilder<CTX, U> category(String category)
    {
        this.category = StringUtils.isNotBlank(category) ? category : UpgradeDisplayInfo.NO_CATEGORY;
        return this;
    }

    @Override
    public ResourceKey<U> key()
    {
        return key;
    }

    @Override
    public U build()
    {
        UpgradeDisplayInfo displayInfo = new UpgradeDisplayInfo(title, description, icon, category);
        return factory.apply(displayInfo, maxRank, supportedSet, exclusiveSet, effectMapBuilder.build());
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

    private MutableComponent defaultTitle()
    {
        return Component.translatable(ModResources.registryPrefixedIdLangKey(key));
    }

    private MutableComponent defaultDescription()
    {
        return Component.translatable(suffixTranslationKey(DEFAULT_DESCRIPTION_SUFFIX));
    }
}