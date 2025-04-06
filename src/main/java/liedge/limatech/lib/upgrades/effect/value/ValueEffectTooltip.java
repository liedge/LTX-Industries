package liedge.limatech.lib.upgrades.effect.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limatech.lib.CompoundValueOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public interface ValueEffectTooltip
{
    Codec<ValueEffectTooltip> CODEC = Type.CODEC.flatDispatch(SimpleValueTooltip.class, SimpleValueTooltip.FLAT_CODEC, ValueEffectTooltip::getType, Type::getCodec);

    Component get(int upgradeRank, CompoundValueOperation operation);

    Type getType();

    enum Type implements StringRepresentable
    {
        SIMPLE_VALUE("simple_value", SimpleValueTooltip.CODEC),
        ATTRIBUTE_AMOUNT("attribute_amount", AttributeAmountTooltip.CODEC);

        public static final LimaEnumCodec<Type> CODEC = LimaEnumCodec.create(Type.class);

        private final String name;
        private final MapCodec<? extends ValueEffectTooltip> codec;

        Type(String name, MapCodec<? extends ValueEffectTooltip> codec)
        {
            this.name = name;
            this.codec = codec;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }

        public MapCodec<? extends ValueEffectTooltip> getCodec()
        {
            return codec;
        }
    }
}