package liedge.ltxindustries.lib.upgrades;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.data.BootstrapObjectBuilder;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.lib.ModResources;
import liedge.ltxindustries.lib.upgrades.effect.*;
import liedge.ltxindustries.lib.upgrades.tooltip.StaticTooltip;
import liedge.ltxindustries.lib.upgrades.tooltip.UpgradeComponentLike;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class UpgradeBuilder implements BootstrapObjectBuilder<Upgrade>
{
    public static final String DEFAULT_DESCRIPTION_SUFFIX = "desc";

    private final ResourceKey<Upgrade> key;
    private final DataComponentMap.Builder effectMapBuilder = DataComponentMap.builder();
    private final Map<DataComponentType<?>, List<?>> effectLists = new Object2ObjectOpenHashMap<>();

    private Component title;
    private Component description;
    private final List<UpgradeComponentLike> tooltips = new ObjectArrayList<>();
    private UpgradeIcon icon = UpgradeIcon.noRenderIcon();
    private String category = UpgradeDisplayInfo.NO_CATEGORY;

    private int maxRank = 1;
    private HolderSet<Item> itemUsers = HolderSet.empty();
    private HolderSet<BlockEntityType<?>> blockEntityUsers = HolderSet.empty();
    private HolderSet<Upgrade> exclusiveSet = HolderSet.empty();

    UpgradeBuilder(ResourceKey<Upgrade> key)
    {
        this.key = key;
        this.title = defaultTitle();
        this.description = defaultDescription();
    }

    public UpgradeBuilder setTitle(Component title)
    {
        this.title = title;
        return this;
    }

    public UpgradeBuilder setDescription(Component description)
    {
        this.description = description;
        return this;
    }

    public UpgradeBuilder createDefaultTitle(LimaColor color)
    {
        return createDefaultTitle(title -> title.withStyle(color.chatStyle()));
    }

    public UpgradeBuilder createDefaultTitle(UnaryOperator<MutableComponent> operator)
    {
        this.title = operator.apply(defaultTitle());
        return this;
    }

    public UpgradeBuilder createDefaultDescription(UnaryOperator<MutableComponent> operator)
    {
        this.description = operator.apply(defaultDescription());
        return this;
    }

    public UpgradeBuilder tooltip(UpgradeComponentLike tooltip)
    {
        this.tooltips.add(tooltip);
        return this;
    }

    public UpgradeBuilder tooltip(int index, Function<String, UpgradeComponentLike> function)
    {
        return tooltip(function.apply(tooltipKey(key, index)));
    }

    public UpgradeBuilder staticTooltip(int index)
    {
        return tooltip(index, key -> StaticTooltip.of(Component.translatable(key)));
    }

    public UpgradeBuilder staticTooltip(Component component)
    {
        return tooltip(StaticTooltip.of(component));
    }

    public UpgradeBuilder setMaxRank(int maxRank)
    {
        this.maxRank = Mth.clamp(maxRank, 1, Upgrade.MAX_RANK);
        return this;
    }

    //#region Compatibility and exclusivity
    public UpgradeBuilder forEquipment(HolderSet<Item> items)
    {
        this.itemUsers = items;
        return this;
    }

    public UpgradeBuilder forEquipment(HolderGetter<Item> holders, TagKey<Item> tagKey)
    {
        return forEquipment(holders.getOrThrow(tagKey));
    }

    @SafeVarargs
    public final UpgradeBuilder forEquipment(Holder<Item>... items)
    {
        return forEquipment(HolderSet.direct(items));
    }

    public UpgradeBuilder forMachines(HolderSet<BlockEntityType<?>> machines)
    {
        this.blockEntityUsers = machines;
        return this;
    }

    public UpgradeBuilder forMachines(HolderGetter<BlockEntityType<?>> holders, TagKey<BlockEntityType<?>> tagKey)
    {
        return forMachines(holders.getOrThrow(tagKey));
    }

    @SafeVarargs
    public final UpgradeBuilder forMachines(Holder<BlockEntityType<?>>... blockEntityTypes)
    {
        return forMachines(HolderSet.direct(blockEntityTypes));
    }

    public UpgradeBuilder exclusiveWith(HolderSet<Upgrade> exclusiveSet)
    {
        this.exclusiveSet = exclusiveSet;
        return this;
    }

    public UpgradeBuilder exclusiveWith(HolderGetter<Upgrade> holders, TagKey<Upgrade> tagKey)
    {
        return exclusiveWith(holders.getOrThrow(tagKey));
    }
    //#endregion

    //#region Effect addition helpers
    public <T> UpgradeBuilder withEffect(DataComponentType<List<T>> type, T effect)
    {
        getEffectsList(type).add(effect);
        return this;
    }

    public <T> UpgradeBuilder withEffect(Supplier<? extends DataComponentType<List<T>>> typeSupplier, T effect)
    {
        return withEffect(typeSupplier.get(), effect);
    }

    public <T> UpgradeBuilder withConditionalEffect(DataComponentType<List<ConditionEffect<T>>> type, T effect, @Nullable LootItemCondition.Builder condition)
    {
        getEffectsList(type).add(new ConditionEffect<>(effect, Optional.ofNullable(condition).map(LootItemCondition.Builder::build)));
        return this;
    }

    public <T> UpgradeBuilder withConditionalEffect(DataComponentType<List<ConditionEffect<T>>> type, T effect)
    {
        return withConditionalEffect(type, effect, null);
    }

    public <T> UpgradeBuilder withConditionalEffect(Supplier<? extends DataComponentType<List<ConditionEffect<T>>>> typeSupplier, T effect, @Nullable LootItemCondition.Builder condition)
    {
        return withConditionalEffect(typeSupplier.get(), effect, condition);
    }

    public <T> UpgradeBuilder withConditionalEffect(Supplier<? extends DataComponentType<List<ConditionEffect<T>>>> typeSupplier, T effect)
    {
        return withConditionalEffect(typeSupplier, effect, null);
    }

    public <T> UpgradeBuilder withTargetedEffect(DataComponentType<List<TargetableEffect<T>>> type, EffectTarget source, EffectTarget affected, T effect, @Nullable LootItemCondition.Builder condition)
    {
        getEffectsList(type).add(new TargetableEffect<>(source, affected, effect, Optional.ofNullable(condition).map(LootItemCondition.Builder::build)));
        return this;
    }

    public <T> UpgradeBuilder withTargetedEffect(DataComponentType<List<TargetableEffect<T>>> type, EffectTarget source, EffectTarget affected, T effect)
    {
        return withTargetedEffect(type, source, affected, effect, null);
    }

    public <T> UpgradeBuilder withTargetedEffect(Supplier<? extends DataComponentType<List<TargetableEffect<T>>>> typeSupplier, EffectTarget source, EffectTarget affected, T effect, @Nullable LootItemCondition.Builder condition)
    {
        return withTargetedEffect(typeSupplier.get(), source, affected, effect, condition);
    }

    public <T> UpgradeBuilder withTargetedEffect(Supplier<? extends DataComponentType<List<TargetableEffect<T>>>> typeSupplier, EffectTarget source, EffectTarget affected, T effect)
    {
        return withTargetedEffect(typeSupplier, source, affected, effect, null);
    }

    public <T> UpgradeBuilder withSpecialEffect(DataComponentType<T> type, T effect)
    {
        effectMapBuilder.set(type, effect);
        return this;
    }

    public <T> UpgradeBuilder withSpecialEffect(Supplier<? extends DataComponentType<T>> typeSupplier, T effect)
    {
        return withSpecialEffect(typeSupplier.get(), effect);
    }

    public UpgradeBuilder withUnitEffect(DataComponentType<Unit> type)
    {
        return withSpecialEffect(type, Unit.INSTANCE);
    }

    public UpgradeBuilder withUnitEffect(Supplier<? extends DataComponentType<Unit>> typeSupplier)
    {
        return withUnitEffect(typeSupplier.get());
    }

    public UpgradeBuilder targetRestriction(LootItemCondition.Builder builder)
    {
        return withEffect(LTXIUpgradeEffectComponents.TARGET_CONDITIONS, builder.build());
    }

    public UpgradeBuilder withConditionUnit(Supplier<? extends DataComponentType<List<ConditionEffect<Unit>>>> typeSupplier, LootItemCondition.Builder builder)
    {
        return withConditionalEffect(typeSupplier, Unit.INSTANCE, builder);
    }

    // Specialty effects
    private <T extends AttributeModifierUpgradeEffect> UpgradeBuilder attributeEffect(Supplier<? extends DataComponentType<List<T>>> typeSupplier, String name, Function<Identifier, T> function)
    {
        Identifier modifierId = key.identifier().withSuffix('.' + name);
        return withEffect(typeSupplier, function.apply(modifierId));
    }

    public UpgradeBuilder itemAttributes(Holder<Attribute> attribute, String name, LevelBasedValue amount, AttributeModifier.Operation operation, EquipmentSlotGroup slots)
    {
        return attributeEffect(LTXIUpgradeEffectComponents.ADD_ITEM_ATTRIBUTES, name, id -> AddItemAttributes.create(attribute, id, amount, operation, slots));
    }

    public UpgradeBuilder damageAttributes(Holder<Attribute> attribute, String name, LevelBasedValue amount, AttributeModifier.Operation operation)
    {
        return attributeEffect(LTXIUpgradeEffectComponents.ADD_DAMAGE_ATTRIBUTES, name, id -> AddDamageAttributes.create(attribute, id, amount, operation));
    }
    //#endregion

    public UpgradeBuilder effectIcon(UpgradeIcon icon)
    {
        this.icon = icon;
        return this;
    }

    public UpgradeBuilder category(String category)
    {
        this.category = StringUtils.isNotBlank(category) ? category : UpgradeDisplayInfo.NO_CATEGORY;
        return this;
    }

    @Override
    public ResourceKey<Upgrade> key()
    {
        return key;
    }

    @Override
    public Upgrade build()
    {
        UpgradeDisplayInfo displayInfo = new UpgradeDisplayInfo(title, description, tooltips, icon, category);
        return new Upgrade(displayInfo, maxRank, new UpgradeUsers(itemUsers, blockEntityUsers), exclusiveSet, effectMapBuilder.build());
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getEffectsList(DataComponentType<List<T>> type)
    {
        return (List<T>) effectLists.computeIfAbsent(type, _ -> {
            List<T> list = new ObjectArrayList<>();
            effectMapBuilder.set(type, list);
            return list;
        });
    }

    private MutableComponent defaultTitle()
    {
        return Component.translatable(defaultTitleKey(key));
    }

    private MutableComponent defaultDescription()
    {
        return Component.translatable(defaultDescriptionKey(key));
    }

    public static String defaultTitleKey(ResourceKey<Upgrade> key)
    {
        return ModResources.registryPrefixedIdLangKey(key);
    }

    public static String defaultDescriptionKey(ResourceKey<Upgrade> key)
    {
        return ModResources.registryPrefixVariantIdLangKey(key, DEFAULT_DESCRIPTION_SUFFIX);
    }

    public static String tooltipKey(ResourceKey<Upgrade> key, int index)
    {
        return ModResources.registryPrefixVariantIdLangKey(key, "tooltip" + index);
    }
}