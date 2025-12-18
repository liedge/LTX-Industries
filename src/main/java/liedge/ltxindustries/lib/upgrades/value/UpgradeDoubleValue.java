package liedge.ltxindustries.lib.upgrades.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.storage.loot.LootContext;

/**
 * Similar to {@link net.minecraft.world.item.enchantment.LevelBasedValue} but with double data type to avoid
 * precision loss.
 */
public interface UpgradeDoubleValue extends UpgradeValueProvider
{
    static Codec<UpgradeDoubleValue> codec(String typeKey)
    {
        return Codec.lazyInitialized(() -> Type.CODEC.dispatchWithInline(typeKey, ConstantDouble.class, ConstantDouble.INLINE_CODEC, UpgradeDoubleValue::getType, Type::getCodec));
    }

    Codec<UpgradeDoubleValue> CODEC = codec("type");

    double calculate(int upgradeRank);

    @Override
    default double get(LootContext context, int rank)
    {
        return calculate(rank);
    }

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