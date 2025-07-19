package liedge.ltxindustries.lib.upgrades.machine;

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
import net.minecraft.world.level.block.entity.BlockEntityType;

public record MachineUpgrade(UpgradeDisplayInfo display, int maxRank, HolderSet<BlockEntityType<?>> supportedSet, HolderSet<MachineUpgrade> exclusiveSet, DataComponentMap effects) implements UpgradeBase<BlockEntityType<?>, MachineUpgrade>
{
    public static final Codec<MachineUpgrade> DIRECT_CODEC = UpgradeBase.createDirectCodec(Registries.BLOCK_ENTITY_TYPE, LTXIRegistries.Keys.MACHINE_UPGRADES, MachineUpgrade::new);
    public static final Codec<Holder<MachineUpgrade>> CODEC = RegistryFixedCodec.create(LTXIRegistries.Keys.MACHINE_UPGRADES);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<MachineUpgrade>> STREAM_CODEC = ByteBufCodecs.holderRegistry(LTXIRegistries.Keys.MACHINE_UPGRADES);

    public static UpgradeBaseBuilder<BlockEntityType<?>, MachineUpgrade> builder(ResourceKey<MachineUpgrade> key)
    {
        return new UpgradeBaseBuilder<>(key, MachineUpgrade::new);
    }
}