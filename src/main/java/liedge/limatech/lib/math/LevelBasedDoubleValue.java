package liedge.limatech.lib.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleImmutableList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

import java.util.List;

/**
 * Similar to {@link net.minecraft.world.item.enchantment.LevelBasedValue} but with double data type to avoid
 * precision loss.
 */
public interface LevelBasedDoubleValue
{
    Codec<LevelBasedDoubleValue> CODEC = Codec.lazyInitialized(() -> Type.CODEC.flatDispatch(ConstantValue.class, ConstantValue.FLAT_CODEC, LevelBasedDoubleValue::getType, Type::getCodec));

    static ConstantValue constant(double value)
    {
        return new ConstantValue(value);
    }

    static LinearValue perLevel(double perLevel)
    {
        return new LinearValue(perLevel, perLevel);
    }

    static LinearValue linear(double base, double perLevelAfterFirst)
    {
        return new LinearValue(base, perLevelAfterFirst);
    }

    static LookupValue lookupOrLowest(double... values)
    {
        DoubleList list = new DoubleImmutableList(values);
        double min = list.doubleStream().min().orElseThrow(() -> new IllegalArgumentException("Lookup value must have at least 1 value."));
        return new LookupValue(list, constant(min));
    }

    static LookupValue lookupOrHighest(double... values)
    {
        DoubleList list = new DoubleImmutableList(values);
        double max = list.doubleStream().max().orElseThrow(() -> new IllegalArgumentException("Lookup value must at least 1 value."));
        return new LookupValue(list, constant(max));
    }

    static Fraction constantNumerator(double numerator, LevelBasedDoubleValue denominator)
    {
        return new Fraction(constant(numerator), denominator);
    }

    static Exponential exponential(double base, LevelBasedDoubleValue power)
    {
        return new Exponential(base, power);
    }

    static Exponential linearExponent(double base)
    {
        return exponential(base, perLevel(1));
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

    record Fraction(LevelBasedDoubleValue numerator, LevelBasedDoubleValue denominator) implements LevelBasedDoubleValue
    {
        public static final MapCodec<Fraction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                LevelBasedDoubleValue.CODEC.fieldOf("numerator").forGetter(Fraction::numerator),
                LevelBasedDoubleValue.CODEC.fieldOf("denominator").forGetter(Fraction::denominator))
                .apply(instance, Fraction::new));


        @Override
        public double calculate(int level)
        {
            return numerator.calculate(level) / denominator.calculate(level);
        }

        @Override
        public Type getType()
        {
            return Type.FRACTION;
        }
    }

    record Exponential(double base, LevelBasedDoubleValue power) implements LevelBasedDoubleValue
    {
        public static final MapCodec<Exponential> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.fieldOf("base").forGetter(Exponential::base),
                LevelBasedDoubleValue.CODEC.fieldOf("power").forGetter(Exponential::power))
                .apply(instance, Exponential::new));

        @Override
        public double calculate(int level)
        {
            return Math.pow(base, power.calculate(level));
        }

        @Override
        public Type getType()
        {
            return Type.EXPONENTIAL;
        }
    }

    record LookupValue(DoubleList values, LevelBasedDoubleValue fallback) implements LevelBasedDoubleValue
    {
        public static final MapCodec<LookupValue> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Codec.DOUBLE.listOf().fieldOf("values").forGetter(LookupValue::values),
                        LevelBasedDoubleValue.CODEC.fieldOf("fallback").forGetter(LookupValue::fallback))
                .apply(instance, LookupValue::new));

        private LookupValue(List<Double> boxedList, LevelBasedDoubleValue fallback)
        {
            this(new DoubleImmutableList(boxedList), fallback);
        }

        @Override
        public double calculate(int level)
        {
            return level <= values.size() ? values.getDouble(level - 1) : fallback.calculate(level);
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
        FRACTION("fraction", Fraction.CODEC),
        EXPONENTIAL("exponential", Exponential.CODEC),
        LOOKUP("lookup", LookupValue.CODEC);

        public static final LimaEnumCodec<Type> CODEC = LimaEnumCodec.create(Type.class);

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