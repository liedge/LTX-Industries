package liedge.ltxindustries.lib.upgrades.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.function.Function;

/**
 * Similar to {@link net.minecraft.world.item.enchantment.LevelBasedValue} but with double data type to avoid
 * precision loss.
 */
public interface ContextlessValue extends UpgradeValueProvider
{
    Codec<ContextlessValue> CODEC = Codec.lazyInitialized(() -> LimaCoreCodecs.dispatchWithInline(UpgradeValueTypes.CONTEXTLESS_REGISTRY_CODEC, ConstantDouble.class, ConstantDouble.INLINE_CODEC, ContextlessValue::codec, Function.identity()));

    static ContextlessValue wrapLevels(LevelBasedValue value)
    {
        return new LBVWrapper(value);
    }

    double calculate(int upgradeRank);

    @Override
    MapCodec<? extends ContextlessValue> codec();

    @Override
    default double get(LootContext context, int upgradeRank)
    {
        return calculate(upgradeRank);
    }
}