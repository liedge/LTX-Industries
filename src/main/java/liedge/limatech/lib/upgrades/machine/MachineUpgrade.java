package liedge.limatech.lib.upgrades.machine;

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
import net.minecraft.world.level.block.entity.BlockEntityType;

public record MachineUpgrade(UpgradeDisplayInfo display, int maxRank, HolderSet<BlockEntityType<?>> supportedSet, HolderSet<MachineUpgrade> exclusiveSet, DataComponentMap effects) implements UpgradeBase<BlockEntityType<?>, MachineUpgrade>
{
    public static final Codec<MachineUpgrade> DIRECT_CODEC = UpgradeBase.createDirectCodec(Registries.BLOCK_ENTITY_TYPE, LimaTechRegistries.Keys.MACHINE_UPGRADES, MachineUpgrade::new);
    public static final Codec<Holder<MachineUpgrade>> CODEC = RegistryFixedCodec.create(LimaTechRegistries.Keys.MACHINE_UPGRADES);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<MachineUpgrade>> STREAM_CODEC = ByteBufCodecs.holderRegistry(LimaTechRegistries.Keys.MACHINE_UPGRADES);

    public static UpgradeBaseBuilder<BlockEntityType<?>, MachineUpgrade> builder(ResourceKey<MachineUpgrade> key)
    {
        return new UpgradeBaseBuilder<>(key, MachineUpgrade::new);
    }
}