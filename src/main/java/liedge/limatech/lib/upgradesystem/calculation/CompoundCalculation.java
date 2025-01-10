package liedge.limatech.lib.upgradesystem.calculation;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limatech.lib.LevelBasedDoubleValue;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

import static liedge.limacore.util.LimaMathUtil.FORMAT_COMMA_INT;
import static liedge.limacore.util.LimaMathUtil.FORMAT_PERCENTAGE;

public abstract class CompoundCalculation implements Comparable<CompoundCalculation>
{
    public static final Codec<CompoundCalculation> CODEC = CalculationType.CODEC.dispatch(CompoundCalculation::getType, CalculationType::getCodec);

    public static double runSteps(final double baseValue, @Nullable Object optionalContext, List<Step> steps)
    {
        if (steps.isEmpty()) return baseValue;

        double result = baseValue;

        for (Step step : steps)
        {
            if (step.calculation.getType() == CalculationType.OVERRIDE_BASE)
            {
                result = step.calculate(result, optionalContext);
            }
            else
            {
                result += step.calculate(result, optionalContext);
            }
        }

        return result;
    }

    public static double runSingle(final double baseValue, @Nullable Object optionalContext, CompoundCalculation calculation, int level)
    {
        if (calculation.isEmpty())
        {
            return baseValue;
        }
        else if (calculation.getType() == CalculationType.OVERRIDE_BASE)
        {
            return calculation.calculate(baseValue, level, optionalContext);
        }
        else
        {
            return baseValue + calculation.calculate(baseValue, level, optionalContext);
        }
    }

    public static double runSteps(final double baseValue, List<Step> steps)
    {
        return runSteps(baseValue, null, steps);
    }

    public static int runStepsAsInt(final double baseValue, @Nullable Object optionalContext, List<Step> steps)
    {
        return (int) runSteps(baseValue, optionalContext, steps);
    }

    public static int runStepsAsInt(final double baseValue, List<Step> steps)
    {
        return runStepsAsInt(baseValue, null, steps);
    }

    protected static <T extends CompoundCalculation> MapCodec<T> simpleCodec(Function<LevelBasedDoubleValue, T> function)
    {
        return LevelBasedDoubleValue.CODEC.fieldOf("value").xmap(function, CompoundCalculation::getValue);
    }

    private final LevelBasedDoubleValue value;

    protected CompoundCalculation(LevelBasedDoubleValue value)
    {
        this.value = value;
    }

    public abstract double calculate(double baseValue, int level, @Nullable Object optionalContext);

    public abstract Component getTooltip(int level);

    public LevelBasedDoubleValue getValue()
    {
        return value;
    }

    public abstract CalculationType getType();

    public boolean isEmpty()
    {
        return false;
    }

    protected String formatStandard(double value)
    {
        if (value < 1000)
        {
            return Double.toString(value);
        }
        else
        {
            return FORMAT_COMMA_INT.format(value);
        }
    }

    protected String formatStandardWithSign(double value)
    {
        String formattedValue = formatStandard(value);
        if (value >= 0) formattedValue = "+" + formattedValue;
        return formattedValue;
    }

    protected String formatPercentage(double value)
    {
        return FORMAT_PERCENTAGE.format(value);
    }

    protected String formatPercentageWithSign(double value)
    {
        String formattedValue = formatPercentage(value);
        if (value >= 0f) formattedValue = "+" + formattedValue;
        return formattedValue;
    }

    @Override
    public int compareTo(@NotNull CompoundCalculation o)
    {
        return Integer.compare(getType().getPriority(), o.getType().getPriority());
    }

    static abstract class FlatOperation extends CompoundCalculation
    {
        protected FlatOperation(LevelBasedDoubleValue value)
        {
            super(value);
        }

        @Override
        public double calculate(double baseValue, int level, @Nullable Object optionalContext)
        {
            return getValue().calculate(level);
        }
    }

    static abstract class MultiplyOperation extends CompoundCalculation
    {
        protected MultiplyOperation(LevelBasedDoubleValue value)
        {
            super(value);
        }

        @Override
        public double calculate(double baseValue, int level, @Nullable Object optionalContext)
        {
            return baseValue * getValue().calculate(level);
        }
    }

    public record Step(CompoundCalculation calculation, int level) implements Comparable<Step>
    {
        public double calculate(double baseValue, @Nullable Object optionalContext)
        {
            return calculation.calculate(baseValue, level, optionalContext);
        }

        @Override
        public int compareTo(@NotNull CompoundCalculation.Step o)
        {
            return this.calculation.compareTo(o.calculation);
        }
    }
}