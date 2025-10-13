package liedge.ltxindustries.lib.upgrades.effect.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public interface UpgradeContextValue
{
    Codec<UpgradeContextValue> CODEC = Type.CODEC.dispatch(UpgradeContextValue::getType, Type::getCodec);

    static UpgradeContextValue of(DoubleLevelBasedValue value)
    {
        return new SimpleValue(value);
    }

    static UpgradeContextValue of(NumberProvider value)
    {
        return new LootContextValue(value);
    }

    double get(LootContext context, int upgradeRank);

    Type getType();

    record SimpleValue(DoubleLevelBasedValue value) implements UpgradeContextValue
    {
        private static final MapCodec<SimpleValue> CODEC = DoubleLevelBasedValue.CODEC.fieldOf("value").xmap(SimpleValue::new, SimpleValue::value);

        @Override
        public double get(LootContext context, int upgradeRank)
        {
            return value.calculate(upgradeRank);
        }

        @Override
        public Type getType()
        {
            return Type.SIMPLE_VALUE;
        }
    }

    record LootContextValue(NumberProvider value) implements UpgradeContextValue
    {
        private static final MapCodec<LootContextValue> CODEC = NumberProviders.CODEC.fieldOf("value").xmap(LootContextValue::new, LootContextValue::value);

        @Override
        public double get(LootContext context, int upgradeRank)
        {
            return value.getFloat(context);
        }

        @Override
        public Type getType()
        {
            return Type.LOOT_CONTEXT_VALUE;
        }
    }

    enum Type implements StringRepresentable
    {
        SIMPLE_VALUE("level_based", SimpleValue.CODEC),
        LOOT_CONTEXT_VALUE("loot_context", LootContextValue.CODEC);

        private static final Codec<Type> CODEC = LimaEnumCodec.create(Type.class);

        private final String name;
        private final MapCodec<? extends UpgradeContextValue> codec;

        Type(String name, MapCodec<? extends UpgradeContextValue> codec)
        {
            this.name = name;
            this.codec = codec;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }

        public MapCodec<? extends UpgradeContextValue> getCodec()
        {
            return codec;
        }
    }
}