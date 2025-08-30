package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.math.MathOperation;
import net.minecraft.util.StringRepresentable;

/**
 * Similar to {@link net.minecraft.world.item.enchantment.LevelBasedValue} but with double data type to avoid
 * precision loss.
 */
public interface DoubleLevelBasedValue
{
    Codec<DoubleLevelBasedValue> CODEC = Codec.lazyInitialized(() -> Type.CODEC.flatDispatch(ConstantValue.class, ConstantValue.FLAT_CODEC, DoubleLevelBasedValue::getType, Type::getCodec));

    static ConstantValue constant(double value)
    {
        return new ConstantValue(value);
    }

    static LinearValue linear(double perLevel)
    {
        return new LinearValue(perLevel, perLevel);
    }

    static LinearValue linear(double base, double perLevelAfterFirst)
    {
        return new LinearValue(base, perLevelAfterFirst);
    }

    static Fraction constantNumerator(double numerator, DoubleLevelBasedValue denominator)
    {
        return new Fraction(constant(numerator), denominator);
    }

    static Exponential exponential(double base, DoubleLevelBasedValue power)
    {
        return new Exponential(base, power);
    }

    static Exponential linearExponent(double base)
    {
        return exponential(base, linear(1));
    }

    static MathOps mathOp(DoubleLevelBasedValue left, DoubleLevelBasedValue right, MathOperation operation)
    {
        return new MathOps(left, right, operation);
    }

    double calculate(int level);

    Type getType();

    record ConstantValue(double value) implements DoubleLevelBasedValue
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

    record LinearValue(double base, double perLevelAfterFirst) implements DoubleLevelBasedValue
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

    record Fraction(DoubleLevelBasedValue numerator, DoubleLevelBasedValue denominator) implements DoubleLevelBasedValue
    {
        public static final MapCodec<Fraction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        DoubleLevelBasedValue.CODEC.fieldOf("numerator").forGetter(Fraction::numerator),
                        DoubleLevelBasedValue.CODEC.fieldOf("denominator").forGetter(Fraction::denominator))
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

    record Exponential(double base, DoubleLevelBasedValue power) implements DoubleLevelBasedValue
    {
        public static final MapCodec<Exponential> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Codec.DOUBLE.fieldOf("base").forGetter(Exponential::base),
                        DoubleLevelBasedValue.CODEC.fieldOf("power").forGetter(Exponential::power))
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

    record MathOps(DoubleLevelBasedValue left, DoubleLevelBasedValue right, MathOperation operation) implements DoubleLevelBasedValue
    {
        public static final MapCodec<MathOps> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                DoubleLevelBasedValue.CODEC.fieldOf("left").forGetter(MathOps::left),
                DoubleLevelBasedValue.CODEC.fieldOf("right").forGetter(MathOps::right),
                MathOperation.SINGLE_OP_CODEC.fieldOf("op").forGetter(MathOps::operation))
                .apply(instance, MathOps::new));

        @Override
        public double calculate(int level)
        {
            return operation.applyAsDouble(left.calculate(level), right.calculate(level));
        }

        @Override
        public Type getType()
        {
            return Type.MATH_OPERATION;
        }
    }

    enum Type implements StringRepresentable
    {
        CONSTANT("constant", ConstantValue.CODEC),
        LINEAR("linear", LinearValue.CODEC),
        FRACTION("fraction", Fraction.CODEC),
        EXPONENTIAL("exponential", Exponential.CODEC),
        MATH_OPERATION("math_ops", MathOps.CODEC);

        public static final LimaEnumCodec<Type> CODEC = LimaEnumCodec.create(Type.class);

        private final String name;
        private final MapCodec<? extends DoubleLevelBasedValue> codec;

        Type(String name, MapCodec<? extends DoubleLevelBasedValue> codec)
        {
            this.name = name;
            this.codec = codec;
        }

        public MapCodec<? extends DoubleLevelBasedValue> getCodec()
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