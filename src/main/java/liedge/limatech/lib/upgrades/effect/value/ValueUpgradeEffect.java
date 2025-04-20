package liedge.limatech.lib.upgrades.effect.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limatech.lib.CompoundValueOperation;
import liedge.limatech.lib.upgrades.effect.EffectTooltipProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.List;
import java.util.function.Consumer;

public abstract class ValueUpgradeEffect implements EffectTooltipProvider.SingleLine
{
    // Codecs
    public static final Codec<ValueUpgradeEffect> CODEC = Type.CODEC.dispatch(ValueUpgradeEffect::getType, Type::getCodec);
    public static final Codec<List<ValueUpgradeEffect>> LIST_CODEC = CODEC.listOf();

    // Helper factories
    public static ValueUpgradeEffect createSimple(DoubleLevelBasedValue value, CompoundValueOperation operation, boolean invertColors)
    {
        return new SimpleValue(value, operation, invertColors);
    }

    public static ValueUpgradeEffect createSimple(DoubleLevelBasedValue value, CompoundValueOperation operation)
    {
        return new SimpleValue(value, operation, false);
    }

    public static ValueUpgradeEffect create(NumberProvider value, CompoundValueOperation operation, ValueEffectTooltip tooltip)
    {
        return new ContextValue(value, operation, tooltip);
    }

    // Class definition
    private final CompoundValueOperation operation;
    private final ValueEffectTooltip tooltip;

    private ValueUpgradeEffect(CompoundValueOperation operation, ValueEffectTooltip tooltip)
    {
        this.operation = operation;
        this.tooltip = tooltip;
    }

    public ValueEffectTooltip getTooltip()
    {
        return tooltip;
    }

    public CompoundValueOperation getOperation()
    {
        return operation;
    }

    public double apply(LootContext context, int upgradeRank, double base, double total)
    {
        return operation.computeDouble(base, total, getValue(context, upgradeRank));
    }

    public abstract Type getType();

    protected abstract double getValue(LootContext context, int upgradeRank);

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();

    @Deprecated
    @Override
    public final void appendEffectLines(int upgradeRank, Consumer<Component> linesConsumer) { }

    @Override
    public Component getEffectTooltip(int upgradeRank)
    {
        return tooltip.get(upgradeRank, operation);
    }

    private static class SimpleValue extends ValueUpgradeEffect
    {
        private static final MapCodec<SimpleValue> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                DoubleLevelBasedValue.CODEC.fieldOf("simple_value").forGetter(o -> o.value),
                CompoundValueOperation.CODEC.fieldOf("op").forGetter(ValueUpgradeEffect::getOperation),
                Codec.BOOL.optionalFieldOf("invert_color", false).forGetter(o -> ((SimpleValueTooltip) o.getTooltip()).invertColors()))
                .apply(instance, SimpleValue::new));

        private final DoubleLevelBasedValue value;

        private SimpleValue(DoubleLevelBasedValue value, CompoundValueOperation operation, boolean invertColors)
        {
            super(operation, new SimpleValueTooltip(value, invertColors));
            this.value = value;
        }

        @Override
        public Type getType()
        {
            return Type.SIMPLE;
        }

        @Override
        protected double getValue(LootContext context, int upgradeRank)
        {
            return value.calculate(upgradeRank);
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == this) return true;
            else if (o instanceof SimpleValue simple) return this.value.equals(simple.value);
            else return false;
        }

        @Override
        public int hashCode()
        {
            return value.hashCode();
        }

        @Override
        public String toString()
        {
            return "SimpleValue[" + value.toString() + "]";
        }
    }

    private static class ContextValue extends ValueUpgradeEffect
    {
        private static final MapCodec<ContextValue> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                NumberProviders.CODEC.fieldOf("value").forGetter(o -> o.value),
                CompoundValueOperation.CODEC.fieldOf("op").forGetter(ValueUpgradeEffect::getOperation),
                ValueEffectTooltip.CODEC.fieldOf("tooltip").forGetter(ValueUpgradeEffect::getTooltip))
                .apply(instance, ContextValue::new));

        private final NumberProvider value;

        private ContextValue(NumberProvider value, CompoundValueOperation operation, ValueEffectTooltip tooltip)
        {
            super(operation, tooltip);
            this.value = value;
        }

        @Override
        public Type getType()
        {
            return Type.CONTEXT;
        }

        @Override
        protected double getValue(LootContext context, int upgradeRank)
        {
            return value.getFloat(context);
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == this) return true;
            else if (o instanceof ContextValue context) return this.value.equals(context.value);
            else return false;
        }

        @Override
        public int hashCode()
        {
            return value.hashCode();
        }

        @Override
        public String toString()
        {
            return "ContextValue[" + value.toString() + "]";
        }
    }

    public enum Type implements StringRepresentable
    {
        SIMPLE("simple", SimpleValue.CODEC),
        CONTEXT("context", ContextValue.CODEC);

        private static final Codec<Type> CODEC = LimaEnumCodec.create(Type.class);

        private final String name;
        private final MapCodec<? extends ValueUpgradeEffect> codec;

        Type(String name, MapCodec<? extends ValueUpgradeEffect> codec)
        {
            this.name = name;
            this.codec = codec;
        }

        public MapCodec<? extends ValueUpgradeEffect> getCodec()
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