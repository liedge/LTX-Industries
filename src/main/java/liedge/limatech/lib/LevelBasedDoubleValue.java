package liedge.limatech.lib;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleImmutableList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.function.Function;

/**
 * Similar to {@link net.minecraft.world.item.enchantment.LevelBasedValue} but with double data type to avoid
 * precision loss.
 */
public interface LevelBasedDoubleValue
{
    Codec<LevelBasedDoubleValue> DISPATCH_CODEC = Type.CODEC.dispatch(LevelBasedDoubleValue::getType, Type::getCodec);
    Codec<LevelBasedDoubleValue> CODEC = Codec.either(ConstantValue.FLAT_CODEC, DISPATCH_CODEC).xmap(
            either -> either.map(Function.identity(), Function.identity()),
            value -> value instanceof ConstantValue constant ? Either.left(constant) : Either.right(value));

    static ConstantValue constant(double value)
    {
        return new ConstantValue(value);
    }

    static LinearValue perLevel(double perLevel)
    {
        return new LinearValue(perLevel, perLevel);
    }

    static LinearValue perLevel(double base, double perLevelAfterFirst)
    {
        return new LinearValue(base, perLevelAfterFirst);
    }

    static LookupValue lookup(double fallback, double... values)
    {
        return new LookupValue(new DoubleImmutableList(values), fallback);
    }

    double calculate(int level);

    Type getType();

    record ConstantValue(double value) implements LevelBasedDoubleValue
    {
        private static final Codec<ConstantValue> FLAT_CODEC = Codec.DOUBLE.xmap(ConstantValue::new, ConstantValue::value);
        private static final MapCodec<ConstantValue> CODEC = FLAT_CODEC.fieldOf("value");

        @Override
        public double calculate(int level)
        {
            return value;
        }

        @Override
        public Type getType()
        {
            return Type.CONSTANT;
        }
    }

    record LinearValue(double base, double perLevelAfterFirst) implements LevelBasedDoubleValue
    {
        private static final MapCodec<LinearValue> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Codec.DOUBLE.fieldOf("base").forGetter(LinearValue::base),
                        Codec.DOUBLE.fieldOf("per_level_after_first").forGetter(LinearValue::perLevelAfterFirst))
                .apply(instance, LinearValue::new));

        @Override
        public double calculate(int level)
        {
            return base + perLevelAfterFirst * (level - 1);
        }

        @Override
        public Type getType()
        {
            return Type.LINEAR;
        }
    }

    record LookupValue(DoubleList values, double fallback) implements LevelBasedDoubleValue
    {
        public static final MapCodec<LookupValue> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Codec.DOUBLE.listOf().fieldOf("values").forGetter(LookupValue::values),
                        Codec.DOUBLE.fieldOf("fallback").forGetter(LookupValue::fallback))
                .apply(instance, LookupValue::new));

        private LookupValue(List<Double> boxedList, double fallback)
        {
            this(new DoubleImmutableList(boxedList), fallback);
        }

        @Override
        public double calculate(int level)
        {
            return level <= values.size() ? values.getDouble(level - 1) : fallback;
        }

        @Override
        public Type getType()
        {
            return Type.LOOKUP;
        }
    }

    enum Type implements StringRepresentable
    {
        CONSTANT("constant", ConstantValue.CODEC),
        LINEAR("linear", LinearValue.CODEC),
        LOOKUP("lookup", LookupValue.CODEC);

        public static final Codec<Type> CODEC = LimaEnumCodec.createStrict(Type.class);

        private final String name;
        private final MapCodec<? extends LevelBasedDoubleValue> codec;

        Type(String name, MapCodec<? extends LevelBasedDoubleValue> codec)
        {
            this.name = name;
            this.codec = codec;
        }

        public MapCodec<? extends LevelBasedDoubleValue> getCodec()
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