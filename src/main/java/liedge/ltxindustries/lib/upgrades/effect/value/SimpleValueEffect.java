package liedge.ltxindustries.lib.upgrades.effect.value;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.MathOperation;
import net.minecraft.world.level.storage.loot.LootContext;

public record SimpleValueEffect(UpgradeDoubleValue value, MathOperation operation) implements ValueUpgradeEffect
{
    static final MapCodec<SimpleValueEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UpgradeDoubleValue.CODEC.fieldOf("value").forGetter(SimpleValueEffect::value),
            MathOperation.COMPOUND_OP_CODEC.fieldOf("op").forGetter(SimpleValueEffect::operation))
            .apply(instance, SimpleValueEffect::new));

    public static SimpleValueEffect of(UpgradeDoubleValue value, MathOperation operation)
    {
        return new SimpleValueEffect(value, operation);
    }

    @Override
    public double get(LootContext context, int upgradeRank)
    {
        return value.calculate(upgradeRank);
    }

    @Override
    public Type getType()
    {
        return Type.SIMPLE;
    }
}