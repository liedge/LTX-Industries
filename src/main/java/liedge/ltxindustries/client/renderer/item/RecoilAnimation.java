package liedge.ltxindustries.client.renderer.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.data.LimaEnumCodec;
import liedge.ltxindustries.client.LTXIRenderer;
import net.minecraft.util.StringRepresentable;

public interface RecoilAnimation
{
    Codec<RecoilAnimation> CODEC = Type.CODEC.dispatch(RecoilAnimation::type, Type::getCodec);

    static RecoilAnimation linear(float threshold)
    {
        return new LinearThreshold(threshold);
    }

    static RecoilAnimation sineCurve()
    {
        return SineCurve.INSTANCE;
    }

    float apply(float delta);

    Type type();

    record LinearThreshold(float threshold) implements RecoilAnimation
    {
        private static final MapCodec<LinearThreshold> CODEC = LimaCoreCodecs.floatOpenRange(0f, 1f).fieldOf("threshold").xmap(LinearThreshold::new, LinearThreshold::threshold);

        @Override
        public float apply(float delta)
        {
            return LTXIRenderer.linearThresholdCurve(delta, threshold);
        }

        @Override
        public Type type()
        {
            return Type.LINEAR_THRESHOLD;
        }
    }

    final class SineCurve implements RecoilAnimation
    {
        private static final SineCurve INSTANCE = new SineCurve();
        private static final MapCodec<SineCurve> CODEC = MapCodec.unit(INSTANCE);

        private SineCurve() { }

        @Override
        public float apply(float delta)
        {
            return LTXIRenderer.sineAnimationCurve(delta);
        }

        @Override
        public Type type()
        {
            return Type.SINE_CURVE;
        }
    }

    enum Type implements StringRepresentable
    {
        LINEAR_THRESHOLD("linear", LinearThreshold.CODEC),
        SINE_CURVE("sine", SineCurve.CODEC);

        public static final Codec<Type> CODEC = LimaEnumCodec.create(Type.class);

        private final String name;
        private final MapCodec<? extends RecoilAnimation> codec;

        Type(String name, MapCodec<? extends RecoilAnimation> codec)
        {
            this.name = name;
            this.codec = codec;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }

        public MapCodec<? extends RecoilAnimation> getCodec()
        {
            return codec;
        }
    }
}