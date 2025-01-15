package liedge.limatech.lib.upgrades.effect.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limatech.lib.math.CompoundOperation;
import liedge.limatech.lib.math.LevelBasedDoubleValue;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public record SimpleValueOperationEffect(LevelBasedDoubleValue value, CompoundOperation operation) implements ValueUpgradeEffect
{
    public static final MapCodec<SimpleValueOperationEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedDoubleValue.CODEC.fieldOf("value").forGetter(SimpleValueOperationEffect::value),
            CompoundOperation.CODEC.fieldOf("operation").forGetter(SimpleValueOperationEffect::operation))
            .apply(instance, SimpleValueOperationEffect::new));
    public static final Codec<SimpleValueOperationEffect> FLAT_CODEC = CODEC.codec();

    @Override
    public double calculate(@Nullable Player player, @Nullable Entity targetEntity, int upgradeRank)
    {
        return value.calculate(upgradeRank);
    }

    @Override
    public Component getValueTooltip(int upgradeRank, boolean beneficial)
    {
        return operation.toComponent(value.calculate(upgradeRank), !beneficial);
    }

    @Override
    public ValueUpgradeEffectType type()
    {
        return ValueUpgradeEffectType.SIMPLE_OPERATION;
    }
}