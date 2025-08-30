package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.math.MathOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.function.Consumer;

public interface ValueUpgradeEffect extends UpgradeTooltipsProvider
{
    static ValueUpgradeEffect of(DoubleLevelBasedValue value, MathOperation operation)
    {
        return new SimpleValue(value, operation);
    }

    static ValueUpgradeEffect of(NumberProvider value, MathOperation operation)
    {
        return new ContextValue(value, operation);
    }

    Codec<ValueUpgradeEffect> CODEC = Type.CODEC.dispatch(ValueUpgradeEffect::getType, Type::getCodec);

    MathOperation operation();

    double getValue(LootContext context, int upgradeRank);

    default double apply(LootContext context, int upgradeRank, double base, double total)
    {
        return operation().applyCompoundingDouble(total, base, getValue(context, upgradeRank));
    }

    Type getType();

    @Deprecated // Do Nothing
    @Override
    default void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines) {}

    record SimpleValue(DoubleLevelBasedValue value, MathOperation operation) implements ValueUpgradeEffect
    {
        private static final MapCodec<SimpleValue> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                DoubleLevelBasedValue.CODEC.fieldOf("value").forGetter(SimpleValue::value),
                MathOperation.COMPOUND_OP_CODEC.fieldOf("op").forGetter(SimpleValue::operation))
                .apply(instance, SimpleValue::new));

        @Override
        public double getValue(LootContext context, int upgradeRank)
        {
            return value.calculate(upgradeRank);
        }

        @Override
        public Type getType()
        {
            return Type.SIMPLE_VALUE;
        }
    }

    record ContextValue(NumberProvider value, MathOperation operation) implements ValueUpgradeEffect
    {
        private static final MapCodec<ContextValue> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                NumberProviders.CODEC.fieldOf("value").forGetter(ContextValue::value),
                MathOperation.COMPOUND_OP_CODEC.fieldOf("op").forGetter(ContextValue::operation))
                .apply(instance, ContextValue::new));

        @Override
        public double getValue(LootContext context, int upgradeRank)
        {
            return value.getFloat(context);
        }

        @Override
        public Type getType()
        {
            return Type.CONTEXT_VALUE;
        }
    }

    enum Type implements StringRepresentable
    {
        SIMPLE_VALUE("simple", SimpleValue.CODEC),
        CONTEXT_VALUE("context", ContextValue.CODEC);

        public static final Codec<Type> CODEC = LimaEnumCodec.create(Type.class);

        private final String name;
        private final MapCodec<? extends ValueUpgradeEffect> codec;

        Type(String name, MapCodec<? extends ValueUpgradeEffect> codec)
        {
            this.name = name;
            this.codec = codec;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }

        public MapCodec<? extends ValueUpgradeEffect> getCodec()
        {
            return codec;
        }
    }
}