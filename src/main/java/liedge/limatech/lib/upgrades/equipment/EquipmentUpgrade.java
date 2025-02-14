package liedge.limatech.lib.upgrades.equipment;

import com.mojang.serialization.Codec;
import liedge.limacore.lib.ModResources;
import liedge.limatech.LimaTechTags;
import liedge.limatech.lib.upgrades.UpgradeIcon;
import liedge.limatech.lib.upgrades.UpgradeBase;
import liedge.limatech.lib.upgrades.UpgradeBaseBuilder;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public record EquipmentUpgrade(Component title, Component description, int maxRank, HolderSet<Item> supportedSet, HolderSet<EquipmentUpgrade> exclusiveSet, DataComponentMap effects, UpgradeIcon icon) implements UpgradeBase<Item, EquipmentUpgrade>
{
    public static final Codec<EquipmentUpgrade> DIRECT_CODEC = UpgradeBase.createDirectCodec(Registries.ITEM, LimaTechRegistries.Keys.EQUIPMENT_UPGRADES, EquipmentUpgrade::new);
    public static final Codec<Holder<EquipmentUpgrade>> CODEC = RegistryFixedCodec.create(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<EquipmentUpgrade>> STREAM_CODEC = ByteBufCodecs.holderRegistry(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES);

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

    public static class Builder extends UpgradeBaseBuilder<Item, EquipmentUpgrade, Builder>
    {
        private Builder(ResourceKey<EquipmentUpgrade> key)
        {
            super(key, EquipmentUpgrade::new);
        }

        public Builder supportsLTXWeapons(HolderGetter<Item> holders)
        {
            return supports(holders, LimaTechTags.Items.LTX_WEAPONS);
        }

        @Override
        protected String defaultTitleKey(ResourceKey<EquipmentUpgrade> key)
        {
            return defaultTitleTranslationKey(key);
        }

        @Override
        protected String defaultDescriptionKey(ResourceKey<EquipmentUpgrade> key)
        {
            return defaultDescriptionTranslationKey(key);
        }
    }
}