package liedge.limatech.lib.upgradesystem.machine;

import com.mojang.serialization.Codec;
import liedge.limacore.lib.ModResources;
import liedge.limatech.client.UpgradeIcon;
import liedge.limatech.lib.upgradesystem.UpgradeBase;
import liedge.limatech.lib.upgradesystem.UpgradeBaseBuilder;
import liedge.limatech.lib.upgradesystem.machine.effect.MachineUpgradeEffect;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;

public record MachineUpgrade(Component title, Component description, int maxRank, HolderSet<BlockEntityType<?>> supportedSet, HolderSet<MachineUpgrade> exclusiveSet, List<MachineUpgradeEffect> effects, UpgradeIcon icon) implements UpgradeBase<BlockEntityType<?>, MachineUpgradeEffect, MachineUpgrade>
{
    public static final Codec<MachineUpgrade> DIRECT_CODEC = UpgradeBase.createDirectCodec(Registries.BLOCK_ENTITY_TYPE, LimaTechRegistries.MACHINE_UPGRADES_KEY, MachineUpgradeEffect.CODEC, MachineUpgrade::new);
    public static final Codec<Holder<MachineUpgrade>> CODEC = RegistryFixedCodec.create(LimaTechRegistries.MACHINE_UPGRADES_KEY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<MachineUpgrade>> STREAM_CODEC = ByteBufCodecs.holderRegistry(LimaTechRegistries.MACHINE_UPGRADES_KEY);

    public static String defaultTitleTranslationKey(ResourceKey<MachineUpgrade> key)
    {
        return ModResources.prefixSuffixIdTranslationKey("machine_upgrade", "title", key.location());
    }

    public static String defaultDescriptionTranslationKey(ResourceKey<MachineUpgrade> key)
    {
        return ModResources.prefixSuffixIdTranslationKey("machine_upgrade", "desc", key.location());
    }

    public static Builder builder(ResourceKey<MachineUpgrade> key)
    {
        return new Builder(key);
    }

    public static class Builder extends UpgradeBaseBuilder<BlockEntityType<?>, MachineUpgradeEffect, MachineUpgrade, Builder>
    {
        private Builder(ResourceKey<MachineUpgrade> key)
        {
            super(key, MachineUpgrade::new);
        }

        @Override
        protected String defaultTitleKey(ResourceKey<MachineUpgrade> key)
        {
            return defaultTitleTranslationKey(key);
        }

        @Override
        protected String defaultDescriptionKey(ResourceKey<MachineUpgrade> key)
        {
            return defaultDescriptionTranslationKey(key);
        }
    }
}