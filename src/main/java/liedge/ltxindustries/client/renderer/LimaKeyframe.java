package liedge.ltxindustries.client.renderer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.EasingType;

public record LimaKeyframe(float time, float value, EasingType easing) implements Comparable<LimaKeyframe>
{
    public static final Codec<LimaKeyframe> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.floatRange(0f, 1f).fieldOf("time").forGetter(LimaKeyframe::time),
            Codec.FLOAT.fieldOf("value").forGetter(LimaKeyframe::value),
            EasingType.CODEC.fieldOf("easing").forGetter(LimaKeyframe::easing))
            .apply(i, LimaKeyframe::new));

    @Override
    public int compareTo(LimaKeyframe o)
    {
        return Float.compare(time, o.time);
    }
}