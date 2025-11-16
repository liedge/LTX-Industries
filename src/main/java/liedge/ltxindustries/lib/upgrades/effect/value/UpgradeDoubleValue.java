package liedge.ltxindustries.lib.upgrades.effect.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

/**
 * Similar to {@link net.minecraft.world.item.enchantment.LevelBasedValue} but with double data type to avoid
 * precision loss.
 */
public interface UpgradeDoubleValue
{
    Codec<UpgradeDoubleValue> CODEC = Codec.lazyInitialized(() -> Type.CODEC.dispatchWithInline(ConstantDouble.class, ConstantDouble.INLINE_CODEC, UpgradeDoubleValue::getType, Type::getCodec));

    double calculate(int upgradeRank);

    Type getType();

    enum Type implements StringRepresentable
    {
        CONSTANT("constant", ConstantDouble.CODEC),
        LINEAR("linear", LinearDouble.CODEC),
        EXPONENTIAL("exponential", ExponentialDouble.CODEC),
        MATH_OPERATION("math_ops", MathOperationDouble.CODEC);

        public static final LimaEnumCodec<Type> CODEC = LimaEnumCodec.create(Type.class);

        private final String name;
        private final MapCodec<? extends UpgradeDoubleValue> codec;

        Type(String name, MapCodec<? extends UpgradeDoubleValue> codec)
        {
            this.name = name;
            this.codec = codec;
        }

        public MapCodec<? extends UpgradeDoubleValue> getCodec()
        {
            return codec;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }
    }
}