package liedge.ltxindustries.lib.upgrades;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record UpgradeEntry(Holder<Upgrade> upgrade, int rank)
{
    public static final Codec<UpgradeEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
            Upgrade.CODEC.fieldOf("upgrade").forGetter(UpgradeEntry::upgrade),
            Upgrade.RANK_CODEC.optionalFieldOf("rank", 1).forGetter(UpgradeEntry::rank))
            .apply(i, UpgradeEntry::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpgradeEntry> STREAM_CODEC = StreamCodec.composite(
            Upgrade.STREAM_CODEC, UpgradeEntry::upgrade,
            Upgrade.RANK_STREAM_CODEC, UpgradeEntry::rank,
            UpgradeEntry::new);
}