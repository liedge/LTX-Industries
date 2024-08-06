package liedge.limatech.upgradesystem;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.ModResources;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechTags;
import liedge.limatech.registry.LimaTechRegistries;
import liedge.limatech.upgradesystem.effect.EquipmentUpgradeEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

public record EquipmentUpgrade(Component title, Component description, int maxRank, HolderSet<Item> supportedItems, HolderSet<EquipmentUpgrade> exclusiveSet, List<EquipmentUpgradeEffect> effects, ResourceLocation iconLocation)
{
    public static final int MAX_UPGRADE_RANK = 10;
    public static final ResourceLocation DEFAULT_UPGRADE_ICON = LimaTech.RESOURCES.location("generic");
    public static final Codec<Integer> UPGRADE_RANK_CODEC = Codec.intRange(1, MAX_UPGRADE_RANK);
    public static final StreamCodec<ByteBuf, Integer> UPGRADE_RANK_STREAM_CODEC = LimaStreamCodecs.varIntRange(1, MAX_UPGRADE_RANK);
    public static final Codec<EquipmentUpgrade> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("title").forGetter(EquipmentUpgrade::title),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(EquipmentUpgrade::description),
            UPGRADE_RANK_CODEC.optionalFieldOf("max_rank", 1).forGetter(EquipmentUpgrade::maxRank),
            RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("supported_items").forGetter(EquipmentUpgrade::supportedItems),
            RegistryCodecs.homogeneousList(LimaTechRegistries.EQUIPMENT_UPGRADES_KEY).optionalFieldOf("exclusive_set", HolderSet.empty()).forGetter(EquipmentUpgrade::exclusiveSet),
            EquipmentUpgradeEffect.CODEC.listOf(1, Integer.MAX_VALUE).fieldOf("effects").forGetter(EquipmentUpgrade::effects),
            ResourceLocation.CODEC.optionalFieldOf("icon", DEFAULT_UPGRADE_ICON).forGetter(EquipmentUpgrade::iconLocation))
            .apply(instance, EquipmentUpgrade::new));
    public static final Codec<Holder<EquipmentUpgrade>> CODEC = RegistryFixedCodec.create(LimaTechRegistries.EQUIPMENT_UPGRADES_KEY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<EquipmentUpgrade>> STREAM_CODEC = ByteBufCodecs.holderRegistry(LimaTechRegistries.EQUIPMENT_UPGRADES_KEY);

    public static String defaultTitleTranslationKey(ResourceKey<EquipmentUpgrade> key)
    {
        return ModResources.prefixSuffixIdTranslationKey("equipment_upgrade", "title", key.location());
    }

    public static String defaultDescriptionTranslationKey(ResourceKey<EquipmentUpgrade> key)
    {
        return ModResources.prefixSuffixIdTranslationKey("equipment_upgrade", "desc", key.location());
    }

    public static Builder builder(ResourceKey<EquipmentUpgrade> key)
    {
        return new Builder(key);
    }

    public boolean canBeInstalledOn(ItemStack equipmentItem)
    {
        return supportedItems.contains(equipmentItem.getItemHolder());
    }

    public boolean canBeInstalledAlongside(Holder<EquipmentUpgrade> otherUpgrade)
    {
        return !exclusiveSet.contains(otherUpgrade);
    }

    public static class Builder
    {
        private final ResourceKey<EquipmentUpgrade> key;
        private final List<EquipmentUpgradeEffect> effects = new ObjectArrayList<>();

        private final Component title;
        private final Component description;
        private int maxRank = 1;
        private HolderSet<Item> supportedItems;
        private HolderSet<EquipmentUpgrade> exclusiveSet = HolderSet.empty();
        private ResourceLocation icon = DEFAULT_UPGRADE_ICON;

        private Builder(ResourceKey<EquipmentUpgrade> key)
        {
            this.key = key;
            this.title = Component.translatable(defaultTitleTranslationKey(key));
            this.description = Component.translatable(defaultDescriptionTranslationKey(key));
        }

        public Builder setMaxRank(int maxRank)
        {
            this.maxRank = Mth.clamp(maxRank, 1, MAX_UPGRADE_RANK);
            return this;
        }

        public Builder supports(HolderSet<Item> supportedItems)
        {
            this.supportedItems = supportedItems;
            return this;
        }

        public Builder supportsAllWeapons(HolderGetter<Item> holders)
        {
            return supports(holders, LimaTechTags.Items.LTX_WEAPONS);
        }

        public Builder supports(HolderGetter<Item> holders, TagKey<Item> tagKey)
        {
            return supports(holders.getOrThrow(tagKey));
        }

        public Builder supports(Holder<Item> item)
        {
            return supports(HolderSet.direct(item));
        }

        @SafeVarargs
        public final Builder supports(Holder<Item>... items)
        {
            return supports(HolderSet.direct(items));
        }

        public Builder exclusiveWith(HolderSet<EquipmentUpgrade> exclusiveSet)
        {
            this.exclusiveSet = exclusiveSet;
            return this;
        }

        public Builder exclusiveWith(HolderGetter<EquipmentUpgrade> holders, TagKey<EquipmentUpgrade> tagKey)
        {
            return exclusiveWith(holders.getOrThrow(tagKey));
        }

        public Builder withEffect(EquipmentUpgradeEffect effect)
        {
            effects.add(effect);
            return this;
        }

        public Builder effectIcon(String name)
        {
            this.icon = LimaTech.RESOURCES.location(name);
            return this;
        }

        public EquipmentUpgrade build()
        {
            Objects.requireNonNull(supportedItems, "Equipment upgrade builder has no valid supported items.");
            Preconditions.checkState(!effects.isEmpty(), "Equipment upgrade builder has no effects defined.");

            return new EquipmentUpgrade(
                    title,
                    description,
                    maxRank,
                    supportedItems,
                    exclusiveSet,
                    effects,
                    icon);
        }

        public void buildAndRegister(BootstrapContext<EquipmentUpgrade> ctx)
        {
            ctx.register(key, build());
        }
    }
}