package liedge.limatech.lib.upgrades.equipment;

import com.mojang.serialization.Codec;
import liedge.limatech.lib.upgrades.UpgradeBase;
import liedge.limatech.lib.upgrades.UpgradeBaseBuilder;
import liedge.limatech.lib.upgrades.UpgradeDisplayInfo;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public record EquipmentUpgrade(UpgradeDisplayInfo display, int maxRank, HolderSet<Item> supportedSet, HolderSet<EquipmentUpgrade> exclusiveSet, DataComponentMap effects) implements UpgradeBase<Item, EquipmentUpgrade>
{
    public static final Codec<EquipmentUpgrade> DIRECT_CODEC = UpgradeBase.createDirectCodec(Registries.ITEM, LimaTechRegistries.Keys.EQUIPMENT_UPGRADES, EquipmentUpgrade::new);
    public static final Codec<Holder<EquipmentUpgrade>> CODEC = RegistryFixedCodec.create(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<EquipmentUpgrade>> STREAM_CODEC = ByteBufCodecs.holderRegistry(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES);

    public static UpgradeBaseBuilder<Item, EquipmentUpgrade> builder(ResourceKey<EquipmentUpgrade> key)
    {
        return new UpgradeBaseBuilder<>(key, EquipmentUpgrade::new);
    }
}