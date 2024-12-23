package liedge.limatech.lib.machinetiers;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.lib.OrderedEnum;
import liedge.limacore.util.LimaCollectionsUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public enum MachineTier implements OrderedEnum<MachineTier>
{
    TIER_1(1),
    TIER_2(2),
    TIER_3(3),
    TIER_4(4),
    TIER_5(5),
    TIER_6(6),
    TIER_7(7);

    public static final Codec<MachineTier> CODEC = Codec.INT.comapFlatMap(lvl -> LimaCoreCodecs.nullableDataResult(byTierLevel(lvl), () -> lvl + " is not a valid machine tier."), MachineTier::getTierLevel);
    public static final StreamCodec<ByteBuf, MachineTier> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(lvl -> {
        MachineTier tier = byTierLevel(lvl);
        if (tier == null) throw new DecoderException(lvl + " is not a valid machine tier.");
        return tier;
    }, MachineTier::getTierLevel);

    public static MachineTier getByTierLevel(int tierLevel)
    {
        return Objects.requireNonNull(byTierLevel(tierLevel), tierLevel + " is not a valid machine tier.");
    }

    private static @Nullable MachineTier byTierLevel(int tierLevel)
    {
        return LimaCollectionsUtil.getFrom(values(), tierLevel - 1);
    }

    private final int tierLevel;
    private final int energyMultiplier;
    private final int speedMultiplier;

    MachineTier(int tierLevel)
    {
        this.tierLevel = tierLevel;
        this.energyMultiplier = (int) Math.pow(4, ordinal());
        this.speedMultiplier = (int) Math.pow(2, ordinal());
    }

    public int calculateInt(int base, int multiplier)
    {
        return tierLevel == 1 ? base : base * multiplier * (tierLevel - 1);
    }

    public int getTierLevel()
    {
        return tierLevel;
    }

    public int getEnergyMultiplier()
    {
        return energyMultiplier;
    }

    public int getSpeedModifier()
    {
        return speedMultiplier;
    }
}