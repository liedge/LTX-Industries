package liedge.ltxindustries.lib.upgrades.equipment;

import com.mojang.serialization.Codec;
import liedge.ltxindustries.lib.upgrades.UpgradeBase;
import liedge.ltxindustries.lib.upgrades.UpgradeBaseBuilder;
import liedge.ltxindustries.lib.upgrades.UpgradeDisplayInfo;
import liedge.ltxindustries.registry.LTXIRegistries;
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
    public static final Codec<EquipmentUpgrade> DIRECT_CODEC = UpgradeBase.createDirectCodec(Registries.ITEM, LTXIRegistries.Keys.EQUIPMENT_UPGRADES, EquipmentUpgrade::new);
    public static final Codec<Holder<EquipmentUpgrade>> CODEC = RegistryFixedCodec.create(LTXIRegistries.Keys.EQUIPMENT_UPGRADES);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<EquipmentUpgrade>> STREAM_CODEC = ByteBufCodecs.holderRegistry(LTXIRegistries.Keys.EQUIPMENT_UPGRADES);

    public static UpgradeBaseBuilder<Item, EquipmentUpgrade> builder(ResourceKey<EquipmentUpgrade> key)
    {
        return new UpgradeBaseBuilder<>(key, EquipmentUpgrade::new);
    }
}