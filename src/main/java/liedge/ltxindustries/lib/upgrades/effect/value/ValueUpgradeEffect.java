package liedge.ltxindustries.lib.upgrades.effect.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.math.MathOperation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.storage.loot.LootContext;

public sealed interface ValueUpgradeEffect permits SimpleValueEffect, ContextValueEffect
{
    Codec<ValueUpgradeEffect> CODEC = Type.CODEC.dispatch(ValueUpgradeEffect::getType, Type::getCodec);

    double get(LootContext context, int upgradeRank);

    MathOperation operation();

    Type getType();

    default double apply(LootContext context, int upgradeRank, double base, double total)
    {
        return operation().applyCompoundingDouble(total, base, get(context, upgradeRank));
    }

    enum Type implements StringRepresentable
    {
        SIMPLE("simple", SimpleValueEffect.CODEC),
        LOOT_CONTEXT("loot_context", ContextValueEffect.CODEC);

        public static final LimaEnumCodec<Type> CODEC = LimaEnumCodec.create(Type.class);

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