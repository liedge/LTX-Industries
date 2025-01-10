package liedge.limatech.lib.upgradesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.BiFunction;

public interface UpgradeBaseEntry<U extends UpgradeBase<?, ?, U>>
{
    static <U extends UpgradeBase<?, ?, U>, UE extends UpgradeBaseEntry<U>> Codec<UE> createCodec(Codec<Holder<U>> upgradeCodec, BiFunction<Holder<U>, Integer, UE> factory)
    {
        return RecordCodecBuilder.create(instance -> instance.group(
                upgradeCodec.fieldOf("upgrade").forGetter(UpgradeBaseEntry::upgrade),
                UpgradeBase.UPGRADE_RANK_CODEC.optionalFieldOf("rank", 1).forGetter(UpgradeBaseEntry::upgradeRank))
                .apply(instance, factory));
    }

    static <U extends UpgradeBase<?, ?, U>, UE extends UpgradeBaseEntry<U>> StreamCodec<RegistryFriendlyByteBuf, UE> createStreamCodec(StreamCodec<RegistryFriendlyByteBuf, Holder<U>> upgradeStreamCodec, BiFunction<Holder<U>, Integer, UE> factory)
    {
        return StreamCodec.composite(
                upgradeStreamCodec, UpgradeBaseEntry::upgrade,
                UpgradeBase.UPGRADE_RANK_STREAM_CODEC, UpgradeBaseEntry::upgradeRank,
                factory);
    }

    Holder<U> upgrade();

    int upgradeRank();
}