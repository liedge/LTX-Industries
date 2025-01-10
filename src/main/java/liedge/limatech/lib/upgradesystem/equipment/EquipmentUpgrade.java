package liedge.limatech.lib.upgradesystem.equipment;

import com.mojang.serialization.Codec;
import liedge.limacore.lib.ModResources;
import liedge.limatech.LimaTechTags;
import liedge.limatech.client.UpgradeIcon;
import liedge.limatech.lib.upgradesystem.UpgradeBase;
import liedge.limatech.lib.upgradesystem.UpgradeBaseBuilder;
import liedge.limatech.lib.upgradesystem.equipment.effect.EquipmentUpgradeEffect;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.List;

public record EquipmentUpgrade(Component title, Component description, int maxRank, HolderSet<Item> supportedSet, HolderSet<EquipmentUpgrade> exclusiveSet, List<EquipmentUpgradeEffect> effects, UpgradeIcon icon) implements UpgradeBase<Item, EquipmentUpgradeEffect, EquipmentUpgrade>
{
    public static final Codec<EquipmentUpgrade> DIRECT_CODEC = UpgradeBase.createDirectCodec(Registries.ITEM, LimaTechRegistries.EQUIPMENT_UPGRADES_KEY, EquipmentUpgradeEffect.CODEC, EquipmentUpgrade::new);
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

    public static class Builder extends UpgradeBaseBuilder<Item, EquipmentUpgradeEffect, EquipmentUpgrade, Builder>
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