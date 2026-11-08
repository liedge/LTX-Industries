package liedge.ltxindustries.data;

import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;

public record TurbineFuel(int energy)
{
    public static final Codec<TurbineFuel> INLINE_CODEC = ExtraCodecs.POSITIVE_INT.xmap(TurbineFuel::new, TurbineFuel::energy);
    public static final Codec<TurbineFuel> CODEC = Codec.withAlternative(INLINE_CODEC.fieldOf("energy").codec(), INLINE_CODEC);
}